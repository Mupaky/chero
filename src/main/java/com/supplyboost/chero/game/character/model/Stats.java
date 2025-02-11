package com.supplyboost.chero.game.character.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Stats {
    private int strength;
    private int agility;
    private int intelligence;
    private int endurance;
}
