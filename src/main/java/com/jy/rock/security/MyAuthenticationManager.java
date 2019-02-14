package com.jy.rock.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 自定义密码校验
 *
 * @author hzhou
 */
@Component
public class MyAuthenticationManager implements AuthenticationManager {

    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getPrincipal() == null || "".equals(authentication.getPrincipal().toString())) {
            throw new BadCredentialsException("account name is empty");
        }

        if (authentication.getCredentials() == null || "".equals(authentication.getCredentials().toString())) {
            throw new BadCredentialsException("password is empty");
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        if (userDetails == null || !Objects.equals(userDetails.getPassword(), authentication.getCredentials().toString())) {
            throw new BadCredentialsException("account name or password error");
        }

        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), userDetails.getAuthorities());
    }
}
