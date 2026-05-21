package com.memegotchi.game.screens;

import com.memegotchi.game.GameResources;
import com.memegotchi.game.engine.PetEngine;

public class KitchenScreen extends BaseScreen {

    public KitchenScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_KITCHEN_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }
    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.KITCHEN;
    }
    @Override
    public boolean shouldDrawCharacter() {
        return false;
    }
}