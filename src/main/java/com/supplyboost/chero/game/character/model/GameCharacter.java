package com.supplyboost.chero.game.character.model;

import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game_character")
public class GameCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User owner;

    @Column
    private String nickName;

    @Column(nullable = false)
    private int experience;

    @Column(nullable = false)
    private int expForNextLevelUp;

    @Column(nullable = false)
    private int health;

    @Column(nullable = false)
    private int currentHealth;

    @Column(nullable = false)
    private int energy;

    @Column(nullable = false)
    private int currentEnergy;

    @Column(nullable = false)
    private int level;

    @OneToOne
    private Stats stats;

    @OneToOne
    private Inventory inventory;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "character_wearings",
            joinColumns = @JoinColumn(name = "character_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @MapKeyColumn(name = "wearing_slot")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Wearings, Item> wearings = new EnumMap<>(Wearings.class);

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "`character_resources`", joinColumns = @JoinColumn(name = "`character_id`"))
    @MapKeyColumn(name = "resource_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "quantity")
    private Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);

}
