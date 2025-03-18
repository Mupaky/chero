package com.supplyboost.chero.web.controller;

import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.AdminUserEditRequest;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/manage-roles")
    public ModelAndView listUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                  @RequestParam(defaultValue = "0") int page) {
        User user = userService.findByUsername(authenticationMetadata.getUsername());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());

        int pageSize = 1;
        Page<User> userPage = userService.findAllUsers(PageRequest.of(page, pageSize));

        ModelAndView modelAndView = new ModelAndView("manage-roles");
        modelAndView.addObject("users", userPage.getContent());
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", userPage.getTotalPages());

        return modelAndView;
    }

    @GetMapping("/edit-user/{id}")
    public ModelAndView editUser(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 @PathVariable UUID id) {
        User user = userService.findByUsername(authenticationMetadata.getUsername());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());

        User userToEdit = userService.getById(id);
        AdminUserEditRequest editRequest = DtoMapper.mapToAdminUserEditRequest(userToEdit);

        ModelAndView modelAndView = new ModelAndView("admin-edit-user");
        modelAndView.addObject("adminUserEditRequest", editRequest);
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);
        modelAndView.addObject("userId", id);

        return modelAndView;
    }

    @PostMapping("/update-user")
    public ModelAndView updateUser(@ModelAttribute AdminUserEditRequest editRequest,
                                   @RequestParam UUID userId,
                                   RedirectAttributes redirectAttributes) {
        try {
            userService.adminUpdateUser(userId, editRequest);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user.");
        }
        return new ModelAndView("redirect:/admin/manage-roles");
    }


}
