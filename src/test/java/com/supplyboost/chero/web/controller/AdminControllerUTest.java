package com.supplyboost.chero.web.controller;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.AdminUserEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerUTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void whenListUsers_thenReturnManageRolesView() throws Exception {
        GameCharacter gameCharacter = GameCharacter.builder()
                .nickName("testNickName")
                .resources(new EnumMap<>(ResourceType.class))
                .build();
        gameCharacter.getResources().put(ResourceType.GOLD, 100);

        User user = User.builder()
                .username("testUser")
                .gameCharacter(gameCharacter)
                .build();

        Page<User> userPage = new PageImpl<>(List.of(user));

        AuthenticationMetadata authenticationMetadata = new AuthenticationMetadata(
                UUID.randomUUID(),
                "testUser",
                "testPassword",
                UserRole.ROLE_ADMIN,
                true
        );

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(userService.findAllUsers(any())).thenReturn(userPage);

        mockMvc.perform(get("/admin/manage-roles")
                        .with(user(authenticationMetadata)))
                .andExpect(status().isOk())
                .andExpect(view().name("manage-roles"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("gameCharacter"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"));
    }

    @Test
    void whenEditUser_thenReturnAdminEditUserView() throws Exception {
        UUID userId = UUID.randomUUID();

        GameCharacter userCharacter = GameCharacter.builder()
                .nickName("testNickName")
                .resources(new EnumMap<>(ResourceType.class))
                .build();
        userCharacter.getResources().put(ResourceType.GOLD, 100);

        GameCharacter userToEditCharacter = GameCharacter.builder()
                .nickName("editNickName")
                .resources(new EnumMap<>(ResourceType.class))
                .build();
        userToEditCharacter.getResources().put(ResourceType.GOLD, 100);

        User user = User.builder()
                .username("testUser")
                .gameCharacter(userCharacter)
                .build();

        User userToEdit = User.builder()
                .id(userId)
                .username("editUser")
                .gameCharacter(userToEditCharacter)
                .build();

        AuthenticationMetadata authenticationMetadata = new AuthenticationMetadata(
                UUID.randomUUID(), "testUser", "testPassword", UserRole.ROLE_ADMIN, true
        );

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(userService.getById(userId)).thenReturn(userToEdit);

        mockMvc.perform(get("/admin/edit-user/{id}", userId)
                        .with(user(authenticationMetadata)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-edit-user"))
                .andExpect(model().attributeExists("adminUserEditRequest"))
                .andExpect(model().attributeExists("gameCharacter"))
                .andExpect(model().attribute("userId", userId));
    }

    @Test
    void whenUpdateUser_thenRedirectToManageRolesWithSuccess() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/admin/update-user")
                        .with(csrf())
                        .with(authentication(new TestingAuthenticationToken(
                                new AuthenticationMetadata(UUID.randomUUID(), "testUser", "testPassword", UserRole.ROLE_ADMIN, true),
                                null,
                                "ROLE_ADMIN"
                        )))
                        .param("userId", userId.toString())
                        .flashAttr("adminUserEditRequest", AdminUserEditRequest.builder().build()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/manage-roles"));

        verify(userService).adminUpdateUser(eq(userId), any(AdminUserEditRequest.class));
    }
}
