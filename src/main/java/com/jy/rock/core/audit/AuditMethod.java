package com.jy.rock.core.audit;

import java.lang.annotation.*;

/**
 * 升级方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AuditMethod {
    /**
     * 方法名称
     *
     * @return 方法名称
     */
    String name();
}
