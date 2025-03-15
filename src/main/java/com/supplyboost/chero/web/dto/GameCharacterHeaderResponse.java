package com.supplyboost.chero.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameCharacterHeaderResponse {

    private String nickName;

    private int experience;

    private int expForNextLevelUp;

    private int health;

    private int currentHealth;

    private int energy;

    private int currentEnergy;

    private int level;

    private int gold;
}
