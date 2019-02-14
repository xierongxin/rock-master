package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "group_info")
public class Group extends BaseDomainWithGuidKey {

    private String name;

    private String code;

    @Column(updatable = false)
    private Boolean editable;

    public Group() {
        this.editable = true;
    }
}
