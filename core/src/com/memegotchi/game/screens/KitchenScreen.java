package com.memegotchi.game.screens;

import com.memegotchi.game.GameResources;

public class KitchenScreen extends BaseScreen {
    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_KITCHEN_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }

    @Override
    public boolean shouldDrawCharacter() {
        return false;
    }

    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.KITCHEN;
    }
}