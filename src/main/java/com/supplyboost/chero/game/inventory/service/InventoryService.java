package com.supplyboost.chero.game.inventory.service;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.inventory.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory createInventory(GameCharacter gameCharacter){
        Inventory inventory = inventoryRepository.save(initializeInventory(gameCharacter));
        log.info("Successfully create new character inventory with id [%s]."
                .formatted(inventory.getId()));
        return inventory;
    }

    public void saveInventory(Inventory inventory){
        inventoryRepository.save(inventory);
    }

    private Inventory initializeInventory(GameCharacter gameCharacter){
        return Inventory.builder()
                .owner(gameCharacter)
                .build();
    }
}
