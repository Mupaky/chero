package com.supplyboost.chero.game.shop;

import com.supplyboost.chero.game.item.model.Item;
import com.supplyboost.chero.game.shop.model.Shop;
import com.supplyboost.chero.game.shop.repository.ShopRepository;
import com.supplyboost.chero.game.shop.service.ShopService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShopServiceUTest {

    @Mock
    private ShopRepository shopRepository;

    @Spy
    @InjectMocks
    private ShopService shopService;

    @Test
    void givenExistingItemId_whenBuyItem_thenReturnItem() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.builder()
                .id(itemId)
                .name("Test Item")
                .build();

        Shop shop = Shop.builder()
                .items(List.of(item))
                .build();

        when(shopRepository.findAll()).thenReturn(List.of(shop));

        Item result = shopService.buyItem(itemId.toString());

        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    void givenNonExistingItemId_whenBuyItem_thenReturnNull() {
        UUID existingItemId = UUID.randomUUID();
        UUID nonExistingItemId = UUID.randomUUID();

        Item item = Item.builder()
                .id(existingItemId)
                .name("Test Item")
                .build();

        Shop shop = Shop.builder()
                .items(List.of(item))
                .build();

        when(shopRepository.findAll()).thenReturn(List.of(shop));

        Item result = shopService.buyItem(nonExistingItemId.toString());

        assertNull(result);
    }

    @Test
    void givenNoShops_whenBuyItem_thenReturnNull() {
        List<Shop> shops = new ArrayList<>();
        shops.add(new Shop());

        when(shopRepository.findAll()).thenReturn(shops);

        Item result = shopService.buyItem(UUID.randomUUID().toString());

        assertNull(result);
    }
}
