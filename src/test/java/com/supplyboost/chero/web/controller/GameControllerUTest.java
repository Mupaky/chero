package com.supplyboost.chero.web.controller;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.game.character.service.NotificationEventService;
import com.supplyboost.chero.game.fight.service.FightService;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.shop.model.Shop;
import com.supplyboost.chero.game.shop.service.ShopService;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.dto.GameCharacterStatsResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(GameController.class)
public class GameControllerUTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ShopService shopService;

    @MockitoBean
    private CharacterService characterService;

    @MockitoBean
    private FightService fightService;

    @MockitoBean
    private NotificationEventService notificationEventService;

    private AuthenticationMetadata auth;

    @BeforeEach
    void setUp() {
        auth = new AuthenticationMetadata(
                UUID.randomUUID(), "testUser", "pass",
                UserRole.ROLE_ADMIN, true
        );
    }

    @Test
    void whenGetDashboard_thenReturnsDashboardView() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);
        gameCharacter.setOwner(user);

        when(userService.findByUsername(any())).thenReturn(user);
        when(characterService.getEnhancedStats(any())).thenReturn(
                Map.of(
                        StatType.STRENGTH, 101,
                        StatType.AGILITY, 15,
                        StatType.INTELLIGENCE, 27,
                        StatType.ENDURANCE, 15
                )
        );
        try (MockedStatic<DtoMapper> dtoMapperMock = mockStatic(DtoMapper.class)) {
            mockDtoMapper(gameCharacter, dtoMapperMock);

            mockMvc.perform(get("/game/dashboard").with(user(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard"))
                    .andExpect(model().attributeExists("profile_picture"))
                    .andExpect(model().attributeExists("gameCharacter"))
                    .andExpect(model().attributeExists("stats"))
                    .andExpect(model().attributeExists("enhancedStats"));
        }
    }

    @Test
    void whenGetTrainingHall_thenReturnsTrainStatsView() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);
        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);

        try (MockedStatic<DtoMapper> dtoMapperMock = mockStatic(DtoMapper.class)) {
            mockDtoMapper(gameCharacter, dtoMapperMock);

            mockMvc.perform(get("/game/train-stats").with(user(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("train-stats"))
                    .andExpect(model().attributeExists("gameCharacter"))
                    .andExpect(model().attributeExists("stats"));
        }
    }

    @Test
    void whenPostTrainStat_thenRedirectsToTrainStats() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);
        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(post("/game/train")
                        .with(csrf())
                        .with(user(auth))
                        .param("stat", "STRENGTH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/train-stats"));

        verify(characterService).trainStat(gameCharacter.getId(), StatType.STRENGTH);
    }

    @Test
    void whenGetUnderground_thenReturnsUndergroundView() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();

        User user = User.builder()
                .username("testUser")
                .profile_picture("profile.png")
                .gameCharacter(gameCharacter)
                .notifications(new ArrayList<>(List.of("Test notification 1", "Test notification 2")))
                .build();

        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);

        try (MockedStatic<DtoMapper> dtoMapperMock = mockStatic(DtoMapper.class)) {
            mockDtoMapper(gameCharacter, dtoMapperMock);

            mockMvc.perform(get("/game/underground").with(user(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("underground"))
                    .andExpect(model().attributeExists("notifications"))
                    .andExpect(model().attributeExists("gameCharacter"));
        }

        verify(userService).save(user);
    }

    @Test
    void whenPostFight_thenRedirectsToUnderground() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);
        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(post("/game/fight")
                        .param("difficulty", "EASY")
                        .with(user(auth))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/underground"));

        verify(fightService).handleFight(user, "EASY");
    }

    @Test
    void whenGetInventory_thenReturnsInventoryView() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);

        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);

        try (MockedStatic<DtoMapper> dtoMapperMock = mockStatic(DtoMapper.class)) {
            mockDtoMapper(gameCharacter, dtoMapperMock);

            mockMvc.perform(get("/game/inventory").with(user(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("inventory"))
                    .andExpect(model().attributeExists("wearings"))
                    .andExpect(model().attributeExists("inventory"))
                    .andExpect(model().attributeExists("gameCharacter"));
        }
    }

    @Test
    void whenPostEquipItem_thenRedirectsToInventory() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);

        gameCharacter.setOwner(user);

        UUID itemId = UUID.randomUUID();

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(post("/game/items/equip")
                        .param("itemId", itemId.toString())
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/inventory"));

        verify(characterService).equipItem(gameCharacter.getId(), itemId);
    }

    @Test
    void whenPostUnEquipItem_thenRedirectsToInventory() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);

        gameCharacter.setOwner(user);

        String slot = "WEAPON";

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(post("/game/items/unequip")
                        .param("slot", slot)
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/inventory"));

        verify(characterService).unEquipItem(gameCharacter.getId(), slot);
    }

    @Test
    void whenGetShop_thenReturnsShopView() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);

        gameCharacter.setOwner(user);

        when(userService.getById(any())).thenReturn(user);
        Shop shop = new Shop();
        shop.setItems(List.of());
        when(shopService.getShop()).thenReturn(shop);

        try (MockedStatic<DtoMapper> dtoMapperMock = mockStatic(DtoMapper.class)) {
            mockDtoMapper(gameCharacter, dtoMapperMock);

            mockMvc.perform(get("/game/shop").with(user(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("shop"))
                    .andExpect(model().attributeExists("shopItems"))
                    .andExpect(model().attributeExists("gameCharacter"));
        }
    }

    @Test
    void whenPostBuyItem_thenRedirectsToShop() throws Exception {
        GameCharacter gameCharacter = getGameCharacter();
        User user = getUser(gameCharacter);

        gameCharacter.setOwner(user);

        String itemId = "test-item-id";

        when(userService.getById(any())).thenReturn(user);
        when(shopService.buyItem(itemId)).thenReturn(mock(Item.class)); // Mock item return

        mockMvc.perform(post("/game/shop/buy")
                        .param("itemId", itemId)
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/shop"));

        verify(shopService).buyItem(itemId);
        verify(characterService).buyItem(eq(user.getGameCharacter().getId()), any(Item.class));
    }

    private User getUser(GameCharacter gameCharacter){
        return User.builder()
                .username("testUser")
                .profile_picture("profile.png")
                .gameCharacter(gameCharacter)
                .build();
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
