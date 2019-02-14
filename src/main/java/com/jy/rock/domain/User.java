package com.jy.rock.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "user_info")
public class User extends BaseDomainWithGuidKey {

    /**
     * 用户名，就是账号
     */
    @Column(updatable = false)
    private String username;

    /**
     * 用户姓名
     */
    private String fullName;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮件
     */
    private String email;

    /**
     * 密码
     */
    @Column(updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 帐号是否启用
     */
    private Boolean enable;

    /**
     * 帐号被锁定
     */
    private Boolean locked;

    /**
     * 登录的时候需要修改密码
     */
    @Column(updatable = false)
    private Boolean needChangePasswordWhenLogin;

    /**
     * 上次修改密码的时间
     */
    @Column(updatable = false)
    private LocalDateTime lastTimeChangePassword;

    /**
     * 过期时间
     */
    @Column(updatable = false)
    private LocalDateTime expiredTime;

    /**
     * 帐号创建时间，不允许更新
     */
    @Column(updatable = false)
    private LocalDateTime created;

    public User() {
        this.enable = true;
        this.locked = false;
        this.needChangePasswordWhenLogin = false;
        this.created = LocalDateTime.now();
    }
}
