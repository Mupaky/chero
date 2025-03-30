package com.supplyboost.chero.web.mapper;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.web.dto.AdminUserEditRequest;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.dto.GameCharacterStatsResponse;
import com.supplyboost.chero.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenUser_whenMapToUserEditRequest_thenReturnCorrectDto() {
        GameCharacter character = GameCharacter.builder()
                .nickName("testNickName")
                .build();

        User user = User.builder()
                .email("test@test.com")
                .gameCharacter(character)
                .build();

        UserEditRequest dto = DtoMapper.mapToUserEditRequest(user);

        assertNotNull(dto);
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("testNickName", dto.getCharacterName());
    }

    @Test
    void givenGameCharacter_whenMapToGameCharacterHeaderResponse_thenReturnCorrectDto() {
        GameCharacter character = GameCharacter.builder()
                .nickName("testNickName")
                .experience(100)
                .expForNextLevelUp(200)
                .health(500)
                .currentHealth(450)
                .energy(10)
                .currentEnergy(5)
                .level(3)
                .resources(new EnumMap<>(Map.of(ResourceType.GOLD, 1000)))
                .build();

        GameCharacterHeaderResponse dto = DtoMapper.mapToGameCharacterHeaderResponse(character);

        assertNotNull(dto);
        assertEquals("testNickName", dto.getNickName());
        assertEquals(100, dto.getExperience());
        assertEquals(200, dto.getExpForNextLevelUp());
        assertEquals(500, dto.getHealth());
        assertEquals(450, dto.getCurrentHealth());
        assertEquals(10, dto.getEnergy());
        assertEquals(5, dto.getCurrentEnergy());
        assertEquals(3, dto.getLevel());
        assertEquals(1000, dto.getGold());
    }

    @Test
    void givenGameCharacter_whenMapToGameCharacterStatsResponse_thenReturnCorrectDto() {
        Stats stats = Stats.builder()
                .stats(Map.of(
                        StatType.STRENGTH, 10,
                        StatType.AGILITY, 8,
                        StatType.INTELLIGENCE, 7,
                        StatType.ENDURANCE, 5
                ))
                .build();

        GameCharacter character = GameCharacter.builder()
                .stats(stats)
                .build();

        GameCharacterStatsResponse dto = DtoMapper.mapToGameCharacterStatsResponse(character);

        assertNotNull(dto);
        assertEquals(10, dto.getStrength());
        assertEquals(8, dto.getAgility());
        assertEquals(7, dto.getIntelligence());
        assertEquals(5, dto.getEndurance());
    }

    @Test
    void givenUser_whenMapToAdminUserEditRequest_thenReturnCorrectDto() {
        GameCharacter character = GameCharacter.builder()
                .nickName("testNickName")
                .build();

        User user = User.builder()
                .email("test@test.com")
                .userRole(UserRole.ROLE_USER)
                .gameCharacter(character)
                .build();

        AdminUserEditRequest dto = DtoMapper.mapToAdminUserEditRequest(user);

        assertNotNull(dto);
        assertEquals("testNickName", dto.getCharacterName());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals(UserRole.ROLE_USER, dto.getUserRole());
    }

}
