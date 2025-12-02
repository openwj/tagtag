package dev.tagtag.contract.iam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
  @NotBlank
  private String oldPassword;
  @NotBlank
  private String newPassword;
}

