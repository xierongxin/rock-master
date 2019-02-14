package com.jy.rock;

import com.jy.rock.core.SystemConfig;
import com.jy.rock.enums.Caches;
import com.xmgsd.lan.roadhog.bean.DataSourceSettingBO;
import com.xmgsd.lan.roadhog.bean.SqlSessionFactorySettingBO;
import com.xmgsd.lan.roadhog.mybatis.ConfigBuildHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author hzhou
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.jy.rock.dao")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableCaching(proxyTargetClass = true)
public class RockConfig {
    private static final String MAPPERS_LOCATION = "classpath:mappers/**/*.xml";

    @Autowired
    private SystemConfig systemConfig;

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
        String locationPattern;
        String profile = System.getProperty("profile.active");
        if ("dev".equals(profile)) {
            locationPattern = "application.dev.properties";
        } else {
            locationPattern = "file:config.properties";
        }
        return ConfigBuildHelper.buildPropertyPlaceholder(locationPattern);
    }

    @Bean
    public DataSource dataSource() {
        // 这里不能开启wall，因为druid不支持达梦数据库
        return ConfigBuildHelper.buildDataSource(new DataSourceSettingBO(
                MessageFormat.format("jdbc:mariadb://{0}/{1}", this.systemConfig.getDbUrl(), this.systemConfig.getDbName()),
                this.systemConfig.getDbUsername(),
                this.systemConfig.getDbPassword(),
                false
        ));
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        return ConfigBuildHelper.buildSqlSessionFactory(new SqlSessionFactorySettingBO(
                MAPPERS_LOCATION,
                "com.jy.rock.core",
                null,
                true,
                Style.camelhump,
                null,
                true
        ), dataSource);
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return ConfigBuildHelper.buildTransactionManager(dataSource);
    }

    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager(Caches.DICTIONARY_CODE, Caches.ATTACHMENT);
    }
}
