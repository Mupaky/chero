package com.supplyboost.chero.game.inventory.model;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.item.model.Item;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private GameCharacter owner;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Item> items = new ArrayList<>();
}
