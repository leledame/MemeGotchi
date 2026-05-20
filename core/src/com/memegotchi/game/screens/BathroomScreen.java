package com.memegotchi.game.screens;

import com.memegotchi.game.GameResources;

public class BathroomScreen extends BaseScreen {
    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_BATHROOM_DAY;
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
        return CatRoomState.TOILET;
    }
}
