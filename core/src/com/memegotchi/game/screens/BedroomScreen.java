package com.memegotchi.game.screens;

import com.memegotchi.game.GameResources;

public class BedroomScreen extends BaseScreen {
    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_BEDROOM_DAY;
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
        return CatRoomState.BEDROOM;
    }
}