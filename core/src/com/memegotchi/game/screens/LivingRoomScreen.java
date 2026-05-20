package com.memegotchi.game.screens;

import com.memegotchi.game.GameResources;

public class LivingRoomScreen extends BaseScreen {
    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }

    @Override
    public boolean shouldDrawCharacter() {
        return true;
    }

    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.LIVING;
    }
}