package com.jy.rock.service;

import com.jy.rock.TestClassBase;
import com.jy.rock.bean.user.UserVO;
import com.jy.rock.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceImplTest extends TestClassBase {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void add() throws Exception {
        UserVO user = new UserVO();
        user.setUsername("admin");
        user.setPassword("1234@abcd");
        user.setFullName("管理员");
        this.userService.add(user);
    }

}
