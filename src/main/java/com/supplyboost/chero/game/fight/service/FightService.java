package com.supplyboost.chero.game.fight.service;

import com.supplyboost.chero.game.character.events.NotificationEventService;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
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
    private final NotificationEventService notificationEventService;

    @Autowired
    public FightService(CharacterService characterService, NotificationEventService notificationEventService) {
        this.characterService = characterService;
        this.notificationEventService = notificationEventService;
    }

    public void handleFight(User user, String difficulty) {
        GameCharacter character = user.getGameCharacter();
        int previousLevel = character.getLevel();

        if(character.getCurrentEnergy() > 0){
            character.setCurrentEnergy(character.getCurrentEnergy() - 1);
        }else{
            notificationEventService.notify(user, "🎉 Out of energy. Please rest!");
            return;
        }

        if(character.getCurrentHealth() <= 0){
            notificationEventService.notify(user, "🎉 You are hurt and have no health. Please rest!");
            return;
        }

        boolean victory = new Random().nextInt(100) < getWinChance(difficulty);

        if (victory) {
            Map<String, Integer> rewards = applyVictoryRewards(difficulty);
            characterService.addExperience(character, rewards.get("Experience"));
            characterService.addResourceAmount(character.getId(), ResourceType.GOLD, rewards.get("Gold"));
            notificationEventService.notify(user, "🎉 You won the fight against " + difficulty + " monster!");

            if (character.getLevel() > previousLevel) {
                notificationEventService.notify(user, "✨ Congratulations! You reached level " + character.getLevel() + "!");
            }
        } else {
            applyDefeatPenalties(character, difficulty);
            characterService.save(character);
            notificationEventService.notify(user, "💥 You lost the fight. Try again!");
        }
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
