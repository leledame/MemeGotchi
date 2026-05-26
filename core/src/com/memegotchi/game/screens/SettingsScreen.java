package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.engine.PetEngine;

public class SettingsScreen extends BaseScreen {
    private Button musicButton;
    private Button soundButton;
    private Button resetButton;
    private Button backButton;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private boolean musicOn = true;
    private boolean soundOn = true;

    public SettingsScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public void show() {
        super.show();

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(3.0f);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.0f);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        com.memegotchi.game.storage.GameStorage storage = new com.memegotchi.game.storage.GameStorage();
        musicOn = storage.isMusicOn();
        soundOn = storage.isSoundOn();

        int buttonWidth = 500;
        int buttonHeight = 90;
        int centerX = (GameResources.SCREEN_WIDTH - buttonWidth) / 2;
        int startY = GameResources.SCREEN_HEIGHT - 300;

        backButton = new Button(25, GameResources.SCREEN_HEIGHT - 110, 280, 90, buttonFont, GameResources.BUTTON_TEXT, "BACK");
        musicButton = new Button(centerX, startY, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "Music: " + (musicOn ? "ON" : "OFF"));
        soundButton = new Button(centerX, startY - 100, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "Sounds: " + (soundOn ? "ON" : "OFF"));
        resetButton = new Button(centerX, startY - 200, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "Reset");

        musicButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        soundButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        resetButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        backButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.3f, 1);

        batch.begin();

        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT);
        }

        GlyphLayout layout = new GlyphLayout();

        titleFont.setColor(Color.GOLD);
        layout.setText(titleFont, "SETTINGS");
        titleFont.draw(batch, "SETTINGS", (GameResources.SCREEN_WIDTH - layout.width) / 2f, GameResources.SCREEN_HEIGHT - 130);

        musicButton.render(batch, false);
        soundButton.render(batch, false);
        resetButton.render(batch, false);
        backButton.render(batch, false);

        batch.end();

        handleInput();
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            if (screenManager instanceof MemeGotchi) {
                ((MemeGotchi) screenManager).getSoundManager().playClick();
            }
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            if (screenManager instanceof MemeGotchi) {
                BaseScreen.screenToWorld((MemeGotchi) screenManager, touchPos);
            }
            int x = (int) touchPos.x;
            int y = (int) touchPos.y;

            if (backButton != null && backButton.contains(x, y) && screenManager != null) {
                screenManager.backToPreviousScreen();
                return;
            }
            if (musicButton != null && musicButton.contains(x, y)) {
                musicOn = !musicOn;
                com.memegotchi.game.storage.GameStorage storage = new com.memegotchi.game.storage.GameStorage();
                storage.saveMusicSettings(musicOn);
                musicButton.setText("Music: " + (musicOn ? "ON" : "OFF"), buttonFont);
                if (screenManager instanceof MemeGotchi) {
                    ((MemeGotchi) screenManager).updateMusic();
                }
                return;
            }
            if (soundButton != null && soundButton.contains(x, y)) {
                soundOn = !soundOn;
                com.memegotchi.game.storage.GameStorage storage = new com.memegotchi.game.storage.GameStorage();
                storage.saveSoundSettings(soundOn);
                soundButton.setText("Sounds: " + (soundOn ? "ON" : "OFF"), buttonFont);
                if (screenManager instanceof MemeGotchi) {
                    ((MemeGotchi) screenManager).updateSound();
                }
                return;
            }
            if (resetButton != null && resetButton.contains(x, y)) {
                com.memegotchi.game.storage.GameStorage storage = new com.memegotchi.game.storage.GameStorage();
                storage.clear();
                if (petEngine != null) {
                    petEngine.getPet().setHunger(100);
                    petEngine.getPet().setHappiness(100);
                    petEngine.getPet().setEnergy(100);
                    petEngine.getPet().setCleanliness(100);
                    petEngine.getPet().setCoins(100);
                }
                showMessage("Game reset!");
                return;
            }
        }
    }

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
        return false;
    }

    @Override
    protected void onScreenShow() {
    }

    @Override
    public CatRoomState getCatRoomState() {
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}
