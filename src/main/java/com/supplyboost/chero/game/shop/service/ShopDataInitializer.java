package com.supplyboost.chero.game.shop.service;

import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.item.model.ItemType;
import com.supplyboost.chero.game.item.repository.ItemRepository;
import com.supplyboost.chero.game.shop.model.Shop;
import com.supplyboost.chero.game.shop.repository.ShopRepository;
import com.supplyboost.chero.game.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShopDataInitializer implements CommandLineRunner {

    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    @Autowired
    private StatsService statsService;

    @Override
    public void run(String... args) throws Exception {
        // Check if a Shop exists; alternatively, you can check if there are any items
        if (shopRepository.count() == 0) {
            Shop shop = Shop.builder().build();
            List<Item> templates = createItemTemplates();
            // Save items first (if needed) then assign them to the shop
            templates.forEach(itemRepository::save);
            shop.setItems(templates);
            shopRepository.save(shop);
            System.out.println("Shop has been populated with " + templates.size() + " items.");
        }
    }

    private List<Item> createItemTemplates() {
        List<Item> templates = new ArrayList<>();
        Map<StatType, Integer> stats = new HashMap<>();
        stats.put(StatType.STRENGTH, 1);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 2);
        // HELMET templates
        templates.add(Item.builder()
                .image("helmet1.png")
                .name("Bronze Helmet")
                .description("A simple bronze helmet that provides basic protection.")
                .levelNeeded(1)
                .type(ItemType.HELMET)
                .stats(statsService.createItemStats(stats))
                .price(100)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 2);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 4);

        templates.add(Item.builder()
                .image("helmet2.png")
                .name("Iron Helmet")
                .description("A sturdy iron helmet that offers better defense.")
                .levelNeeded(3)
                .type(ItemType.HELMET)
                .stats(statsService.createItemStats(stats))
                .price(250)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 5);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 0);

        // WEAPON templates
        templates.add(Item.builder()
                .image("weapon1.png")
                .name("Sword of Wisdom")
                .description("A basic sword that boosts strength a little.")
                .levelNeeded(1)
                .type(ItemType.WEAPON)
                .stats(statsService.createItemStats(stats))
                .price(100)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 15);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 0);

        templates.add(Item.builder()
                .image("weapon2.png")
                .name("Sword of Valor")
                .description("A legendary sword that boosts strength.")
                .levelNeeded(5)
                .type(ItemType.WEAPON)
                .stats(statsService.createItemStats(stats))
                .price(500)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 0);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 7);

        // SHIELD template
        templates.add(Item.builder()
                .image("shield1.png")
                .name("Wooden Shield")
                .description("A basic shield for beginners.")
                .levelNeeded(2)
                .type(ItemType.SHIELD)
                .stats(statsService.createItemStats(stats))
                .price(150)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 0);
        stats.put(StatType.AGILITY, 1);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 3);

        // ARMOR template
        templates.add(Item.builder()
                .image("armor1.png")
                .name("Leather Armor")
                .description("Light armor offering moderate protection.")
                .levelNeeded(2)
                .type(ItemType.ARMOR)
                .stats(statsService.createItemStats(stats))
                .price(250)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 0);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 3);
        stats.put(StatType.ENDURANCE, 4);

        // RING template
        templates.add(Item.builder()
                .image("ring1.png")
                .name("Ring of Power")
                .description("A ring that boosts magical abilities.")
                .levelNeeded(4)
                .type(ItemType.RING)
                .stats(statsService.createItemStats(stats))
                .price(300)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 2);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 30);
        stats.put(StatType.ENDURANCE, 0);

        // AMULET template
        templates.add(Item.builder()
                .image("amulet1.png")
                .name("Amulet of Wisdom")
                .description("An amulet that increases intelligence.")
                .levelNeeded(4)
                .type(ItemType.AMULET)
                .stats(statsService.createItemStats(stats))
                .price(320)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 0);
        stats.put(StatType.AGILITY, 2);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 0);

        // BOOTS template
        templates.add(Item.builder()
                .image("boots1.png")
                .name("Leather Boots")
                .description("Boots that increase agility.")
                .levelNeeded(1)
                .type(ItemType.BOOTS)
                .stats(statsService.createItemStats(stats))
                .price(80)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 2);
        stats.put(StatType.AGILITY, 0);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 1);

        // GLOVES template
        templates.add(Item.builder()
                .image("gloves1.png")
                .name("Iron Gloves")
                .description("Gloves that improve strength.")
                .levelNeeded(3)
                .type(ItemType.GLOVES)
                .stats(statsService.createItemStats(stats))
                .price(120)
                .isEquipped(false)
                .build());

        stats.clear();

        stats.put(StatType.STRENGTH, 1);
        stats.put(StatType.AGILITY, 1);
        stats.put(StatType.INTELLIGENCE, 0);
        stats.put(StatType.ENDURANCE, 2);

        // PANTS template
        templates.add(Item.builder()
                .image("pants1.png")
                .name("Chainmail Pants")
                .description("Pants offering moderate protection.")
                .levelNeeded(2)
                .type(ItemType.PANTS)
                .stats(statsService.createItemStats(stats))
                .price(180)
                .isEquipped(false)
                .build());


        return templates;
    }
}