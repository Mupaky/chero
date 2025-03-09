package com.supplyboost.chero.scheduler;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.repository.CharacterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class GameCharacterScheduler {

    private final CharacterRepository characterRepository;

    @Autowired
    public GameCharacterScheduler(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    // Every 5 minutes
    @Scheduled(fixedRate = 300000) // 300,000 ms = 5 min
    @Transactional
    public void topUpEnergyAndHealth() {
        List<GameCharacter> activeCharacters = characterRepository.findAllByOwner_IsActiveTrue();

        activeCharacters.forEach(character -> {
            int prevEnergy = character.getCurrentEnergy();
            int prevHealth = character.getCurrentHealth();

            character.setCurrentEnergy(Math.min(character.getEnergy(), character.getCurrentEnergy() + 10));

            character.setCurrentHealth(Math.min(character.getHealth(), character.getCurrentHealth() + 80));

            log.info(
                    "Character [{}]: Energy {}→{}, Health {}→{}",
                    character.getNickName(),
                    prevEnergy, character.getCurrentEnergy(),
                    prevHealth, character.getCurrentHealth()
            );

            characterRepository.save(character);
        });

        log.info("Scheduled top-up done for {} active characters.", activeCharacters.size());
    }
}
