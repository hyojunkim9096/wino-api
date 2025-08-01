package com.wino.wino_api.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AES256Util {

        // 🔑 application.yml 또는 properties에서 불러올 키
        @Value("${aes.secret-key}")
        private String secretKeyFromConfig;

        private static String SECRET_KEY;
        private static final String ALGORITHM = "AES";

        @PostConstruct
        public void init() {
            // static 메서드에서도 사용할 수 있게 static 필드에 저장
            SECRET_KEY = secretKeyFromConfig;
        }

        /**
         * 🔐 암호화 (Encrypt)
         * @param plainText 암호화할 문자열
         * @return Base64로 인코딩된 암호문
         */
        public static String encrypt(String plainText) throws Exception {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        /**
         * 🔓 복호화 (Decrypt)
         * @param encryptedText 암호화된 Base64 문자열
         * @return 원본 평문 문자열
         */
        public static String decrypt(String encryptedText) throws Exception {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        }

}
