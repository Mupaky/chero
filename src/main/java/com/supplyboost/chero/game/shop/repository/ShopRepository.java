package com.supplyboost.chero.game.shop.repository;

import com.supplyboost.chero.game.shop.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {
}
