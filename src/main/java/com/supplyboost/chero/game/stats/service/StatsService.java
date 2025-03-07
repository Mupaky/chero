package com.supplyboost.chero.game.stats.service;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.repository.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class StatsService {

    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public Stats createCharacterStats(GameCharacter gameCharacter){
        Stats stats = statsRepository.save(initializeStatsForCharacter(gameCharacter));
        log.info("Successfully create new character stats with id [%s]."
                .formatted(stats.getId()));
        return stats;
    }

    public Stats createItemStats(Map<StatType, Integer> itemStats){
        Stats stats = statsRepository.save(initializeStats(itemStats));
        log.info("Successfully create new item stats with id [%s]."
                .formatted(stats.getId()));
        return stats;
    }


    public Integer getStatValue(UUID id, StatType statType) {
        Stats stats = statsRepository.getReferenceById(id);

        return stats.getStats().getOrDefault(statType, 5);
    }

    public int getStatPrice(UUID id, StatType statType){
        Stats stats = statsRepository.getReferenceById(id);

        return (int)(stats.getStats().getOrDefault(statType, 1) * 2.5);
    }

    public void increaseStat(UUID id, StatType statType, int amount){
        Stats stats = statsRepository.getReferenceById(id);

        stats.getStats().put(statType,
                stats.getStats().getOrDefault(statType, 5) + amount);

        statsRepository.save(stats);
    }

    private Stats initializeStatsForCharacter(GameCharacter gameCharacter){
        Map<StatType,Integer> initStats = new HashMap<>();
        for(StatType type : StatType.values()){
            initStats.put(type, 5);
        }

        return Stats.builder()
                .owner(gameCharacter)
                .stats(initStats)
                .build();
    }

    private Stats initializeStats(Map<StatType, Integer> initStats){
        return Stats.builder()
                .stats(initStats)
                .build();
    }

}
