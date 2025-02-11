package com.supplyboost.chero.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UserEditRequest {


    @Size(max = 40, message = "Sorry the name can't be more that 40 symbols.")
    private String fullName;

    @Size(max = 56, message = "There is no Country with that long name except UK = 56 symbols.")
    private String country;

    @Email
    private String email;

    @URL
    private String profilePicture;
}
