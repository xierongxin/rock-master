package com.jy.rock.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.core.SystemConfig;
import com.jy.rock.domain.AuditLog;
import com.xmgsd.lan.roadhog.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.time.LocalDateTime;

import static com.jy.rock.core.audit.AuditAspect.getClientIp;
import static com.jy.rock.core.audit.AuditAspect.getRequestType;

/**
 * 支持使用json进行认证
 *
 * @author hzhou
 */
@Slf4j
@Component
public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String USER_NAME_PARAM = "username";

    private static final String PASSWORD_PARAM = "password";

    private static final String CODE_PARAM = "code";

    private static final String AJAX_CONTENT_TYPE = "application/json";

    @Autowired
    private SystemConfig systemConfig;

    static boolean isJson(HttpServletRequest request) {
        String contentType = request.getHeader("Content-Type");
        if (contentType.contains(AJAX_CONTENT_TYPE)) {
            return true;
        }
        String ajaxHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(ajaxHeader);
    }

    public static String getSessionValidCode(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object temp = session == null ? null : session.getAttribute(SystemConfig.VALID_CODE_ATTRIBUTE);
        return temp == null ? null : temp.toString();
    }

    @Nullable
    private static String obtainParam(String paramName, HttpServletRequest request) {
        if (!isJson(request)) {
            request.setAttribute(paramName, request.getParameter(CODE_PARAM));
        }
        return request.getAttribute(paramName) == null ? null : request.getAttribute(paramName).toString();
    }

    private static void readParamFromJson(String paramName, HttpServletRequest request, JsonNode jsonNode) {
        JsonNode node = jsonNode.get(paramName);
        if (node != null) {
            request.setAttribute(paramName, node.asText());
        }
    }

    /**
     * 判断验证码是否正确
     */
    private void validateCode(HttpServletRequest request) {
        String code = obtainParam(CODE_PARAM, request);
        if (Strings.isNullOrEmpty(code)) {
            log.debug("no valid code");
            throw new BadCredentialsException("valid code empty");
        }

        String correctCode = getSessionValidCode(request);
        if (!code.equalsIgnoreCase(correctCode)) {
            log.debug("valid code error, want: {}, but: {}", correctCode, code);
            throw new BadCredentialsException("valid code error");
        }
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return obtainParam(USER_NAME_PARAM, request);
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return obtainParam(PASSWORD_PARAM, request);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuditLog al = new AuditLog();
        al.setUrl(request.getRequestURI());
        al.setIp(getClientIp(request));
        al.setRequestType(getRequestType(request));
        al.setMethod("attemptAuthentication");
        al.setMethodName("登录");
        al.setCreated(LocalDateTime.now());

        if (isJson(request)) {
            try {
                String s = CharStreams.toString(new InputStreamReader(request.getInputStream(), Charsets.UTF_8));
                JsonNode jsonNode = JSON.deserializeToNode(s);
                readParamFromJson(USER_NAME_PARAM, request, jsonNode);
                readParamFromJson(PASSWORD_PARAM, request, jsonNode);
                readParamFromJson(CODE_PARAM, request, jsonNode);
                al.setParams(MessageFormat.format("username: {0}, valid_code: {1}",
                        request.getAttribute(USER_NAME_PARAM), request.getAttribute(CODE_PARAM)));
            } catch (IOException e) {
                throw new BadCredentialsException("can not read json");
            }
        }

        request.setAttribute("al", al);

        if (systemConfig.isEnableValidCode()) {
            validateCode(request);
        }

        Authentication authentication = super.attemptAuthentication(request, response);
//        UserWithGroupsVO user = (UserWithGroupsVO) authentication.getPrincipal();
//        if (user.getNeedChangePasswordWhenLogin()) {
//            throw new CredentialsExpiredException("need change password");
//        }
        return authentication;
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Autowired
    @Override
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        super.setAuthenticationSuccessHandler(successHandler);
    }

    @Autowired
    @Override
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }
}
