package com.supplyboost.chero.game.shop.service;

import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.shop.model.Shop;
import com.supplyboost.chero.game.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
public class ShopService {

    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Item buyItem(String itemId){
        Shop shop = getShop();

        return shop.getItems().stream().filter(item1 -> item1.getId().equals(UUID.fromString(itemId)))
                .findFirst()
                .orElse(null);
    }

    public Shop getShop(){
        return shopRepository.findAll().stream().findFirst().orElse(null);
    }


}
