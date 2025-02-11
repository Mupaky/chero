package com.supplyboost.chero.mapper;

import com.supplyboost.chero.dto.UserEditRequest;
import com.supplyboost.chero.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static UserEditRequest mapToUserEditRequest(User user){
        return UserEditRequest.builder()
                .country(user.getCountry())
                .profilePicture(user.getProfile_picture())
                .email(user.getEmail())
                .build();
    }
}
