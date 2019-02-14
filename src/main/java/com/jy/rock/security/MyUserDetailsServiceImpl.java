package com.jy.rock.security;

import com.google.common.base.Strings;
import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 负责从数据库载入用户
 *
 * @author hzhou
 */
@Component
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Strings.isNullOrEmpty(username)) {
            throw new UsernameNotFoundException("username is null");
        }

        UserWithGroupsVO user = this.userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("no user with account: " + username);
        }

        return user;
    }
}
