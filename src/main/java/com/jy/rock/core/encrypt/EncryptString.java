package com.jy.rock.core.encrypt;

import java.io.Serializable;
import java.util.Objects;

/**
 * 代表这个字段对应数据库里的一个自动加密解密字段
 *
 * @author hzhou
 */
public class EncryptString implements Serializable {

    private String value;

    public EncryptString(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EncryptString) {
            return Objects.equals(((EncryptString) obj).getValue(), this.value);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
