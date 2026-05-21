package com.memegotchi.game.screens;

import com.memegotchi.game.GameResources;
import com.memegotchi.game.engine.PetEngine;

public class BedroomScreen extends BaseScreen {

    public BedroomScreen(PetEngine petEngine) {
        super(petEngine);
    }
    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_BEDROOM_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }
    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.BEDROOM;
    }
    @Override
    public boolean shouldDrawCharacter() {
        return false;
    }
}