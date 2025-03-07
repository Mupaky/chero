package com.supplyboost.chero.game.controller;


import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.shop.model.Shop;
import com.supplyboost.chero.game.shop.service.ShopService;
import com.supplyboost.chero.game.stats.model.StatType;
import com.supplyboost.chero.game.stats.service.StatsService;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/game")
public class GameController {

    private final UserService userService;
    private final ShopService shopService;
    private final CharacterService characterService;

    @Autowired
    public GameController(UserService userService, ShopService shopService, CharacterService characterService, StatsService statsService) {
        this.userService = userService;
        this.shopService = shopService;
        this.characterService = characterService;
    }


    @GetMapping("/dashboard")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/train-stats")
    public ModelAndView getTrainingHall(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("train-stats");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/train")
    public ModelAndView trainStats(@RequestParam String stat,
                                @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        StatType statType = StatType.valueOf(stat);

        characterService.trainStat(
                user.getGameCharacter().getId(),
                user.getGameCharacter().getStats().getId(),
                statType);

        ModelAndView modelAndView = new ModelAndView("redirect:/game/train-stats");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/underground")
    public ModelAndView getUnderground(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("underground");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/inventory")
    public ModelAndView getInventory(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        log.info("Items: [%s]".formatted(user.getGameCharacter().getInventory().getItems()));

        ModelAndView modelAndView = new ModelAndView("inventory");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/shop")
    public ModelAndView getShop(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        Shop shop = shopService.getShop();

        ModelAndView modelAndView = new ModelAndView("shop");
        modelAndView.addObject("user", user);
        modelAndView.addObject("shopItems", shop.getItems());

        return modelAndView;
    }

    @PostMapping("/shop/buy")
    public ModelAndView buyItem(@RequestParam String itemId,
                                @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        Item item = shopService.buyItem(itemId);
        characterService.buyItem(user.getGameCharacter().getId(), item);


        ModelAndView modelAndView = new ModelAndView("redirect:/game/shop");
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
