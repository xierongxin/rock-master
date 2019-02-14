package com.jy.rock.bean.group;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.jy.rock.domain.Group;
import com.xmgsd.lan.roadhog.bean.FormData;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzh_1
 */
@Data
public class GroupVO implements FormData {

    private String id;

    private String code;

    private String name;

    private Boolean editable;

    private List<IdNameEntry> users;

    public GroupVO() {
        this.users = new ArrayList<>();
    }

    public Group toGroup() {
        Group group = new Group();
        BeanUtils.copyProperties(this, group);
        return group;
    }
}
