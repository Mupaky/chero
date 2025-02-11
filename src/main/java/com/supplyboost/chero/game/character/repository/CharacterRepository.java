package com.supplyboost.chero.game.character.repository;

import com.supplyboost.chero.game.character.model.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<GameCharacter, UUID> {


}
