package dev.tagtag.start;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 使用 Spring Boot 官方内置 Delegating（默认 BCrypt）生成/校验哈希
 */
public class BcryptHashTest {

    /**
     * 生成哈希并打印，同时自校验匹配结果
     */
    @Test
    public void generateAndSelfVerify() {
        String plain = System.getProperty("PLAINTEXT", "Admin@123");
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hash = encoder.encode(plain);
        System.out.println("PLAINTEXT=" + plain);
        System.out.println("HASH=" + hash);
        boolean match = encoder.matches(plain, hash);
        System.out.println("MATCH=" + match);
        org.junit.jupiter.api.Assertions.assertTrue(match);
    }
}

