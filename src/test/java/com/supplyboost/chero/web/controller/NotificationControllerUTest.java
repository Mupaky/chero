package com.supplyboost.chero.web.controller;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.notification.service.NotificationService;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.dto.GameCharacterStatsResponse;
import com.supplyboost.chero.web.dto.NotificationPreferenceResponse;
import com.supplyboost.chero.web.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(NotificationController.class)
public class NotificationControllerUTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NotificationService notificationService;

    private AuthenticationMetadata auth;

    @BeforeEach
    void setUp() {
        auth = new AuthenticationMetadata(
                UUID.randomUUID(), "testUser", "pass",
                UserRole.ROLE_ADMIN, true
        );
    }

    @Test
    void whenGetNotifications_thenReturnsNotificationView() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = User.builder()
                .username("testUser")
                .profile_picture("profile.png")
                .gameCharacter(gameCharacter)
                .build();
        gameCharacter.setOwner(user);

        NotificationPreferenceResponse notificationPreferenceResponse = NotificationPreferenceResponse.builder()
                .enabled(true)
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(notificationService.getNotificationPreference(any(), anyBoolean(), any())).thenReturn(notificationPreferenceResponse);

        try (MockedStatic<DtoMapper> dtoMapperMock = mockStatic(DtoMapper.class)) {
            mockDtoMapper(gameCharacter, dtoMapperMock);

            mockMvc.perform(get("/notifications").with(user(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("notifications"))
                    .andExpect(model().attributeExists("gameCharacter"))
                    .andExpect(model().attributeExists("notificationPreference"));
        }
    }

    @Test
    void whenToggleNotification_thenRedirectsToNotifications() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = User.builder()
                .username("testUser")
                .profile_picture("profile.png")
                .gameCharacter(gameCharacter)
                .build();
        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);
        when(notificationService.togglePreferences(any(), any())).thenReturn(
                NotificationPreferenceResponse.builder().enabled(false).build()
        );

        mockMvc.perform(get("/notifications/preferences/toggle").with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).togglePreferences(user.getId(), user.getEmail());
    }

    private GameCharacter getGameCharacter(){
        EnumMap<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
        resources.put(ResourceType.GOLD, 500);

        return GameCharacter.builder()
                .experience(100)
                .expForNextLevelUp(200)
                .currentHealth(50)
                .health(100)
                .currentEnergy(30)
                .energy(50)
                .level(1)
                .nickName("testNickName")
                .stats(Stats.builder()
                        .stats(Map.of(
                                StatType.STRENGTH, 5,
                                StatType.AGILITY, 5,
                                StatType.INTELLIGENCE, 5,
                                StatType.ENDURANCE, 5
                        ))
                        .build())
                .resources(resources)
                .inventory(Inventory.builder().build())
                .build();
    }

    private void mockDtoMapper(GameCharacter gameCharacter, MockedStatic<DtoMapper> dtoMapperMock) {
        dtoMapperMock.when(() -> DtoMapper.mapToGameCharacterHeaderResponse(gameCharacter))
                .thenReturn(GameCharacterHeaderResponse.builder()
                        .nickName(gameCharacter.getNickName())
                        .experience(gameCharacter.getExperience())
                        .expForNextLevelUp(gameCharacter.getExpForNextLevelUp())
                        .health(gameCharacter.getHealth())
                        .currentHealth(gameCharacter.getCurrentHealth())
                        .energy(gameCharacter.getEnergy())
                        .currentEnergy(gameCharacter.getCurrentEnergy())
                        .level(gameCharacter.getLevel())
                        .build());

        dtoMapperMock.when(() -> DtoMapper.mapToGameCharacterStatsResponse(gameCharacter))
                .thenReturn(GameCharacterStatsResponse.builder()
                        .strength(5)
                        .agility(5)
                        .intelligence(5)
                        .endurance(5)
                        .build());
    }
}