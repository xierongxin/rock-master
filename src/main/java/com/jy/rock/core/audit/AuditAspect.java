package com.jy.rock.core.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.domain.AuditLog;
import com.jy.rock.service.AuditLogServiceImpl;
import com.xmgsd.lan.roadhog.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author hzhou
 */
@Aspect
@Component
@Slf4j(topic = "audit")
public class AuditAspect {

    private static final String UNKNOWN_IP = "unknown";

    @Autowired
    private AuditLogServiceImpl auditLogService;

    /**
     * 获取客户端ip地址
     *
     * @param request 请求
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (Strings.isNullOrEmpty(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (Strings.isNullOrEmpty(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (Strings.isNullOrEmpty(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ip.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }

    /**
     * 获取请求类型，如：ajax，普通请求等
     *
     * @param request 请求
     * @return 请求类型
     */
    public static String getRequestType(HttpServletRequest request) {
        return request.getHeader("X-Requested-With");
    }

    private static AuditLog initAuditLog(@NotNull HttpServletRequest request, @Nullable UserWithGroupsVO user) throws JsonProcessingException {
        AuditLog al = new AuditLog();

        if (user != null) {
            al.setUsername(user.getUsername());
            al.setFullName(user.getFullName());
        }

        al.setUrl(request.getRequestURI());
        if (!request.getParameterMap().isEmpty()) {
            al.setParams(JSON.serialize(request.getParameterMap()));
        }
        al.setIp(getClientIp(request));
        al.setMethod(request.getMethod());
        al.setRequestType(getRequestType(request));
        al.setCreated(LocalDateTime.now());

        return al;
    }

    @Around("execution(public * AbstractAuditCurdController+.* (..)) &&" +
            "(@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 设置当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserWithGroupsVO user = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            user = (UserWithGroupsVO) authentication.getPrincipal();
        }
        AuditLog al = initAuditLog(request, user);

        // 设置模块名称，需要在controller类上使用注解：@AuditModule，如果没有的话就取类名
        AuditModule auditModuleAnnotation = joinPoint.getTarget().getClass().getAnnotation(AuditModule.class);
        if (auditModuleAnnotation == null) {
            al.setModuleName(joinPoint.getTarget().getClass().getSimpleName());
        } else {
            al.setModuleName(auditModuleAnnotation.moduleName());
        }

        // 设置方法名称，顺序如下：
        // 1. 方法的注解 @AuditMethod
        // 2. 方法的英文名
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AuditMethod auditMethodAnnotation = method.getAnnotation(AuditMethod.class);
        if (auditMethodAnnotation != null) {
            al.setMethodName(auditMethodAnnotation.name());
        } else {
            al.setMethodName(method.getName());
        }

        // 遍历方法的所有参数，把类型为 AuditLog 的参数赋值为当前的al
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof AuditLog) {
                args[i] = al;
                break;
            }
        }

        try {
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            al.setSuccess(false);
            al.setDetails(Throwables.getStackTraceAsString(e));
            throw e;
        } finally {
            this.auditLogService.add(al);
            log.info(JSON.serialize(al));
        }
    }
}
