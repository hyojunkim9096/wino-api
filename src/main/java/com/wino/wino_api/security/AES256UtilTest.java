package com.wino.wino_api.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AES256UtilTest implements CommandLineRunner {

    private final AES256Util aes256Util;

    public AES256UtilTest(AES256Util aes256Util) {
        this.aes256Util = aes256Util;
    }

    public static void main(String[] args) {
        SpringApplication.run(AES256UtilTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String plain = "900906-1234567";

        String encrypted = aes256Util.encrypt(plain);
        String decrypted = aes256Util.decrypt(encrypted);

        System.out.println("🔐 암호화: " + encrypted);
        System.out.println("🔓 복호화: " + decrypted);
    }
}
