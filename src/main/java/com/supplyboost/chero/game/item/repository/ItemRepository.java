package com.supplyboost.chero.game.item.repository;

import com.supplyboost.chero.game.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

}
