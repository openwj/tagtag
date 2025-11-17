package dev.tagtag.contract.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogoutRequest {
    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;
}