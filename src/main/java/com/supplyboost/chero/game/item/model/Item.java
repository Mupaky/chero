package com.supplyboost.chero.game.item.model;

import com.supplyboost.chero.game.character.model.Stats;
import com.supplyboost.chero.game.inventory.model.Inventory;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String Description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = true)
    private Inventory owner;

    @Column
    @Enumerated(EnumType.STRING)
    private ItemType type;

    private Stats stats;

    @Column(nullable = false)
    private int price;

    private boolean isEquipped;
}
