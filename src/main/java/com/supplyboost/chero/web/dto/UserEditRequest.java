package com.supplyboost.chero.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserEditRequest {


    @Size(max = 40, message = "Sorry the name can't be more that 40 symbols.")
    private String characterName;

    @Email
    private String email;

    private String profilePicture;
}
