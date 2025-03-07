package com.supplyboost.chero.game.stats.repository;

import com.supplyboost.chero.game.stats.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StatsRepository extends JpaRepository<Stats, UUID> {

}
