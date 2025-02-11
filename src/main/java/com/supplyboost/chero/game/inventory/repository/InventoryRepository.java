package com.supplyboost.chero.game.inventory.repository;

import com.supplyboost.chero.game.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

}
