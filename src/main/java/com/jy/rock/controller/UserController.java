package com.jy.rock.controller;

import com.jy.rock.bean.user.UserVO;
import com.jy.rock.service.UserServiceImpl;
import com.xmgsd.lan.roadhog.bean.SimpleResponseVO;
import com.xmgsd.lan.roadhog.web.controller.SimpleCurdViewController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzh_1
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserServiceImpl> implements SimpleCurdViewController<UserServiceImpl> {

    @GetMapping("/list_options")
    public List<UserVO> usersForList() {
        return this.getService().list();
    }

    @PutMapping("/reset_password/{id}")
    public SimpleResponseVO resetPassword(@PathVariable String id) {
        String newPassword = this.getService().resetPassword(id);
        return new SimpleResponseVO(true, newPassword, HttpStatus.OK.value());
    }
}
