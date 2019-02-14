package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.DbItem;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author hzhou
 */
@Data
public class Authority implements DbItem {

    @Id
    private String groupId;

    private String authority;
}
