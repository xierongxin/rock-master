package com.jy.rock.core.audit;

import java.lang.annotation.*;

/**
 * 审计模块
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface AuditModule {
    /**
     * @return 模块名称
     */
    String moduleName();
}
