package com.supplyboost.chero.game.item.service;

import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.item.repository.ItemRepository;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Slf4j
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final StatsService statsService;

    @Autowired
    public ItemService(ItemRepository itemRepository, StatsService statsService) {
        this.itemRepository = itemRepository;
        this.statsService = statsService;
    }


    public void cloneTemplate(Item template, Inventory inventory) {
        Stats stats = statsService.createItemStats(new HashMap<>(template.getStats().getStats()));

        Item item = Item.builder()
                .image(template.getImage())
                .name(template.getName())
                .description(template.getDescription())
                .levelNeeded(template.getLevelNeeded())
                .owner(inventory)
                .type(template.getType())
                .stats(stats)
                .price(template.getPrice())
                .isEquipped(false)
                .build();

        itemRepository.save(item);

    }
}
