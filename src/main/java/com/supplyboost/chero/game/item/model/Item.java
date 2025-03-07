package com.supplyboost.chero.game.item.model;

import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.stats.model.Stats;
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
    private String description;

    @Column(nullable = false)
    private int levelNeeded;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = true)
    private Inventory owner;

    @Column
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @OneToOne
    private Stats stats;

    @Column(nullable = false)
    private int price;

    private boolean isEquipped;
}
