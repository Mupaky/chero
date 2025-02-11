package com.supplyboost.chero.game.character.service;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.model.Stats;
import com.supplyboost.chero.game.character.repository.CharacterRepository;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.inventory.service.InventoryService;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.utils.Generator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CharacterService(CharacterRepository characterRepository, InventoryService inventoryService) {
        this.characterRepository = characterRepository;
        this.inventoryService = inventoryService;
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

        gameCharacter.setInventory(inventory);
        characterRepository.save(gameCharacter);
        return gameCharacter;
    }


    private Map<ResourceType, Integer> initializeResources() {
        Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
        resources.put(ResourceType.GOLD, 200);

        return resources;
    }

    private Stats initializeStats(){
        return Stats.builder()
                .agility(5)
                .strength(5)
                .intelligence(5)
                .endurance(5)
                .build();
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
                .stats(initializeStats())
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
