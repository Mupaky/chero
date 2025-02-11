package com.supplyboost.chero.game.shop.model;

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
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    private List<Item> items = new ArrayList<>();
}
