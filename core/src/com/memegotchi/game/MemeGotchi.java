package com.memegotchi.game;

import com.badlogic.gdx.Game;
import com.memegotchi.game.buttons.BottomPanelButton.LocationType;
import com.memegotchi.game.screens.BaseScreen;
import com.memegotchi.game.screens.BathroomScreen;
import com.memegotchi.game.screens.BedroomScreen;
import com.memegotchi.game.screens.KitchenScreen;
import com.memegotchi.game.screens.LivingRoomScreen;
import com.memegotchi.game.screens.FishingScreen;
import com.memegotchi.game.screens.CatRoomState;
import com.memegotchi.game.screens.ScreenManager;

public class MemeGotchi extends Game implements ScreenManager {
    private LivingRoomScreen livingRoomScreen;
    private BedroomScreen bedroomScreen;
    private FishingScreen fishingScreen;
    private KitchenScreen kitchenScreen;
    private BathroomScreen bathroomScreen;
    private CatRoomState currentCatRoom = CatRoomState.LIVING;
    private BaseScreen currentScreen;

    public CatRoomState getCurrentCatRoom() { return currentCatRoom; }
    public void setCatRoom(CatRoomState room) { this.currentCatRoom = room; }

    @Override
    public void create() {
        livingRoomScreen = new LivingRoomScreen();
        bedroomScreen = new BedroomScreen();
        kitchenScreen = new KitchenScreen();
        fishingScreen = new FishingScreen();
        bathroomScreen = new BathroomScreen();

        livingRoomScreen.setScreenManager(this);
        bedroomScreen.setScreenManager(this);
        kitchenScreen.setScreenManager(this);
        fishingScreen.setScreenManager(this);
        bathroomScreen.setScreenManager(this);

        setScreen(livingRoomScreen);
        currentScreen = livingRoomScreen;
        currentScreen.setActiveLocation(LocationType.LIVING);
    }

    public void switchToLocation(LocationType location) {
        BaseScreen targetScreen = resolveScreen(location);
        if (targetScreen == null || targetScreen == currentScreen) return;

        setScreen(targetScreen);
        currentScreen = targetScreen;
        currentScreen.setActiveLocation(location);
    }

    private BaseScreen resolveScreen(LocationType location) {
        switch (location) {
            case BEDROOM:
                return bedroomScreen;
            case KITCHEN:
                return kitchenScreen;
            case TOILET:
                return bathroomScreen;
            case LIVING:
                return livingRoomScreen;
            case WALK:
                return fishingScreen;
            default:
                return null;
        }
    }

    @Override
    public void dispose() {
        if (livingRoomScreen != null) livingRoomScreen.dispose();
        if (bedroomScreen != null) bedroomScreen.dispose();
        if (kitchenScreen != null) kitchenScreen.dispose();
        if (fishingScreen != null) fishingScreen.dispose();
        if (bathroomScreen != null) bathroomScreen.dispose();
        super.dispose();
    }
}