package com.jy.rock.security;

import com.google.common.base.Strings;
import com.jy.rock.core.SystemConfig;
import com.jy.rock.domain.AuditLog;
import com.jy.rock.service.AuditLogServiceImpl;
import com.xmgsd.lan.roadhog.bean.SimpleResponseVO;
import com.xmgsd.lan.roadhog.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;


/**
 * 配置spring security
 *
 * @author hzhou
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
@EnableWebSecurity
@Slf4j
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * 允许匿名访问的URL
     */
    private static final String[] ANONYMOUS_URLS = new String[]{
            "/",
            "/web_settings",
            "/dictionary_code",
            "/group_manager",
            "/code_image",
            "/reset_my_password",
            "/public/*"
    };

    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private MyUserDetailsServiceImpl myUserDetailsService;

    @Autowired
    private AuditLogServiceImpl auditLogService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder delegatingPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return delegatingPasswordEncoder;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices(systemConfig.getCikey(), myUserDetailsService);
        services.setAlwaysRemember(true);
        return services;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            if (MyAuthenticationFilter.isJson(request)) {
                AuditLog al = (AuditLog) request.getAttribute("al");
                auditLogService.add(al);

                response.addHeader("Content-Type", "application/json;charset=UTF-8;");
                response.getWriter().write(JSON.serialize(new SimpleResponseVO(true)));
                response.getWriter().flush();
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new MyAuthenticationFailureHandler(auditLogService);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(ANONYMOUS_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
//                .formLogin()
//                .loginPage("/login")
//                .and()
                .csrf()
                .disable()
                .logout().logoutUrl("/logout")
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        MyLoginConfigurer<HttpSecurity> c = new MyLoginConfigurer<>();
        http.apply(c);

        if (this.systemConfig.isEnableRememberMe()) {
            if (Strings.isNullOrEmpty(this.systemConfig.getCikey())) {
                throw new IllegalArgumentException("when enableRememberMe, you must set cikey");
            }
            http.rememberMe().rememberMeServices(this.rememberMeServices()).key(systemConfig.getCikey());
            log.warn("remember me service enabled");
        }

        if (systemConfig.getConcurrencyLogin() != null && systemConfig.getConcurrencyLogin() > 0) {
            log.warn("concurrency login control enabled, allow: {}", systemConfig.getConcurrencyLogin());
            http.sessionManagement()
                    .maximumSessions(systemConfig.getConcurrencyLogin())
                    .maxSessionsPreventsLogin(true);
        }
    }
}
