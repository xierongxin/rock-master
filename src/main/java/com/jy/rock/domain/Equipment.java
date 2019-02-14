package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomain;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Equipment extends BaseDomainWithGuidKey {

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备类别编号
     */
    @Column(updatable = false)
    private String typeId;

    /**
     * 型号编号
     */
    private String modelId;

    /**
     * 所有权，关联到客户表，如果为NULL，表示所有权是公司的
     */
    private String owner;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * 出厂日期
     */
    private Date manufactureDate;

    @Override
    public <T extends BaseDomain> void update(T source) {
        super.update(source, "typeId");
    }
}
