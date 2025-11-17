package dev.tagtag.start;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 生成与校验 BCrypt 密码哈希的测试
 * 用法：
 * - 生成：mvn -DPLAINTEXT=password -Dtest=PasswordHashTest test
 * - 校验：mvn -DPLAINTEXT=password -DHASH=$2a$... -Dtest=PasswordHashTest test
 */
public class PasswordHashTest {

    /**
     * 生成 BCrypt 哈希并打印，同时自校验匹配结果
     */
    @Test
    public void generateAndSelfVerify() {
        String plain = System.getProperty("PLAINTEXT", "password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(plain);
        System.out.println("PLAINTEXT=" + plain);
        System.out.println("BCRYPT HASH=" + hash);
        boolean match = encoder.matches(plain, hash);
        System.out.println("SELF VERIFY MATCH=" + match);
        org.junit.jupiter.api.Assertions.assertTrue(match);
    }

    /**
     * 使用外部提供的哈希进行匹配校验（可选）
     */
    @Test
    public void verifyGivenHashIfProvided() {
        String plain = System.getProperty("PLAINTEXT", "password");
        String givenHash = System.getProperty("HASH");
        if (givenHash == null || givenHash.isEmpty()) {
            System.out.println("No HASH provided, skip external verification.");
            return;
        }
        // 兼容 {bcrypt} 前缀
        String normalized = givenHash.startsWith("{bcrypt}") ? givenHash.substring("{bcrypt}".length()) : givenHash;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean match = encoder.matches(plain, normalized);
        System.out.println("PLAINTEXT=" + plain);
        System.out.println("GIVEN HASH=" + givenHash);
        System.out.println("EXTERNAL VERIFY MATCH=" + match);
        org.junit.jupiter.api.Assertions.assertTrue(match, "Given hash does not match plaintext");
    }
}