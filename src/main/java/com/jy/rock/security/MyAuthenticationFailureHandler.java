package com.jy.rock.security;

import com.google.common.base.Charsets;
import com.jy.rock.domain.AuditLog;
import com.jy.rock.service.AuditLogServiceImpl;
import com.xmgsd.lan.roadhog.bean.SimpleResponseVO;
import com.xmgsd.lan.roadhog.utils.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 身份认证错误处理器
 *
 * @author hzhou
 */
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private AuditLogServiceImpl auditLogService;

    MyAuthenticationFailureHandler(AuditLogServiceImpl auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage = "未知错误";

        if (exception.getMessage().contains("account name is empty")) {
            errorMessage = "用户名不能为空";
        } else if (exception.getMessage().contains("password is empty")) {
            errorMessage = "密码不能为空";
        } else if (exception.getMessage().contains("valid code error") || exception.getMessage().contains("no valid code")) {
            errorMessage = "验证码错误";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "用户名或密码错误";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "密码已过期";
        } else if (exception instanceof DisabledException
                || exception instanceof LockedException
                || exception instanceof AccountExpiredException
                || exception instanceof SessionAuthenticationException
        ) {
            errorMessage = exception.getMessage();
        }

        String responseMessage = MyAuthenticationFilter.isJson(request)
                ? JSON.serialize(new SimpleResponseVO(false, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value()))
                : errorMessage;

        AuditLog al = (AuditLog) request.getAttribute("al");
        al.setSuccess(false);
        al.setDetails(errorMessage);
        auditLogService.add(al);

        response.setHeader("Content-type", "text/xml;charset=UTF-8");
        response.setCharacterEncoding(Charsets.UTF_8.name());
        response.getWriter().write(responseMessage);
        response.getWriter().flush();
    }
}
