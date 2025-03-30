package com.supplyboost.chero.game.stats;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.repository.StatsRepository;
import com.supplyboost.chero.game.stats.service.StatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceUTest {

    @Mock
    private StatsRepository statsRepository;

    @Spy
    @InjectMocks
    private StatsService statsService;

    @Test
    void givenGameCharacter_whenCreateCharacterStats_thenStatsAreInitializedAndSaved() {
        GameCharacter character = GameCharacter.builder()
                .id(UUID.randomUUID())
                .build();

        Stats initializedStats = Stats.builder()
                .id(UUID.randomUUID())
                .build();

        when(statsRepository.save(any(Stats.class))).thenReturn(initializedStats);

        Stats result = statsService.createCharacterStats(character);

        assertNotNull(result);

        verify(statsRepository).save(any(Stats.class));
    }

    @Test
    void givenItemStats_whenCreateItemStats_thenStatsAreInitializedAndSaved() {
        Map<StatType, Integer> itemStats = Map.of(StatType.STRENGTH, 10, StatType.ENDURANCE, 5);

        Stats initializedStats = Stats.builder()
                .id(UUID.randomUUID())
                .build();

        when(statsRepository.save(any(Stats.class))).thenReturn(initializedStats);

        Stats result = statsService.createItemStats(itemStats);

        assertNotNull(result);

        verify(statsRepository).save(any(Stats.class));
    }

    @Test
    void givenExistingStat_whenGetStatValue_thenReturnStatValue() {
        UUID statsId = UUID.randomUUID();
        Stats stats = Stats.builder()
                .stats(Map.of(StatType.STRENGTH, 15))
                .build();

        when(statsRepository.getReferenceById(statsId)).thenReturn(stats);

        Integer value = statsService.getStatValue(statsId, StatType.STRENGTH);

        assertEquals(15, value);
    }

    @Test
    void givenMissingStat_whenGetStatValue_thenReturnDefault() {
        UUID statsId = UUID.randomUUID();
        Stats stats = Stats.builder()
                .stats(new HashMap<>())
                .build();

        when(statsRepository.getReferenceById(statsId)).thenReturn(stats);

        Integer value = statsService.getStatValue(statsId, StatType.ENDURANCE);

        assertEquals(5, value);
    }

    @Test
    void givenExistingStat_whenGetStatPrice_thenCalculatePrice() {
        UUID statsId = UUID.randomUUID();
        Stats stats = Stats.builder()
                .stats(Map.of(StatType.INTELLIGENCE, 8))
                .build();

        when(statsRepository.getReferenceById(statsId)).thenReturn(stats);

        int price = statsService.getStatPrice(statsId, StatType.INTELLIGENCE);

        assertEquals(20, price);
    }

    @Test
    void givenMissingStat_whenGetStatPrice_thenCalculatePriceWithDefault() {
        UUID statsId = UUID.randomUUID();
        Stats stats = Stats.builder()
                .stats(new HashMap<>())
                .build();

        when(statsRepository.getReferenceById(statsId)).thenReturn(stats);

        int price = statsService.getStatPrice(statsId, StatType.ENDURANCE);

        assertEquals(2, price);
    }

    @Test
    void givenStat_whenIncreaseStat_thenStatIsIncreasedAndSaved() {
        UUID statsId = UUID.randomUUID();
        Stats stats = Stats.builder()
                .stats(new HashMap<>(Map.of(StatType.ENDURANCE, 5)))
                .build();

        when(statsRepository.getReferenceById(statsId)).thenReturn(stats);

        statsService.increaseStat(statsId, StatType.ENDURANCE, 3);

        assertEquals(8, stats.getStats().get(StatType.ENDURANCE));

        verify(statsRepository).save(stats);
    }

    @Test
    void givenMissingStat_whenIncreaseStat_thenStatIsCreatedAndSaved() {
        UUID statsId = UUID.randomUUID();
        Stats stats = Stats.builder()
                .stats(new HashMap<>())
                .build();

        when(statsRepository.getReferenceById(statsId)).thenReturn(stats);

        statsService.increaseStat(statsId, StatType.STRENGTH, 4);

        assertEquals(9, stats.getStats().get(StatType.STRENGTH));
        
        verify(statsRepository).save(stats);
    }

}
