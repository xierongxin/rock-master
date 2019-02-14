package com.jy.rock.core;

import com.google.common.base.Charsets;
import com.jy.rock.bean.UserWithGroupsVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author hzhou
 */
@Slf4j
public class ParamEncryptUtil {

    public static final String PREFIX = "TOKEN_";

    private static SecretKeySpec getKey(@NotNull String key) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(key.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();

        // 转换为AES专用密钥
        return new SecretKeySpec(enCodeFormat, "AES");
    }

    public static String encrypt(@NotNull UserWithGroupsVO user, @NotNull String input) throws Exception {
        String key = user.getUsername() + SystemConfig.CI_KEY;
        return encrypt(key, input);
    }

    public static String decrypt(@NotNull UserWithGroupsVO user, @NotNull String input) {
        String key = user.getUsername() + SystemConfig.CI_KEY;
        return decrypt(key, input);
    }

    public static String encrypt(@NotNull String key, @NotNull String input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
        byte[] byteContent = input.getBytes();

        // 加密
        byte[] result = cipher.doFinal(byteContent);

        String s = Base64.getUrlEncoder().encodeToString(result);
        return PREFIX + s;
    }

    public static String decrypt(@NotNull String key, @NotNull String input) {
        try {
            if (!input.startsWith(PREFIX)) {
                return input;
            }
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey(key));
            byte[] b = Base64.getUrlDecoder().decode(input.substring(PREFIX.length()));
            byte[] result = cipher.doFinal(b);
            return new String(result, Charsets.UTF_8);
        } catch (Exception e) {
            log.error("decrypt param error", e);
            return input;
        }
    }
}
