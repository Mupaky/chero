package com.supplyboost.chero.web.mapper;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.notification.client.dto.NotificationRequest;
import com.supplyboost.chero.notification.client.dto.NotificationResponse;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.dto.GameCharacterStatsResponse;
import com.supplyboost.chero.web.dto.UserEditRequest;
import com.supplyboost.chero.user.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DtoMapper {

    public static UserEditRequest mapToUserEditRequest(User user){
        return UserEditRequest.builder()
                .profilePicture(user.getProfile_picture())
                .email(user.getEmail())
                .characterName(user.getGameCharacter().getNickName())
                .build();
    }

    public static GameCharacterHeaderResponse mapToGameCharacterHeaderResponse(GameCharacter gameCharacter){
        return GameCharacterHeaderResponse.builder()
                .nickName(gameCharacter.getNickName())
                .experience(gameCharacter.getExperience())
                .expForNextLevelUp(gameCharacter.getExpForNextLevelUp())
                .health(gameCharacter.getHealth())
                .currentHealth(gameCharacter.getCurrentHealth())
                .energy(gameCharacter.getEnergy())
                .currentEnergy(gameCharacter.getCurrentEnergy())
                .level(gameCharacter.getLevel())
                .gold(gameCharacter.getResources().get(ResourceType.GOLD))
                .build();
    }

    public static GameCharacterStatsResponse mapToGameCharacterStatsResponse(GameCharacter gameCharacter){

        return GameCharacterStatsResponse.builder()
                .strength(gameCharacter.getStats().getStats().get(StatType.STRENGTH))
                .agility(gameCharacter.getStats().getStats().get(StatType.AGILITY))
                .intelligence(gameCharacter.getStats().getStats().get(StatType.INTELLIGENCE))
                .endurance(gameCharacter.getStats().getStats().get(StatType.ENDURANCE))
                .build();
    }

}
