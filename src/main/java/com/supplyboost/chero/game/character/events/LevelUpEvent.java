package com.supplyboost.chero.game.character.events;

import com.supplyboost.chero.game.character.model.GameCharacter;
import org.springframework.context.ApplicationEvent;

public class LevelUpEvent extends ApplicationEvent {

    private final GameCharacter character;

    public LevelUpEvent(Object source, GameCharacter character) {
        super(source);
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return character;
    }
}
