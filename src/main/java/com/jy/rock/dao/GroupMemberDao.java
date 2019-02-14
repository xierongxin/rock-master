package com.jy.rock.dao;

import com.jy.rock.domain.GroupMember;
import com.xmgsd.lan.roadhog.mybatis.mappers.RelationMapper;
import org.springframework.stereotype.Repository;

/**
 * @author hzhou
 */
@Repository
public interface GroupMemberDao extends RelationMapper<GroupMember, String> {
}
