package com.supplyboost.chero.game.entity.model;

import jakarta.persistence.Enumerated;
import lombok.*;


@Builder
@Getter
@Setter
public class Monster{

    private String name;

    @Enumerated
    private Difficulty difficulty;

    private int health;

    private int damage;
}
