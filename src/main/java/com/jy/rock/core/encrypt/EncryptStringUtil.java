package com.jy.rock.core.encrypt;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.jy.rock.core.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加解密工具类
 *
 * @author hzhou
 */
@Slf4j
public class EncryptStringUtil {

    private static final String PREFIX = "{GSDEncrypt}";

    private static SecretKeySpec key;

    private static SecretKeySpec getKey() throws NoSuchAlgorithmException {
        if (key == null) {

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            // 利用用户密码作为随机数初始化出128位的key生产者
            // 加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，
            // 只要种子相同，序列就一样，所以解密只要有password就行
            kgen.init(128, new SecureRandom(SystemConfig.CI_KEY.getBytes()));

            // 根据用户密码，生成一个密钥
            SecretKey secretKey = kgen.generateKey();

            // 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
            byte[] enCodeFormat = secretKey.getEncoded();

            // 转换为AES专用密钥
            key = new SecretKeySpec(enCodeFormat, "AES");
        }

        return key;
    }

    public static String encrypt(@Nullable EncryptString input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (input == null || Strings.isNullOrEmpty(input.getValue())) {
            return null;
        }

        if (input.getValue().startsWith(PREFIX)) {
            log.warn("field already encrypted, direct return. field value: {}", input);
            return input.getValue();
        }

        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");

        // 初始化为加密模式的密码器
        cipher.init(Cipher.ENCRYPT_MODE, getKey());

        byte[] byteContent = input.getValue().getBytes();

        // 加密
        byte[] result = cipher.doFinal(byteContent);

        String s = Base64.getEncoder().encodeToString(result);
        return PREFIX + s;
    }

    public static EncryptString decrypt(@Nullable String input) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (Strings.isNullOrEmpty(input)) {
            return null;
        }

        if (!input.startsWith(PREFIX)) {
            log.warn("field not encrypt, direct return. field value: {}", input);
            return new EncryptString(input);
        }

        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");

        // 初始化为加密模式的密码器
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        byte[] b = Base64.getDecoder().decode(input.substring(PREFIX.length()));
        byte[] result = cipher.doFinal(b);
        return new EncryptString(new String(result, Charsets.UTF_8));
    }
}
