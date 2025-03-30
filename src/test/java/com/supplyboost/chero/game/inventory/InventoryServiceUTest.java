package com.supplyboost.chero.game.inventory;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.inventory.repository.InventoryRepository;
import com.supplyboost.chero.game.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceUTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Spy
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void givenGameCharacter_whenCreateInventory_thenInventoryIsInitializedAndSaved() {
        GameCharacter character = GameCharacter.builder()
                .id(UUID.randomUUID())
                .build();

        Inventory initializedInventory = Inventory.builder()
                .owner(character)
                .build();

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(initializedInventory);

        Inventory result = inventoryService.createInventory(character);

        assertNotNull(result);
        assertEquals(character, result.getOwner());

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void givenInventory_whenSaveInventory_thenInventoryIsSaved() {
        Inventory inventory = Inventory.builder()
                .id(UUID.randomUUID())
                .build();

        inventoryService.saveInventory(inventory);

        verify(inventoryRepository).save(inventory);
    }

}
