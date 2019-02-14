package com.jy.rock.bean;

import com.jy.rock.enums.AttachmentType;
import lombok.Data;

/**
 * @author hzhou
 */
@Data
public class AttachmentSummaryVO {
    private String id;

    private String name;

    private AttachmentType type;

    private String token;
}
