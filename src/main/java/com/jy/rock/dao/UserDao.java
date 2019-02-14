package com.jy.rock.dao;

import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.bean.user.UserVO;
import com.jy.rock.domain.User;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hzhou
 */
@Repository
public interface UserDao extends CurdMapper<User, String> {

    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Nullable
    UserWithGroupsVO getWithGroupByUsername(@Param("username") String username);

    List<UserVO> findWithGroups();

    /**
     * 重置用户密码
     *
     * @param id                          用户id
     * @param newPassword                 新密码，必须是加密后的密码
     * @param lastTimeChangePasswordDate  最后一次修改密码的时间
     * @param needChangePasswordWhenLogin 下次登录的时候是否要求用户改密码
     */
    void resetPassword(@Param("id") String id,
                       @Param("newPassword") String newPassword,
                       @Param("lastTimeChangePasswordDate") LocalDateTime lastTimeChangePasswordDate,
                       @Param("needChangePasswordWhenLogin") boolean needChangePasswordWhenLogin);
}
