package dev.tagtag.contract.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 令牌传输对象，承载访问令牌与刷新令牌信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {

    /** 访问令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;

    /** 令牌类型，例如 Bearer */
    private String tokenType;

    /** 访问令牌过期秒数 */
    private long expiresIn;

    /**
     * 工厂方法：创建仅包含访问令牌的对象
     *
     * @param accessToken 访问令牌
     * @param expiresIn 过期秒数
     * @return TokenDTO
     */
    public static TokenDTO ofAccess(String accessToken, long expiresIn) {
        return TokenDTO.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}
