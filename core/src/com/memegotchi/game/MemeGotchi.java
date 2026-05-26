package com.memegotchi.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.memegotchi.game.buttons.BottomPanelButton.LocationType;
import com.memegotchi.game.engine.PetEngine;
import com.memegotchi.game.engine.SoundManager;
import com.memegotchi.game.model.PetModel;
import com.memegotchi.game.screens.*;
import com.memegotchi.game.storage.GameStorage;

public class MemeGotchi extends Game implements ScreenManager {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;

    private PetModel pet;
    private PetEngine petEngine;
    private GameStorage storage;

    // Экраны
    private StartScreen startScreen;
    public LivingRoomScreen livingRoomScreen;
    private BedroomScreen bedroomScreen;
    private KitchenScreen kitchenScreen;
    private BathroomScreen bathroomScreen;
    private FishingScreen fishingScreen;
    private ShopScreen shopScreen;

    private BaseScreen currentScreen;
    private BaseScreen previousScreen;

    private CatRoomState currentCatRoom = CatRoomState.LIVING;
    private SettingsScreen settingsScreen;
    private SoundManager soundManager;


    @Override
    public CatRoomState getCurrentCatRoom() {
        return currentCatRoom;
    }

    @Override
    public void setCatRoom(CatRoomState room) {
        this.currentCatRoom = room;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT, camera);
        camera.position.set(GameResources.SCREEN_WIDTH / 2f, GameResources.SCREEN_HEIGHT / 2f, 0);
        camera.update();

        storage = new GameStorage();
        pet = storage.load();

        // Обновляем питомца после загрузки
        long now = System.currentTimeMillis();
        long minutesPassed = Math.max(0, (now - pet.getLastUpdateTime()) / (1000 * 60));
        petEngine = new PetEngine(pet);
        if (minutesPassed > 0) {
            petEngine.updateOffline(minutesPassed);
            pet.setLastUpdateTime(now);
            storage.save(pet);
        }

        // Инициализация экранов
        startScreen = new StartScreen(this);
        livingRoomScreen = new LivingRoomScreen(petEngine);
        bedroomScreen = new BedroomScreen(petEngine);
        kitchenScreen = new KitchenScreen(petEngine);
        bathroomScreen = new BathroomScreen(petEngine);
        fishingScreen = new FishingScreen(petEngine);
        shopScreen = new ShopScreen(petEngine);

        livingRoomScreen.setScreenManager(this);
        bedroomScreen.setScreenManager(this);
        kitchenScreen.setScreenManager(this);
        bathroomScreen.setScreenManager(this);
        fishingScreen.setScreenManager(this);
        shopScreen.setScreenManager(this);

        setScreen(startScreen);
        currentScreen = null;
        // в create() после инициализации других экранов:
        settingsScreen = new SettingsScreen(petEngine);
        settingsScreen.setScreenManager(this);

        soundManager = new SoundManager();
        soundManager.loadSettings(storage.isMusicOn(), storage.isSoundOn());
        if (storage.isMusicOn()) soundManager.playBackground();




    }

    @Override
    public void switchToSettings() {
        if (currentScreen != settingsScreen) {
            previousScreen = currentScreen;
            setScreen(settingsScreen);
            currentScreen = settingsScreen;
        }
    }
    public void updateMusic() {
        if (soundManager != null) soundManager.setMusicEnabled(storage.isMusicOn());
    }

    public void updateSound() {
        if (soundManager != null) soundManager.setSoundEnabled(storage.isSoundOn());
    }

    @Override
    public void switchToLocation(LocationType location) {
        BaseScreen targetScreen = resolveScreen(location);
        if (targetScreen == null) return;

        if (currentScreen != null && currentScreen != shopScreen) {
            storage.save(pet);
        }

        previousScreen = currentScreen;
        setScreen(targetScreen);
        currentScreen = targetScreen;
        currentScreen.setActiveLocation(location);
    }

    @Override
    public void switchToShop() {
        if (currentScreen != shopScreen) {
            previousScreen = currentScreen;
            setScreen(shopScreen);
            currentScreen = shopScreen;
        }
    }

    @Override
    public void backToPreviousScreen() {
        if (previousScreen != null && previousScreen != shopScreen) {
            setScreen(previousScreen);
            currentScreen = previousScreen;
            if (currentScreen != null) {
                LocationType location = getLocationForScreen(currentScreen);
                if (location != null) {
                    currentScreen.setActiveLocation(location);
                }
            }
        } else if (currentScreen == shopScreen) {
            switchToLocation(LocationType.LIVING);
        }
    }

    private LocationType getLocationForScreen(BaseScreen screen) {
        if (screen instanceof LivingRoomScreen) return LocationType.LIVING;
        if (screen instanceof BedroomScreen) return LocationType.BEDROOM;
        if (screen instanceof KitchenScreen) return LocationType.KITCHEN;
        if (screen instanceof BathroomScreen) return LocationType.TOILET;
        if (screen instanceof FishingScreen) return LocationType.WALK;
        return LocationType.LIVING;
    }

    private BaseScreen resolveScreen(LocationType location) {
        switch (location) {
            case BEDROOM: return bedroomScreen;
            case LIVING:  return livingRoomScreen;
            case KITCHEN: return kitchenScreen;
            case TOILET:  return bathroomScreen;
            case WALK:    return fishingScreen;
            default:      return livingRoomScreen;
        }
    }

    @Override
    public void render() {
        // Обновляем вьюпорт и камеру перед рендером
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Обновляем питомца только в игровых экранах
        if (currentScreen != null && currentScreen != shopScreen) {
            petEngine.update(0.016f);
        }
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(GameResources.SCREEN_WIDTH / 2f, GameResources.SCREEN_HEIGHT / 2f, 0);
        camera.update();

        // Уведомляем текущий экран о ресайзе
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (pet != null) {
            storage.save(pet);
        }

        if (startScreen != null) startScreen.dispose();
        if (livingRoomScreen != null) livingRoomScreen.dispose();
        if (bedroomScreen != null) bedroomScreen.dispose();
        if (kitchenScreen != null) kitchenScreen.dispose();
        if (bathroomScreen != null) bathroomScreen.dispose();
        if (fishingScreen != null) fishingScreen.dispose();
        if (shopScreen != null) shopScreen.dispose();
        batch.dispose();
        super.dispose();
    }
}