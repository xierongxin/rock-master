package com.jy.rock.service;

import com.jy.rock.bean.ResetMyPasswordVO;
import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.bean.user.UserVO;
import com.jy.rock.dao.GroupMemberDao;
import com.jy.rock.dao.UserDao;
import com.jy.rock.domain.GroupMember;
import com.jy.rock.domain.User;
import com.jy.rock.utils.PasswordUtil;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Service
public class UserServiceImpl extends BaseService<UserDao> implements SimpleCurdViewService<String, UserVO> {

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Override
    public List<UserVO> list() {
        return this.getMapper().findWithGroups();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVO add(@NotNull UserVO item) throws Exception {
        User user = item.toUser();

        String s = PasswordUtil.encryptPassword(user.getPassword());
        user.setPassword(s);
        this.getMapper().insert(user);

        this.groupMemberDao.insertRelations(
                GroupMember::getUserId,
                user.getId(),
                GroupMember::getGroupId,
                item.getGroups().stream().map(IdNameEntry::getId).collect(Collectors.toList())
        );

        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVO update(@NotNull String id, @NotNull UserVO item) throws Exception {
        User oldItem = this.getMapper().selectByPrimaryKey(id);
        if (oldItem == null) {
            throw new IllegalArgumentException("no entity with id: " + id);
        }

        User user = item.toUser();
        oldItem.update(user);
        this.getMapper().updateByPrimaryKey(oldItem);

        this.groupMemberDao.updateRelations(
                GroupMember::getUserId,
                id,
                GroupMember::getGroupId,
                item.getGroups().stream().map(IdNameEntry::getId).collect(Collectors.toList())
        );

        return item;
    }

    @Nullable
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserWithGroupsVO getByUsername(@NotNull String username) {
        return this.getMapper().getWithGroupByUsername(username);
    }

    /**
     * 重置一个用户的密码，逻辑如下：
     * 1. 随机生成一个密码
     * 2. 修改该用户密码
     * 3. 修改该用户的needChangePasswordWhenLogin = true，要求用户下次登录的时候输入新的密码
     *
     * @param id 用户id
     * @return 新密码
     */
    public String resetPassword(@NotNull String id) {
        String newPassword = RandomStringUtils.random(8, true, true);
        boolean exists = this.getMapper().existsWithPrimaryKey(id);
        if (!exists) {
            throw new IllegalArgumentException("no entry with id: " + id);
        }
        this.getMapper().resetPassword(id, PasswordUtil.encryptPassword(newPassword), LocalDateTime.now(), true);
        return newPassword;
    }

    /**
     * 用户修改自己的密码
     *
     * @param resetMyPasswordVO 相关信息
     */
    public void resetMyPassword(@NotNull ResetMyPasswordVO resetMyPasswordVO) {
        UserWithGroupsVO user = this.getMapper().getWithGroupByUsername(resetMyPasswordVO.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("no username: " + resetMyPasswordVO.getUsername());
        }
        boolean match = PasswordUtil.matches(resetMyPasswordVO.getOldPassword(), user.getPassword());
        if (!match) {
            throw new IllegalArgumentException("密码错误");
        }
        this.getMapper().resetPassword(user.getId(), PasswordUtil.encryptPassword(resetMyPasswordVO.getNewPassword()), LocalDateTime.now(), false);
    }
}
