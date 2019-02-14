package com.jy.rock.bean.equipment;

import com.jy.rock.bean.JsonFileVO;
import com.jy.rock.domain.Equipment;
import com.xmgsd.lan.roadhog.bean.FormData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zzh_1
 */
@Data
public class EquipmentVO implements FormData {
    private String id;

    private String name;

    private String typeId;

    private String modelName;

    private String brandId;

    private String modelId;

    private String owner;

    private String serialNumber;

    private Date manufactureDate;

    private List<JsonFileVO> files;

    public EquipmentVO() {
        this.files = new ArrayList<>();
    }

    public Equipment toEquipment() {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(this, equipment);
        return equipment;
    }
}
