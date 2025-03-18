package com.supplyboost.chero.web.dto;

import com.supplyboost.chero.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserEditRequest {

    @Size(max = 40, message = "Sorry the name can't be more that 40 symbols.")
    private String characterName;

    @Email
    private String email;

    private UserRole userRole;
}
