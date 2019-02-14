package com.jy.rock.service;

import com.jy.rock.bean.JsonFileVO;
import com.jy.rock.dao.AttachmentDao;
import com.jy.rock.dao.EquipmentModelDao;
import com.jy.rock.domain.Attachment;
import com.jy.rock.domain.EquipmentModel;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class EquipmentModelServiceImpl extends BaseService<EquipmentModelDao> implements SimpleCurdViewService<String, EquipmentModel> {

    @Autowired
    private AttachmentDao attachmentDao;

    private static List<Attachment> generateAttachments(@NotNull String recorderId, @Nullable List<JsonFileVO> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }

        return files.stream().map(fvo -> {
            Attachment attachment = new Attachment(fvo);
            attachment.setId(fvo.getId());
            attachment.setType(AttachmentType.instructions);
            attachment.setRecorderType(EquipmentModel.class.getSimpleName());
            attachment.setRecorderId(recorderId);
            return attachment;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(EquipmentModel em, List<JsonFileVO> files) {
        this.getMapper().insert(em);
        List<Attachment> attachments = generateAttachments(em.getId(), files);
        this.attachmentDao.insert(attachments);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(String id, EquipmentModel em, List<JsonFileVO> files) throws Exception {
        this.update(id, em);
        List<Attachment> attachments = generateAttachments(id, files);
        this.attachmentDao.updateRecorderAttachments(id, null, attachments);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(@NotNull String id) {
        this.attachmentDao.deleteByRecorderId(id);
        this.getMapper().deleteByPrimaryKey(id);
    }
}
