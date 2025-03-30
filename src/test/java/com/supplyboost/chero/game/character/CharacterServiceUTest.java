package com.supplyboost.chero.game.character;

import com.supplyboost.chero.game.character.events.LevelUpEvent;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.model.Wearings;
import com.supplyboost.chero.game.character.repository.CharacterRepository;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.inventory.service.InventoryService;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.item.model.ItemType;
import com.supplyboost.chero.game.item.service.ItemService;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.service.StatsService;
import com.supplyboost.chero.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CharacterServiceUTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ItemService itemService;

    @Mock
    private StatsService statsService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    @InjectMocks
    private CharacterService characterService;


    @Test
    void givenCharacter_whenSave_thenRepositoryIsCalled() {
        GameCharacter character = GameCharacter.builder()
                .id(UUID.randomUUID())
                .build();

        characterService.save(character);

        verify(characterRepository).save(character);
    }

    @Test
    void givenCharacterWithResources_whenAddResourceAmount_thenAmountIsIncreasedAndSaved() {
        UUID characterId = UUID.randomUUID();

        ResourceType type = ResourceType.GOLD;
        int initialAmount = 100;
        int toAdd = 50;

        GameCharacter gameCharacter = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(Map.of(type, initialAmount)))
                .build();

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(gameCharacter));
        when(characterService.getCharacter(characterId)).thenReturn(gameCharacter);

        characterService.addResourceAmount(characterId, type, toAdd);

        assertEquals(initialAmount + toAdd, gameCharacter.getResources().get(type));

        verify(characterRepository).save(gameCharacter);
    }

    @Test
    void givenEnoughResources_whenRemoveResourceAmount_thenAmountIsDecreasedAndSaved() {
        UUID characterId = UUID.randomUUID();
        ResourceType type = ResourceType.GOLD;
        int initial = 100;
        int toRemove = 30;

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(Map.of(type, initial)))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        characterService.removeResourceAmount(characterId, type, toRemove);

        assertEquals(initial - toRemove, character.getResources().get(type));

        verify(characterRepository).save(character);
    }

    @Test
    void givenNotEnoughResources_whenRemoveResourceAmount_thenNoChangeAndNotSaved() {
        UUID characterId = UUID.randomUUID();
        ResourceType type = ResourceType.GOLD;
        int initial = 20;
        int toRemove = 50;

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(Map.of(type, initial)))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        characterService.removeResourceAmount(characterId, type, toRemove);

        assertEquals(initial, character.getResources().get(type));

        verify(characterRepository, never()).save(any());
    }

    @Test
    void givenCharacterWithResource_whenGetResourceAmount_thenReturnCorrectAmount() {
        UUID characterId = UUID.randomUUID();
        ResourceType type = ResourceType.WOOD;
        int expectedAmount = 75;

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(Map.of(type, expectedAmount)))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        int result = characterService.getResourceAmount(characterId, type);

        assertEquals(expectedAmount, result);
    }

    @Test
    void givenResourceTypeMissing_whenGetResourceAmount_thenThrowException() {
        UUID characterId = UUID.randomUUID();
        ResourceType type = ResourceType.GOLD;

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(ResourceType.class))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        assertThrows(NullPointerException.class, () -> characterService.getResourceAmount(characterId, type));
    }

    @Test
    void givenIdAndNewName_whenEditCharacterName_thenNicknameIsUpdatedAndSaved() {
        UUID characterId = UUID.randomUUID();
        String newNickName = "testName";

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .nickName("oldName")
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);

        characterService.editCharacterName(characterId, newNickName);

        assertEquals(newNickName, character.getNickName());

        verify(characterRepository).getReferenceById(characterId);
        verify(characterService).save(character);
    }

    @Test
    void givenUser_whenCreateGameCharacter_thenCharacterIsInitializedWithInventoryAndStats() {
        UUID characterId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();

        GameCharacter initializedCharacter = GameCharacter.builder()
                .id(characterId)
                .nickName("testName")
                .build();

        Inventory inventory = Inventory.builder().id(UUID
                .randomUUID())
                .build();

        Stats stats = Stats.builder().id(UUID
                .randomUUID())
                .build();

        when(characterRepository.save(any(GameCharacter.class))).thenReturn(initializedCharacter);
        when(inventoryService.createInventory(initializedCharacter)).thenReturn(inventory);
        when(statsService.createCharacterStats(initializedCharacter)).thenReturn(stats);

        GameCharacter result = characterService.createGameCharacter(user);

        assertNotNull(result);
        assertEquals(inventory, result.getInventory());
        assertEquals(stats, result.getStats());

        verify(inventoryService).createInventory(initializedCharacter);
        verify(statsService).createCharacterStats(initializedCharacter);
    }

    @Test
    void givenCharacterAndItem_whenBuyItemAndHasEnoughGold_thenItemIsClonedAndCharacterSaved() {
        UUID characterId = UUID.randomUUID();

        Inventory inventory = Inventory.builder().id(UUID.randomUUID()).build();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .nickName("testName")
                .inventory(inventory)
                .resources(new EnumMap<>(Map.of(ResourceType.GOLD, 500)))
                .build();

        Item item = Item.builder()
                .name("testItemName")
                .price(300)
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);
        doReturn(true).when(characterService).spendMoney(characterId, item.getPrice());

        characterService.buyItem(characterId, item);

        verify(characterRepository).getReferenceById(characterId);
        verify(characterService).spendMoney(characterId, item.getPrice());
        verify(itemService).cloneTemplate(item, inventory);
        verify(characterRepository).save(character);
    }


    @Test
    void givenCharacterAndItem_whenSpendMoneyFails_thenDoNotCloneItemOrSaveCharacter() {
        UUID characterId = UUID.randomUUID();
        Inventory inventory = Inventory.builder().id(UUID.randomUUID()).build();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .nickName("testName")
                .inventory(inventory)
                .resources(new EnumMap<>(Map.of(ResourceType.GOLD, 100)))
                .build();

        Item item = Item.builder()
                .name("testItemName")
                .price(300)
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);
        doReturn(false).when(characterService).spendMoney(characterId, item.getPrice());

        characterService.buyItem(characterId, item);

        verify(characterRepository).getReferenceById(characterId);
        verify(characterService).spendMoney(characterId, item.getPrice());
        verify(itemService, never()).cloneTemplate(any(), any());
        verify(characterRepository, never()).save(any());
    }

    @Test
    void givenStatTypeEndurance_whenTrainStat_thenHealthIncreasesAndStatIsUpdated() {
        UUID characterId = UUID.randomUUID();
        UUID statsId = UUID.randomUUID();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .health(100)
                .energy(100)
                .stats(Stats.builder().id(statsId).build())
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);
        when(statsService.getStatPrice(statsId, StatType.ENDURANCE)).thenReturn(100);
        doReturn(true).when(characterService).spendMoney(characterId, 100);

        characterService.trainStat(characterId, StatType.ENDURANCE);

        assertEquals(150, character.getHealth());
        verify(statsService).increaseStat(statsId, StatType.ENDURANCE, 1);
    }

    @Test
    void givenStatTypeIntelligence_whenTrainStat_thenEnergyIncreasesAndStatIsUpdated() {
        UUID characterId = UUID.randomUUID();
        UUID statsId = UUID.randomUUID();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .health(100)
                .energy(100)
                .stats(Stats.builder().id(statsId).build())
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);
        when(statsService.getStatPrice(statsId, StatType.INTELLIGENCE)).thenReturn(100);
        doReturn(true).when(characterService).spendMoney(characterId, 100);

        characterService.trainStat(characterId, StatType.INTELLIGENCE);

        assertEquals(101, character.getEnergy());

        verify(statsService).increaseStat(statsId, StatType.INTELLIGENCE, 1);
    }

    @Test
    void givenSpendMoneyFails_whenTrainStat_thenDoNothing() {
        UUID characterId = UUID.randomUUID();
        UUID statsId = UUID.randomUUID();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .health(100)
                .energy(100)
                .stats(Stats.builder().id(statsId).build())
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);
        when(statsService.getStatPrice(statsId, StatType.ENDURANCE)).thenReturn(100);
        doReturn(false).when(characterService).spendMoney(characterId, 100);

        characterService.trainStat(characterId, StatType.ENDURANCE);

        assertEquals(100, character.getHealth());
        assertEquals(100, character.getEnergy());

        verify(statsService, never()).increaseStat(any(), any(), anyInt());
    }

    @Test
    void givenEnoughGold_whenSpendMoney_thenSubtractAndSaveAndReturnTrue() {
        UUID characterId = UUID.randomUUID();
        int initialGold = 500;
        int amountToSpend = 200;

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(Map.of(ResourceType.GOLD, initialGold)))
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);

        boolean result = characterService.spendMoney(characterId, amountToSpend);

        assertTrue(result);
        assertEquals(initialGold - amountToSpend, character.getResources().get(ResourceType.GOLD));

        verify(characterRepository).save(character);
    }

    @Test
    void givenNotEnoughGold_whenSpendMoney_thenReturnFalseAndDoNotSave() {
        UUID characterId = UUID.randomUUID();
        int initialGold = 100;
        int amountToSpend = 300;

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .resources(new EnumMap<>(Map.of(ResourceType.GOLD, initialGold)))
                .build();

        when(characterRepository.getReferenceById(characterId)).thenReturn(character);

        boolean result = characterService.spendMoney(characterId, amountToSpend);

        assertFalse(result);
        assertEquals(initialGold, character.getResources().get(ResourceType.GOLD));

        verify(characterRepository, never()).save(any());
    }

    @Test
    void givenValidItemAndEnoughLevel_whenEquipItem_thenEquipAndSave() {
        UUID characterId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item item = Item.builder()
                .id(itemId)
                .type(ItemType.WEAPON)
                .levelNeeded(5)
                .isEquipped(false)
                .build();

        Inventory inventory = Inventory.builder()
                .items(new ArrayList<>(List.of(item)))
                .build();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .level(5)
                .inventory(inventory)
                .wearings(new EnumMap<>(Wearings.class))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        boolean result = characterService.equipItem(characterId, itemId);

        assertTrue(result);
        assertTrue(item.isEquipped());
        assertEquals(item, character.getWearings().get(Wearings.WEAPON));
        assertFalse(inventory.getItems().contains(item));

        verify(characterService).save(character);
        verify(inventoryService).saveInventory(inventory);
    }

    @Test
    void givenInvalidItemId_whenEquipItem_thenThrowItemNotFound() {
        UUID characterId = UUID.randomUUID();
        UUID missingItemId = UUID.randomUUID();

        Inventory inventory = Inventory.builder()
                .items(new ArrayList<>())
                .build();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .level(10)
                .inventory(inventory)
                .wearings(new EnumMap<>(Wearings.class))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        assertThrows(IllegalArgumentException.class, () -> characterService.equipItem(characterId, missingItemId));
    }

    @Test
    void givenLowLevel_whenEquipItem_thenThrowLevelTooLow() {
        UUID characterId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item item = Item.builder()
                .id(itemId)
                .type(ItemType.WEAPON)
                .levelNeeded(10)
                .isEquipped(false)
                .build();

        Inventory inventory = Inventory.builder()
                .items(new ArrayList<>(List.of(item)))
                .build();

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .level(5)
                .inventory(inventory)
                .wearings(new EnumMap<>(Wearings.class))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        assertThrows(IllegalArgumentException.class, () -> characterService.equipItem(characterId, itemId));
    }

    @Test
    void givenExistingEquippedItem_whenEquipNewItem_thenReplaceAndReturnOldOneToInventory() {
        UUID characterId = UUID.randomUUID();
        UUID oldItemId = UUID.randomUUID();
        UUID newItemId = UUID.randomUUID();

        Item oldItem = Item.builder()
                .id(oldItemId)
                .type(ItemType.ARMOR)
                .isEquipped(true)
                .build();

        Item newItem = Item.builder()
                .id(newItemId)
                .type(ItemType.ARMOR)
                .levelNeeded(1)
                .isEquipped(false)
                .build();

        Inventory inventory = Inventory.builder()
                .items(new ArrayList<>(List.of(newItem)))
                .build();

        Map<Wearings, Item> wearings = new EnumMap<>(Wearings.class);
        wearings.put(Wearings.ARMOR, oldItem);

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .level(10)
                .inventory(inventory)
                .wearings(wearings)
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        characterService.equipItem(characterId, newItemId);

        assertTrue(newItem.isEquipped());
        assertFalse(oldItem.isEquipped());
        assertEquals(newItem, character.getWearings().get(Wearings.ARMOR));
        assertTrue(inventory.getItems().contains(oldItem));
        assertFalse(inventory.getItems().contains(newItem));

        verify(characterService).save(character);
        verify(inventoryService).saveInventory(inventory);
    }

    @Test
    void givenValidSlotWithItem_whenUnEquipItem_thenItemIsMovedAndSaved() {
        UUID characterId = UUID.randomUUID();
        String slot = "weapon";

        Item equippedItem = Item.builder()
                .id(UUID.randomUUID())
                .isEquipped(true)
                .type(ItemType.WEAPON)
                .build();

        Inventory inventory = Inventory.builder()
                .items(new ArrayList<>())
                .build();

        Map<Wearings, Item> wearings = new EnumMap<>(Wearings.class);
        wearings.put(Wearings.WEAPON, equippedItem);

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .inventory(inventory)
                .wearings(wearings)
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        boolean result = characterService.unEquipItem(characterId, slot);

        assertTrue(result);
        assertFalse(equippedItem.isEquipped());
        assertTrue(inventory.getItems().contains(equippedItem));
        assertFalse(character.getWearings().containsKey(Wearings.WEAPON));

        verify(characterService).save(character);
        verify(inventoryService).saveInventory(inventory);
    }

    @Test
    void givenInvalidSlotString_whenUnEquipItem_thenThrowException() {
        UUID characterId = UUID.randomUUID();
        String invalidSlot = "testSlot";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> characterService.unEquipItem(characterId, invalidSlot));

        assertTrue(exception.getMessage().contains("Invalid slot specified"));
    }

    @Test
    void givenValidSlotButEmpty_whenUnEquipItem_thenThrowException() {
        UUID characterId = UUID.randomUUID();
        String slot = "armor";

        GameCharacter character = GameCharacter.builder()
                .id(characterId)
                .inventory(Inventory.builder()
                        .items(new ArrayList<>())
                        .build())
                .wearings(new EnumMap<>(Wearings.class))
                .build();

        doReturn(character).when(characterService).getCharacter(characterId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> characterService.unEquipItem(characterId, slot));

        assertTrue(exception.getMessage().contains("No item equipped"));
    }

    @Test
    void givenCharacterWithBaseStatsOnly_whenGetEnhancedStats_thenReturnBaseStats() {
        Map<StatType, Integer> baseStats = Map.of(StatType.STRENGTH, 10, StatType.ENDURANCE, 5);

        Stats stats = Stats.builder()
                .stats(baseStats)
                .build();

        GameCharacter character = GameCharacter.builder()
                .stats(stats)
                .wearings(new EnumMap<>(Wearings.class))
                .build();

        Map<StatType, Integer> result = characterService.getEnhancedStats(character);

        assertEquals(2, result.size());
        assertEquals(10, result.get(StatType.STRENGTH));
        assertEquals(5, result.get(StatType.ENDURANCE));
    }

    @Test
    void givenCharacterWithItemBoostingStat_whenGetEnhancedStats_thenReturnMergedStats() {
        Map<StatType, Integer> baseStats = Map.of(StatType.STRENGTH, 10);
        Map<StatType, Integer> itemStats = Map.of(StatType.STRENGTH, 5);

        Item item = Item.builder()
                .stats(Stats.builder()
                        .stats(itemStats)
                        .build())
                .build();

        Map<Wearings, Item> wearings = Map.of(Wearings.WEAPON, item);

        GameCharacter character = GameCharacter.builder()
                .stats(Stats.builder()
                        .stats(baseStats)
                        .build())
                .wearings(new EnumMap<>(wearings))
                .build();

        Map<StatType, Integer> result = characterService.getEnhancedStats(character);

        assertEquals(1, result.size());
        assertEquals(15, result.get(StatType.STRENGTH));
    }

    @Test
    void givenItemWithNullStats_whenGetEnhancedStats_thenIgnoreItem() {
        Map<StatType, Integer> baseStats = Map.of(StatType.ENDURANCE, 8);

        Item item = Item.builder()
                .stats(null)
                .build();

        Map<Wearings, Item> wearings = Map.of(Wearings.ARMOR, item);

        GameCharacter character = GameCharacter.builder()
                .stats(Stats.builder()
                        .stats(baseStats)
                        .build())
                .wearings(new EnumMap<>(wearings))
                .build();

        Map<StatType, Integer> result = characterService.getEnhancedStats(character);

        assertEquals(1, result.size());
        assertEquals(8, result.get(StatType.ENDURANCE));
    }

    @Test
    void givenMultipleItemsBoostingSameStat_whenGetEnhancedStats_thenStatsAreSummed() {
        Map<StatType, Integer> baseStats = Map.of(StatType.INTELLIGENCE, 5);

        Item ring = Item.builder()
                .stats(Stats.builder()
                        .stats(Map.of(StatType.INTELLIGENCE, 2))
                        .build())
                .build();

        Item amulet = Item.builder()
                .stats(Stats.builder()
                        .stats(Map.of(StatType.INTELLIGENCE, 3))
                        .build())
                .build();

        Map<Wearings, Item> wearings = Map.of(Wearings.RING, ring, Wearings.AMULET, amulet);

        GameCharacter character = GameCharacter.builder()
                .stats(Stats.builder().stats(baseStats).build())
                .wearings(new EnumMap<>(wearings))
                .build();

        Map<StatType, Integer> result = characterService.getEnhancedStats(character);

        assertEquals(1, result.size());
        assertEquals(10, result.get(StatType.INTELLIGENCE));
    }

    @Test
    void givenCharacterWithLowXP_whenAddExperience_thenJustAddXpAndSave() {
        GameCharacter character = GameCharacter.builder()
                .experience(50)
                .expForNextLevelUp(200)
                .build();

        characterService.addExperience(character, 100);

        assertEquals(150, character.getExperience());

        verify(characterRepository).save(character);
        verify(eventPublisher, never()).publishEvent(any(LevelUpEvent.class));
    }

    @Test
    void givenCharacterWhenXPIsEnoughToLevelUp_thenLevelUpAndPublishEvent() {
        GameCharacter character = GameCharacter.builder()
                .experience(150)
                .expForNextLevelUp(200)
                .build();

        doAnswer(invocation -> {
            character.setExperience(50);
            return null;
        }).when(characterService).levelUp(character);

        characterService.addExperience(character, 100);

        assertEquals(50, character.getExperience());
        verify(characterService).levelUp(character);
        verify(characterRepository).save(character);
        verify(eventPublisher).publishEvent(any(LevelUpEvent.class));
    }

    @Test
    void givenXpExactlyAtThreshold_whenAddExperience_thenLevelUpTriggered() {
        GameCharacter character = GameCharacter.builder()
                .experience(150)
                .expForNextLevelUp(200)
                .build();

        doAnswer(invocation -> {
            character.setExperience(0);
            return null;
        }).when(characterService).levelUp(character);

        characterService.addExperience(character, 50);

        verify(characterService).levelUp(character);
        verify(characterRepository).save(character);
        verify(eventPublisher).publishEvent(any(LevelUpEvent.class));
    }

    @Test
    void givenCharacter_whenLevelUp_thenUpdateStatsCorrectly() {
        GameCharacter character = GameCharacter.builder()
                .experience(300)
                .expForNextLevelUp(200)
                .level(5)
                .health(500)
                .currentHealth(450)
                .resources(new EnumMap<>(Map.of(ResourceType.GOLD, 1000)))
                .nickName("testNickName")
                .build();

        characterService.levelUp(character);

        assertEquals(100, character.getExperience());
        assertEquals(6, character.getLevel());
        assertEquals(240, character.getExpForNextLevelUp());
        assertEquals(600, character.getHealth());
        assertEquals(600, character.getCurrentHealth());
        assertEquals(4000, character.getResources().get(ResourceType.GOLD));
    }


}
