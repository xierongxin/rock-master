package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DictionaryCode extends BaseDomainWithGuidKey {

    private String code;

    private String name;

    private String parentId;

    private Integer index;

    private String remark;

    @Column(updatable = false)
    private Boolean editable;

    /**
     * 备用字段1
     */
    private String f1;

    private String f2;

    private String f3;

    private String f4;

    private String f5;

    public DictionaryCode() {
        this.editable = true;
    }
}
