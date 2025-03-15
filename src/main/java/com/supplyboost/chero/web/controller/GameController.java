package com.supplyboost.chero.web.controller;


import com.supplyboost.chero.game.character.events.NotificationEventService;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.game.fight.service.FightService;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.shop.model.Shop;
import com.supplyboost.chero.game.shop.service.ShopService;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.dto.GameCharacterStatsResponse;
import com.supplyboost.chero.web.mapper.DtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/game")
public class GameController {

    private final UserService userService;
    private final ShopService shopService;
    private final CharacterService characterService;
    private final FightService fightService;

    @Autowired
    public GameController(UserService userService, ShopService shopService, CharacterService characterService, FightService fightService, NotificationEventService notificationEventService) {
        this.userService = userService;
        this.shopService = shopService;
        this.characterService = characterService;
        this.fightService = fightService;
    }


    @GetMapping("/dashboard")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.findByUsername(authenticationMetadata.getUsername());

        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());
        GameCharacterStatsResponse gameCharacterStatsResponse = DtoMapper.mapToGameCharacterStatsResponse(user.getGameCharacter());
        Map<StatType, Integer> enhancedStats = characterService.getEnhancedStats(user.getGameCharacter());

        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("profile_picture", user.getProfile_picture());
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);
        modelAndView.addObject("stats", gameCharacterStatsResponse);
        modelAndView.addObject("enhancedStats", enhancedStats);

        log.info("Enhanced Stats: " + enhancedStats);

        return modelAndView;
    }

    @GetMapping("/train-stats")
    public ModelAndView getTrainingHall(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());
        GameCharacterStatsResponse gameCharacterStatsResponse = DtoMapper.mapToGameCharacterStatsResponse(user.getGameCharacter());

        ModelAndView modelAndView = new ModelAndView("train-stats");
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);
        modelAndView.addObject("stats", gameCharacterStatsResponse);

        return modelAndView;
    }

    @PostMapping("/train")
    public ModelAndView trainStats(@RequestParam String stat,
                                @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        StatType statType = StatType.valueOf(stat);

        characterService.trainStat(user.getGameCharacter().getId(), statType);

        return new ModelAndView("redirect:/game/train-stats");
    }

    @GetMapping("/underground")
    public ModelAndView getUnderground(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());


        ModelAndView modelAndView = new ModelAndView("underground");
        modelAndView.addObject("notifications", user.getNotifications());
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);

        log.info("NOTIFICATION " + user.getNotifications().toString());

        user.getNotifications().clear();
        userService.save(user);

        return modelAndView;
    }

    @PostMapping("/fight")
    public String fight(@RequestParam String difficulty,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        fightService.handleFight(user, difficulty);

        return "redirect:/game/underground";
    }

    @GetMapping("/inventory")
    public ModelAndView getInventory(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());

        log.info("Items: [%s]".formatted(user.getGameCharacter().getInventory().getItems()));

        ModelAndView modelAndView = new ModelAndView("inventory");
        modelAndView.addObject("wearings", user.getGameCharacter().getWearings());
        modelAndView.addObject("inventory", user.getGameCharacter().getInventory());
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);

        return modelAndView;
    }

    @PostMapping("/items/equip")
    public ModelAndView equipItem(@RequestParam UUID itemId,
                            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        characterService.equipItem(user.getGameCharacter().getId(), itemId);

        return new ModelAndView("redirect:/game/inventory");
    }

    @PostMapping("/items/unequip")
    public ModelAndView unEquipItem(@RequestParam String slot,
                              @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        characterService.unEquipItem(user.getGameCharacter().getId(), slot);

        return new ModelAndView("redirect:/game/inventory");
    }



    @GetMapping("/shop")
    public ModelAndView getShop(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());
        Shop shop = shopService.getShop();

        ModelAndView modelAndView = new ModelAndView("shop");
        modelAndView.addObject("shopItems", shop.getItems());
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);

        return modelAndView;
    }

    @PostMapping("/shop/buy")
    public ModelAndView buyItem(@RequestParam String itemId,
                                @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        Item item = shopService.buyItem(itemId);
        characterService.buyItem(user.getGameCharacter().getId(), item);

        return new ModelAndView("redirect:/game/shop");
    }


}
