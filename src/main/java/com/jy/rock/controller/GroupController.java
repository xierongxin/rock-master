package com.jy.rock.controller;

import com.jy.rock.dao.GroupDao;
import com.jy.rock.service.GroupServiceImpl;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.web.controller.SimpleCurdViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zzh_1
 */
@RestController
@RequestMapping("/group")
public class GroupController extends BaseController<GroupServiceImpl> implements SimpleCurdViewController<GroupServiceImpl> {

    @Autowired
    private GroupDao groupDao;

    @GetMapping("/list_options")
    public List<IdNameEntry> getGroupOptions() {
        return this.groupDao.selectAll().stream().map(i -> new IdNameEntry(i.getId(), i.getName())).collect(Collectors.toList());
    }
}
