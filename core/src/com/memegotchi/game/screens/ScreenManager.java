package com.memegotchi.game.screens;

import com.memegotchi.game.buttons.BottomPanelButton.LocationType;

public interface ScreenManager {
    void switchToLocation(LocationType location);
    void switchToShop();
    void backToPreviousScreen();
    CatRoomState getCurrentCatRoom();
    void setCatRoom(CatRoomState room);

    void switchToSettings();
}