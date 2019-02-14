package com.jy.rock.core;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hzhou
 * 系统配置
 */
@Component
@Getter
public class SystemConfig {

    public final static String VALID_CODE_ATTRIBUTE = "validCode";

    public static String CI_KEY;

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.name}")
    private String dbName;

    /**
     * 加密cookie的密钥，启用enableRememberMe的话，必须设置这个值
     */
    private String cikey;

    /**
     * 是否启用“记住我”功能
     */
    @Value("${auth.enableRememberMe:false}")
    private boolean enableRememberMe;

    /**
     * 登录的时候是否需要验证码
     */
    @Value("${auth.enableValidCode:false}")
    private boolean enableValidCode;

    /**
     * 允许并发登录的用户数量，如果设置为 null 或者 小于 1，代表不限制
     */
    @Value("${auth.concurrency:0}")
    private Integer concurrencyLogin;

    @Value("${auth.cikey}")
    public void setCikey(String cikey) {
        this.cikey = cikey;
        CI_KEY = cikey;
    }
}
