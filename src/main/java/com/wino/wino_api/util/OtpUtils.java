package com.wino.wino_api.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class OtpUtils {
    private static final SecureRandom RND = new SecureRandom();
    private OtpUtils(){}

    public static String generate6digit() {
        return String.format("%06d", RND.nextInt(1_000_000));
    }

    public static String sha256Hex(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
