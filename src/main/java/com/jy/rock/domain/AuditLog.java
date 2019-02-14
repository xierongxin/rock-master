package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * 审计日志
 *
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseDomainWithGuidKey {

    /**
     * 操作人账号
     */
    private String username;

    /**
     * 操作人姓名
     */
    private String fullName;

    /**
     * 访问的IP
     */
    private String ip;

    /**
     * 访问地址
     */
    private String url;

    /**
     * 请求的类别: GET, POST, DELETE 等
     */
    private String method;

    /**
     * 设置请求类型（json|普通请求）
     */
    private String requestType;

    /**
     * 访问参数，JSON字符串
     */
    private String params;

    /**
     * 是否访问成功
     */
    private Boolean success;

    /**
     * 日志细节
     */
    private String details;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 记录时间
     */
    @Column(updatable = false)
    private LocalDateTime created;
}
