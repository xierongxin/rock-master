package com.jy.rock.bean.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jy.rock.domain.User;
import com.xmgsd.lan.roadhog.bean.FormData;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zzh_1
 */
@Data
public class UserVO implements FormData {

    private String id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String fullName;

    private String phone;

    private String email;

    private Boolean enable;

    private Boolean locked;

    private List<IdNameEntry> groups;

    public UserVO() {
        this.groups = new ArrayList<>();
    }

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
