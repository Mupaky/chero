package com.supplyboost.chero.game.item;

import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.item.model.ItemType;
import com.supplyboost.chero.game.item.repository.ItemRepository;
import com.supplyboost.chero.game.item.service.ItemService;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.service.StatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StatsService statsService;

    @Spy
    @InjectMocks
    private ItemService itemService;

    @Test
    void givenItemTemplateAndInventory_whenCloneTemplate_thenItemIsClonedAndSaved() {
        Inventory inventory = Inventory.builder()
                .id(UUID.randomUUID())
                .build();

        Stats templateStats = Stats.builder()
                .stats(Map.of(StatType.STRENGTH, 10))
                .build();

        Item template = Item.builder()
                .image("image.png")
                .name("Sword")
                .description("Very powerful!")
                .levelNeeded(5)
                .type(ItemType.WEAPON)
                .stats(templateStats)
                .price(200)
                .build();

        Stats clonedStats = Stats.builder()
                .stats(new HashMap<>(templateStats.getStats()))
                .build();

        when(statsService.createItemStats(anyMap())).thenReturn(clonedStats);

        itemService.cloneTemplate(template, inventory);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);

        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();

        assertEquals(template.getName(), savedItem.getName());
        assertEquals(template.getDescription(), savedItem.getDescription());
        assertEquals(template.getImage(), savedItem.getImage());
        assertEquals(template.getLevelNeeded(), savedItem.getLevelNeeded());
        assertEquals(template.getType(), savedItem.getType());
        assertEquals(template.getPrice(), savedItem.getPrice());
        assertEquals(clonedStats, savedItem.getStats());
        assertEquals(inventory, savedItem.getOwner());
        assertFalse(savedItem.isEquipped());
    }
}
