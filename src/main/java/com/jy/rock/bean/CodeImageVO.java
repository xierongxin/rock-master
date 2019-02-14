package com.jy.rock.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * 图片验证码
 *
 * @author hzhou
 */
@Data
@AllArgsConstructor
public class CodeImageVO {
    private String code;

    private BufferedImage image;
}
