package com.jy.rock.dao;

import com.jy.rock.bean.equipment.EquipmentVO;
import com.jy.rock.bean.equipment.EquipmentQueryVO;
import com.jy.rock.domain.Equipment;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import com.xmgsd.lan.roadhog.mybatis.mappers.PaginationMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hzhou
 */
public interface EquipmentDao extends CurdMapper<Equipment, String>, PaginationMapper<EquipmentVO, EquipmentQueryVO> {
    @Override
    List<EquipmentVO> pagination(@Param("query") EquipmentQueryVO query);
}
