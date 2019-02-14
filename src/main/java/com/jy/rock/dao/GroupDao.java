package com.jy.rock.dao;

import com.jy.rock.bean.group.GroupVO;
import com.jy.rock.domain.Group;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hzhou
 */
@Repository
public interface GroupDao extends CurdMapper<Group, String> {

    List<GroupVO> findWithUsers();
}
