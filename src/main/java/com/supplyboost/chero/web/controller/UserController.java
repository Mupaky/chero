package com.supplyboost.chero.web.controller;


import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.mapper.DtoMapper;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.UserEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CharacterService characterService;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    public UserController(UserService userService, CharacterService characterService) {
        this.userService = userService;
        this.characterService = characterService;
    }


    @GetMapping("/profile/edit")
    public ModelAndView showEditProfilePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());

        UserEditRequest userEditRequest = DtoMapper.mapToUserEditRequest(user);

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("userEditRequest", userEditRequest);
        modelAndView.addObject("userProfilePicture", user.getProfile_picture());
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);

        return modelAndView;
    }

    @PostMapping("/profile/update")
    public ModelAndView updateProfile(@ModelAttribute @Valid UserEditRequest userEditRequest,
                                      BindingResult result,
                                      @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                      @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile) {

        User user = userService.getById(authenticationMetadata.getUserId());

        userService.editUserDetails(user.getId(), userEditRequest, profilePictureFile);

        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("userEditRequest", userEditRequest);
            modelAndView.addObject("userProfilePicture",user.getProfile_picture());
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/users/profile/edit");
        modelAndView.addObject("userEditRequest", userEditRequest);
        modelAndView.addObject("userProfilePicture", user.getProfile_picture());
        modelAndView.addObject("user", user);

        return modelAndView;
    }



}
