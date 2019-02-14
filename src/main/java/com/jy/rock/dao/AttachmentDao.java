package com.jy.rock.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.jy.rock.bean.AttachmentSummaryVO;
import com.jy.rock.domain.Attachment;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.mybatis.mapper.weekend.Weekend;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
public interface AttachmentDao extends CurdMapper<Attachment, String> {

    /**
     * 根据附件关联的记录的Id和附件类别查询属于该记录的所有附件的ID编号
     *
     * @param recorderId     记录ID
     * @param attachmentType 附件类别
     * @return 属于该记录的所有附件的ID编号
     */
    List<AttachmentSummaryVO> getAttachmentsByRecorderIdAndAttachmentType(@Param("recorderId") String recorderId, @Param("attachmentType") AttachmentType attachmentType);

    /**
     * 插入之前会检查content，对于附件内容是空的记录不会插入
     *
     * @param recordList 要插入的记录
     * @return 插入成功的记录
     */
    @Override
    default int insert(@NotNull List<Attachment> recordList) {
        int result = 0;
        for (Attachment attachment : recordList) {
            if (attachment.getContent() != null && attachment.getContent().length > 0) {
                this.insert(attachment);
                result++;
            }
        }
        return result;
    }

    /**
     * 更新指定记录的附件，会自动比对出新增的附件和删除的附件
     *
     * @param recorderId     记录ID
     * @param attachmentType 附件类别
     * @param newAttachments 该记录本次传入的附件
     */
    default void updateRecorderAttachments(@NotNull String recorderId, @Nullable AttachmentType attachmentType, List<Attachment> newAttachments) {
        // 查询出该记录的附件
        Set<String> oldAttachmentIds = this.getAttachmentsByRecorderIdAndAttachmentType(recorderId, attachmentType)
                .stream().map(AttachmentSummaryVO::getId).collect(Collectors.toSet());

        // 如果附件的ID不为空，代表这个附件是存在在数据库中的
        Set<String> newAttachmentIds = newAttachments.stream().filter(i -> !Strings.isNullOrEmpty(i.getId()))
                .map(Attachment::getId).collect(Collectors.toSet());

        // 比对出删除掉的附件
        Sets.SetView<String> deleteAttachments = Sets.difference(oldAttachmentIds, newAttachmentIds);
        for (String attachment : deleteAttachments) {
            this.deleteByPrimaryKey(attachment);
        }

        // 对于ID为空的附件，插入这个附件
        List<Attachment> attachmentsToAdd = newAttachments.stream().filter(i -> Strings.isNullOrEmpty(i.getId())).collect(Collectors.toList());
        this.insert(attachmentsToAdd);
    }

    /**
     * 根据附件所属记录ID删除所有属于该记录的附件
     *
     * @param recorderId 记录ID
     */
    default void deleteByRecorderId(@NotNull String recorderId) {
        Weekend<Attachment> weekend = Weekend.of(Attachment.class);
        weekend.weekendCriteria().andEqualTo(Attachment::getRecorderId, recorderId);
        this.deleteByExample(weekend);
    }
}
