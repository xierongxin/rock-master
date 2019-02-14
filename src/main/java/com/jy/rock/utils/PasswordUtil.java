package com.jy.rock.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 *
 * @author hzhou
 */
public class PasswordUtil {

    /**
     * 获取加密后的密码
     *
     * @param password 明文密码
     * @return 密文密码
     */
    public static String encryptPassword(@NotNull String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public static boolean matches(@NotNull String rawPassword, @NotNull String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
