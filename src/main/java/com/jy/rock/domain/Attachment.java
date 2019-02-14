package com.jy.rock.domain;

import com.jy.rock.bean.JsonFileVO;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 附件表
 * @author hzhou
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Attachment extends BaseDomainWithGuidKey {

    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件类别
     */
    private AttachmentType type;

    /**
     * 附件内容
     */
    private byte[] content;

    private String contentType;

    private String recorderType;

    private String recorderId;

    public Attachment(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public Attachment(@NotNull MultipartFile file) throws IOException {
        this.name = file.getOriginalFilename();
        this.content = file.getBytes();
        this.contentType = file.getContentType();
    }

    public Attachment(@NotNull JsonFileVO file) {
        this.setId( file.getId());
        this.name = file.getName();
        this.content = file.getContent();
        this.contentType = file.getContentType();
    }
}
