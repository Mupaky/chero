package com.supplyboost.chero.game.stats.model;

import com.supplyboost.chero.game.character.model.GameCharacter;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private GameCharacter owner;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "character_stats", joinColumns = @JoinColumn(name = "character_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "stat_value")
    private Map<StatType, Integer> stats = new HashMap<>();

}
