package com.jy.rock.domain;

import lombok.Data;

import javax.persistence.Id;

/**
 * @author hzhou
 */
@Data
public class UPS implements EquipmentExtend {

    /**
     * 设备编号，主键，也是外键，关联到 Equipment 表
     */
    @Id
    private String equipmentId;

}
