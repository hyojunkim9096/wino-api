package com.wino.wino_api.config;

import com.wino.wino_api.security.AES256Util;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * AES256Util을 이용해 String 필드를 자동 암·복호화하는 JPA AttributeConverter
 */
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return AES256Util.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("암호화 오류", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return AES256Util.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("복호화 오류", e);
        }
    }
}
