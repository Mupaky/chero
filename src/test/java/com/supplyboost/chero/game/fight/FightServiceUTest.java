package com.supplyboost.chero.game.fight;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.model.Wearings;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.game.character.service.NotificationEventService;
import com.supplyboost.chero.game.fight.service.FightService;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FightServiceUTest {

    @Mock
    private CharacterService characterService;

    @Mock
    private NotificationEventService notificationEventService;

    @Spy
    @InjectMocks
    private FightService fightService;

    @Test
    void givenCharacterWithoutEnergy_whenHandleFight_thenNotifyOutOfEnergy() {
        User user = getUserWithCharacter(1, 100, 0); // level, health, energy

        fightService.handleFight(user, "easy");

        verify(notificationEventService).notify(user, "Out of energy. Please rest!");
    }

    @Test
    void givenCharacterWithoutHealth_whenHandleFight_thenNotifyOutOfHealth() {
        User user = getUserWithCharacter(1, 0, 5);

        fightService.handleFight(user, "easy");

        verify(notificationEventService).notify(user, "You are hurt and have no health. Please rest!");
    }

    @Test
    void givenVictoryAndLevelUp_whenHandleFight_thenApplyRewardsAndNotify() {
        User user = getUserWithCharacter(1, 100, 5);
        GameCharacter character = user.getGameCharacter();
        character.setExperience(200);
        character.setExpForNextLevelUp(100);

        doReturn(100).when(fightService).getWinChance("easy");
        doReturn(Map.of("Experience", 150, "Gold", 500)).when(fightService).applyVictoryRewards("easy");

        fightService.handleFight(user, "easy");

        verify(characterService).addExperience(character, 150);
        verify(characterService).addResourceAmount(character.getId(), ResourceType.GOLD, 500);
        verify(notificationEventService).notify(user, "You won the fight against [%s] monster!".formatted("easy"));
    }

    @Test
    void givenDefeat_whenHandleFight_thenApplyPenaltiesAndNotify() {
        User user = getUserWithCharacter(1, 100, 5);

        doReturn(0).when(fightService).getWinChance("hard");

        fightService.handleFight(user, "hard");

        verify(fightService).applyDefeatPenalties(user.getGameCharacter(), "hard");
        verify(characterService).save(user.getGameCharacter());
        verify(notificationEventService).notify(user, "You lost the fight. Try again!");
    }

    @Test
    void givenDifficultyEasy_whenGetWinChance_thenReturn70() {
        int chance = fightService.getWinChance("EASY");
        assertEquals(70, chance);
    }

    @Test
    void givenDifficultyMedium_whenGetWinChance_thenReturn50() {
        int chance = fightService.getWinChance("MEDIUM");
        assertEquals(50, chance);
    }

    @Test
    void givenDifficultyHard_whenGetWinChance_thenReturn40() {
        int chance = fightService.getWinChance("HARD");
        assertEquals(40, chance);
    }

    @Test
    void givenUnknownDifficulty_whenGetWinChance_thenReturn0() {
        int chance = fightService.getWinChance("UNKNOWN");
        assertEquals(0, chance);
    }

    @Test
    void givenEasyDifficulty_whenApplyVictoryRewards_thenReturnCorrectRewards() {
        Map<String, Integer> rewards = fightService.applyVictoryRewards("EASY");

        assertEquals(50, rewards.get("Experience"));
        assertEquals(25, rewards.get("Gold"));
    }

    @Test
    void givenMediumDifficulty_whenApplyVictoryRewards_thenReturnCorrectRewards() {
        Map<String, Integer> rewards = fightService.applyVictoryRewards("MEDIUM");

        assertEquals(100, rewards.get("Experience"));
        assertEquals(50, rewards.get("Gold"));
    }

    @Test
    void givenHardDifficulty_whenApplyVictoryRewards_thenReturnCorrectRewards() {
        Map<String, Integer> rewards = fightService.applyVictoryRewards("HARD");

        assertEquals(200, rewards.get("Experience"));
        assertEquals(100, rewards.get("Gold"));
    }

    @Test
    void givenUnknownDifficulty_whenApplyVictoryRewards_thenReturnZeroRewards() {
        Map<String, Integer> rewards = fightService.applyVictoryRewards("UNKNOWN");

        assertEquals(0, rewards.get("Experience"));
        assertEquals(0, rewards.get("Gold"));
    }

    private User getUserWithCharacter(int level, int health, int energy) {
        GameCharacter character = GameCharacter.builder()
                .level(level)
                .currentHealth(health)
                .currentEnergy(energy)
                .expForNextLevelUp(1000)
                .experience(0)
                .inventory(Inventory.builder().items(new ArrayList<>()).build())
                .resources(new EnumMap<>(ResourceType.class))
                .wearings(new EnumMap<>(Wearings.class))
                .build();

        return User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .gameCharacter(character)
                .build();
    }

}
