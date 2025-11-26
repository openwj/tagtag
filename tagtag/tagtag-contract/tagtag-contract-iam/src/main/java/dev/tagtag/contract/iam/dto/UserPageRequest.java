package dev.tagtag.contract.iam.dto;

import dev.tagtag.common.model.PageQuery;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPageRequest {
  private UserQueryDTO query;
  @Valid
  private PageQuery page;
}

