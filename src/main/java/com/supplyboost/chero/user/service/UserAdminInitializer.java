package com.supplyboost.chero.user.service;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.repository.CharacterRepository;
import com.supplyboost.chero.game.inventory.model.Inventory;
import com.supplyboost.chero.game.inventory.repository.InventoryRepository;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.model.Stats;
import com.supplyboost.chero.game.stats.repository.StatsRepository;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final InventoryRepository inventoryRepository;
    private final StatsRepository statsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User user = User.builder()
                    .username("moderator")
                    .email("admin@underground.com")
                    .password(passwordEncoder.encode("moderator"))
                    .userRole(UserRole.ROLE_ADMIN)
                    .isActive(true)
                    .createdOn(LocalDateTime.now())
                    .updatedOn(LocalDateTime.now())
                    .build();
            userRepository.save(user);

            Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
            resources.put(ResourceType.GOLD, 1000000000);

            user.setGameCharacter(GameCharacter.builder()
                    .owner(user)
                            .nickName("The Moderator")
                            .experience(999998)
                            .expForNextLevelUp(999999)
                            .health(100000000)
                            .currentHealth(100000000)
                            .energy(100000000)
                            .currentEnergy(100000000)
                            .level(100000000)
                            .resources(resources)
                    .build());

            characterRepository.save(user.getGameCharacter());

            Map<StatType, Integer> statsTypes = new HashMap<>();
            statsTypes.put(StatType.STRENGTH, 100000);
            statsTypes.put(StatType.ENDURANCE, 100000);
            statsTypes.put(StatType.AGILITY, 100000);
            statsTypes.put(StatType.INTELLIGENCE, 100000);

            Stats stats = Stats.builder()
                    .owner(user.getGameCharacter())
                    .stats(statsTypes)
                    .build();

            Inventory inventory = Inventory.builder()
                    .owner(user.getGameCharacter())
                    .build();

            user.getGameCharacter().setInventory(inventory);
            user.getGameCharacter().setStats(stats);
            inventoryRepository.save(inventory);
            statsRepository.save(stats);

            characterRepository.save(user.getGameCharacter());
            userRepository.save(user);



            log.info("User Admin added with id [%s]. ".formatted(user.getId()));
        }
    }
}
