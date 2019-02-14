package com.jy.rock.bean;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zzh_1
 */
@Data
public class ResetMyPasswordVO {

    @NotBlank
    private String username;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String code;
}
