package com.jy.rock.service;

import com.jy.rock.bean.group.GroupVO;
import com.jy.rock.dao.GroupDao;
import com.jy.rock.dao.GroupMemberDao;
import com.jy.rock.domain.Group;
import com.jy.rock.domain.GroupMember;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zzh_1
 */
@Service
@Slf4j
public class GroupServiceImpl extends BaseService<GroupDao> implements SimpleCurdViewService<String, GroupVO> {

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Override
    public List<GroupVO> list() {
        return this.getMapper().findWithUsers();
    }

    @Override
    public GroupVO add(@NotNull GroupVO item) throws Exception {
        Group group = item.toGroup();
        this.getMapper().insert(group);

        this.groupMemberDao.insertRelations(
                GroupMember::getGroupId,
                group.getId(),
                GroupMember::getUserId,
                item.getUsers().stream().map(IdNameEntry::getId).collect(Collectors.toList())
        );
        return item;
    }

    @Override
    public GroupVO update(@NotNull String id, @NotNull GroupVO item) throws Exception {
        Group oldItem = this.getMapper().selectByPrimaryKey(id);
        if (oldItem == null) {
            throw new IllegalArgumentException("no entry with id: " + id);
        }

        if (oldItem.getEditable()) {
            Group newItem = item.toGroup();
            oldItem.update(newItem);
            this.getMapper().updateByPrimaryKey(oldItem);
        }

        this.groupMemberDao.updateRelations(
                GroupMember::getGroupId,
                id,
                GroupMember::getUserId,
                item.getUsers().stream().map(IdNameEntry::getId).collect(Collectors.toList())
        );
        return item;
    }
}
