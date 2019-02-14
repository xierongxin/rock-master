package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.DbItem;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author hzhou
 */
@Data
public class GroupMember implements DbItem {

    @Id
    private String groupId;

    @Id
    private String userId;
}
