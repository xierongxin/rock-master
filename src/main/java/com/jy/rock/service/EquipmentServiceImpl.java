package com.jy.rock.service;

import com.jy.rock.bean.equipment.EquipmentQueryVO;
import com.jy.rock.bean.equipment.EquipmentVO;
import com.jy.rock.dao.AttachmentDao;
import com.jy.rock.dao.EquipmentDao;
import com.jy.rock.domain.Attachment;
import com.jy.rock.domain.Equipment;
import com.jy.rock.enums.AttachmentType;
import com.jy.rock.enums.EquipmentType;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class EquipmentServiceImpl extends BaseService<EquipmentDao>
        implements SimpleCurdViewService<String, EquipmentVO>, PaginationService<EquipmentQueryVO, EquipmentVO> {
    @Autowired
    private DictionaryCodeServiceImpl dictionaryCodeService;

    @Autowired
    private AttachmentDao attachmentDao;

    @Override
    public BasePaginationMapper<EquipmentVO> getPaginationMapper() {
        return this.getMapper();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EquipmentVO add(@NotNull EquipmentVO item) {
        Equipment equipment = item.toEquipment();
        this.getMapper().insert(equipment);

        List<Attachment> attachments = item.getFiles().stream().map(fvo -> {
            Attachment attachment = new Attachment(fvo);
            attachment.setRecorderId(equipment.getId());
            attachment.setRecorderType(Equipment.class.getSimpleName());
            attachment.setType(AttachmentType.equipmentFile);
            return attachment;
        }).collect(Collectors.toList());

        this.attachmentDao.insert(attachments);

        EquipmentType equipmentType = EquipmentType.valueOf(dictionaryCodeService.get(equipment.getTypeId()).getCode());
        switch (equipmentType) {
            case ups:
                break;
            default:
                throw new IllegalArgumentException("not support equipmentType: " + equipmentType);
        }

        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EquipmentVO update(@NotNull String id, @NotNull EquipmentVO item) {
        Equipment oldItem = this.getMapper().selectByPrimaryKey(id);
        if (oldItem == null) {
            throw new IllegalArgumentException("no entry with id: " + id);
        }
        Equipment newItem = item.toEquipment();

        oldItem.update(newItem);
        this.getMapper().updateByPrimaryKey(oldItem);

        List<Attachment> attachments = item.getFiles().stream().map(fvo -> {
            Attachment attachment = new Attachment(fvo);
            attachment.setRecorderId(id);
            attachment.setRecorderType(Equipment.class.getSimpleName());
            attachment.setType(AttachmentType.equipmentFile);
            return attachment;
        }).collect(Collectors.toList());
        this.attachmentDao.updateRecorderAttachments(id, AttachmentType.equipmentFile, attachments);

        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(@NotNull String id) {
        this.attachmentDao.deleteByRecorderId(id);
        this.getMapper().deleteByPrimaryKey(id);
    }
}
