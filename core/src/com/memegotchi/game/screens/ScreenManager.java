package com.memegotchi.game.screens;

import com.memegotchi.game.buttons.BottomPanelButton.LocationType;
import com.memegotchi.game.screens.CatRoomState;

public interface ScreenManager {
    void switchToLocation(LocationType location);
    CatRoomState getCurrentCatRoom();
    void setCatRoom(CatRoomState room);
}