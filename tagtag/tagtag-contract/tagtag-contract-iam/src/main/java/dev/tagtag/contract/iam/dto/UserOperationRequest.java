package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOperationRequest {
  private Long id;
  private List<Long> ids;
  private Integer status;
  private String password;
  private List<Long> userIds;
  private List<Long> roleIds;
}

