package com.supplyboost.chero.game.character.service;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.repository.CharacterRepository;
import com.supplyboost.chero.game.character.events.LevelUpEvent;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.inventory.service.InventoryService;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.item.service.ItemService;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.service.StatsService;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.utils.Generator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final InventoryService inventoryService;
    private final ItemService itemService;
    private final StatsService statsService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CharacterService(CharacterRepository characterRepository, InventoryService inventoryService, ItemService itemService, StatsService statsService, ApplicationEventPublisher eventPublisher) {
        this.characterRepository = characterRepository;
        this.inventoryService = inventoryService;
        this.itemService = itemService;
        this.statsService = statsService;
        this.eventPublisher = eventPublisher;
    }

    public void save(GameCharacter character) {
        characterRepository.save(character);
    }

    public void addResourceAmount(UUID characterId, ResourceType type, int amount) {
        GameCharacter gameCharacter = getCharacter(characterId);
        gameCharacter.getResources().put(type, gameCharacter.getResources().get(type) + amount);

        characterRepository.save(gameCharacter);
    }

    public void removeResourceAmount(UUID characterId, ResourceType type, int removeAmount) {
        GameCharacter gameCharacter = getCharacter(characterId);
        int amount = gameCharacter.getResources().get(type);
        if(amount >= removeAmount){
            gameCharacter.getResources().put(type, amount - removeAmount);
            characterRepository.save(gameCharacter);
        }
    }

    public int getResourceAmount(UUID characterId, ResourceType type) {
        GameCharacter gameCharacter = getCharacter(characterId);
        return gameCharacter.getResources().get(type);
    }

    public GameCharacter createGameCharacter(User user){
        GameCharacter gameCharacter = characterRepository.save(initializeCharacter(user));
        Inventory inventory = inventoryService.createInventory(gameCharacter);
        log.info("Successfully create new game character with id [%s] and Nick Name [%s]"
                .formatted(gameCharacter.getId(), gameCharacter.getNickName()));
        Stats stats = statsService.createCharacterStats(gameCharacter);

        gameCharacter.setInventory(inventory);
        gameCharacter.setStats(stats);
        characterRepository.save(gameCharacter);
        return gameCharacter;
    }

    public void buyItem(UUID characterId, Item item){
        log.info("Item name: [%s]".formatted(item.getName()));
        GameCharacter character = characterRepository.getReferenceById(characterId);
        log.info("Character name: [%s]".formatted(character.getNickName()));

        if(spendMoney(characterId, item.getPrice())){
            itemService.cloneTemplate(item, character.getInventory());
            log.info("Character resource: [%s]".formatted(character.getResources().get(ResourceType.GOLD)));
        }

        characterRepository.save(character);
    }

    public void trainStat(UUID gameCharacterId , UUID id, StatType statType) {
        GameCharacter gameCharacter = characterRepository.getReferenceById(gameCharacterId);

        int price = statsService.getStatPrice(id, statType);

        if(spendMoney(gameCharacterId, price)){
            statsService.increaseStat(id, statType, 1);

            if(StatType.ENDURANCE.equals(statType)){

            }
            switch (statType){
                case StatType.ENDURANCE ->  gameCharacter.setHealth(gameCharacter.getHealth() + 50);
                case StatType.INTELLIGENCE -> gameCharacter.setEnergy(gameCharacter.getEnergy() + 1);
            }
        }
    }



    public boolean spendMoney(UUID characterId, int amount){
        GameCharacter character = characterRepository.getReferenceById(characterId);

        if(character.getResources().get(ResourceType.GOLD) >= amount){
            character.getResources().put(
                    ResourceType.GOLD,
                    character.getResources().get(ResourceType.GOLD) - amount);
            characterRepository.save(character);
            return true;
        }
        return false;
    }

    public void addExperience(GameCharacter character, int amount) {
        character.setExperience(character.getExperience() + amount);

        boolean leveledUp = false;
        while (character.getExperience() >= character.getExpForNextLevelUp()) {
            levelUp(character);
            leveledUp = true;
        }

        characterRepository.save(character);

        if(leveledUp) {
            eventPublisher.publishEvent(new LevelUpEvent(this, character));
        }
    }

    private void levelUp(GameCharacter character) {
        character.setExperience(character.getExperience() - character.getExpForNextLevelUp());
        character.setLevel(character.getLevel() + 1);
        character.setExpForNextLevelUp((int)(character.getExpForNextLevelUp() * 1.2));

        character.setHealth(character.getHealth() + 100);
        character.setCurrentHealth(character.getHealth());

        character.getResources().put(ResourceType.GOLD,
                character.getResources().get(ResourceType.GOLD) + 500 * character.getLevel());

        log.info("Character [%s] leveled up to Level [%d]!"
                .formatted(character.getNickName(), character.getLevel()));
    }

    private Map<ResourceType, Integer> initializeResources() {
        Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
        resources.put(ResourceType.GOLD, 200);

        return resources;
    }

    private GameCharacter getCharacter(UUID characterId) {
        Optional<GameCharacter> character = characterRepository.findById(characterId);

        if(character.isEmpty()){
            throw new IllegalArgumentException("Character with id: {%s} not found.".formatted(characterId));
        }

        return character.get();
    }

    private GameCharacter initializeCharacter(User user){
        Map<ResourceType, Integer> resources = initializeResources();
        return GameCharacter.builder()
                .owner(user)
                .nickName(Generator.generateNickName())
                .expForNextLevelUp(1000)
                .experience(0)
                .currentHealth(1000)
                .health(1000)
                .currentEnergy(90)
                .energy(100)
                .level(0)
                .resources(resources)
                .build();
    }

}
