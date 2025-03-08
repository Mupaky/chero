package com.supplyboost.chero.game.fight.service;

import com.supplyboost.chero.game.character.events.NotificationService;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.repository.CharacterRepository;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class FightService {

    private final CharacterService characterService;
    private final NotificationService notificationService;

    @Autowired
    public FightService(CharacterService characterService, NotificationService notificationService) {
        this.characterService = characterService;
        this.notificationService = notificationService;
    }

    public boolean handleFight(User user, String difficulty) {
        GameCharacter character = user.getGameCharacter();
        int previousLevel = character.getLevel();

        if(character.getCurrentEnergy() > 0){
            character.setCurrentEnergy(character.getCurrentEnergy() - 1);
        }else{
            notificationService.notify(user, "🎉 Out of energy. Please rest!");
            return false;
        }

        if(character.getCurrentHealth() <= 0){
            notificationService.notify(user, "🎉 You are hurt and have no health. Please rest!");
            return false;
        }

        boolean victory = new Random().nextInt(100) < getWinChance(difficulty);

        if (victory) {
            Map<String, Integer> rewards = applyVictoryRewards(difficulty);
            characterService.addExperience(character, rewards.get("Experience"));
            characterService.addResourceAmount(character.getId(), ResourceType.GOLD, rewards.get("Gold"));
            notificationService.notify(user, "🎉 You won the fight against " + difficulty + " monster!");

            if (character.getLevel() > previousLevel) {
                notificationService.notify(user, "✨ Congratulations! You reached level " + character.getLevel() + "!");
            }
        } else {
            applyDefeatPenalties(character, difficulty);
            characterService.save(character);
            notificationService.notify(user, "💥 You lost the fight. Try again!");
        }

        return victory;
    }

    private int getWinChance(String difficulty) {
        return switch (difficulty.toUpperCase()) {
            case "EASY" -> 70;
            case "MEDIUM" -> 50;
            case "HARD" -> 40;
            default -> 0;
        };
    }

    private Map<String, Integer> applyVictoryRewards(String difficulty) {
        Map<String, Integer> rewards = new HashMap<>();
        int experience = 0;
        int gold = 0;
        switch (difficulty.toUpperCase()) {
            case "EASY":
                experience = 50;
                gold = 25;
                break;

            case "MEDIUM":
                experience = 100;
                gold = 50;
                break;

            case "HARD":
                experience = 200;
                gold = 100;
                break;
        }
        rewards.put("Experience", experience);
        rewards.put("Gold", gold);
        return rewards;
    }

    private void applyDefeatPenalties(GameCharacter character, String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                character.setCurrentHealth(Math.max(character.getCurrentHealth() - 15, 0));
                break;

            case "MEDIUM":
                character.setCurrentHealth(Math.max(character.getCurrentHealth() - 40, 0));
                break;

            case "HARD":
                character.setCurrentHealth(Math.max(character.getCurrentHealth() - 80, 0));
                break;
        }
    }

}
