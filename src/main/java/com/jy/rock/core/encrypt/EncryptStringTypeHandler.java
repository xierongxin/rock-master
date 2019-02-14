package com.jy.rock.core.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * 自动加解密字段类型的逻辑实现
 *
 * @author hzhou
 */
@SuppressWarnings("unused")
@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes(EncryptString.class)
@Slf4j
public class EncryptStringTypeHandler extends BaseTypeHandler<EncryptString> {

    public EncryptStringTypeHandler() {
        log.info("EncryptStringTypeHandler load ...");
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EncryptString parameter, JdbcType jdbcType) throws SQLException {
        try {
            String encrypt = EncryptStringUtil.encrypt(parameter);
            ps.setString(i, encrypt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            String errorMessage = MessageFormat.format("encrypt {0} failed", parameter.getValue());
            throw new SQLException(errorMessage, e);
        }
    }

    @Override
    public EncryptString getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        try {
            return EncryptStringUtil.decrypt(value);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            String errorMessage = MessageFormat.format("decrypt column {0}({1}) failed", columnName, value);
            throw new SQLException(errorMessage, e);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public EncryptString getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        try {
            return EncryptStringUtil.decrypt(value);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            String errorMessage = MessageFormat.format("decrypt columnIndex {0}({1}) failed", columnIndex, value);
            throw new SQLException(errorMessage, e);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public EncryptString getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        try {
            return EncryptStringUtil.decrypt(value);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            String errorMessage = MessageFormat.format("decrypt columnIndex {0}({1}) failed", columnIndex, value);
            throw new SQLException(errorMessage, e);
        }
    }
}
