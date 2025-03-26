package com.supplyboost.chero.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {

    @Size(min = 6, message = "Username must be at least 6 symbols.")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 symbols.")
    private String password;

    @Email(message = "You should enter a valid email address.")
    private String email;


}
