package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.engine.PetEngine;

public class ShopScreen extends BaseScreen {
    private Button backButton;
    private Button buyFoodButton;
    private Button buyToyButton;
    private Button buyCleanButton;
    private BitmapFont titleFont;
    private BitmapFont priceFont;
    private BitmapFont buttonFont;

    public ShopScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public void show() {
        super.show();

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        priceFont = new BitmapFont();
        priceFont.getData().setScale(1.8f);

        int buttonWidth = 350;
        int buttonHeight = 70;
        int centerX = (GameResources.SCREEN_WIDTH - buttonWidth) / 2;
        int startY = GameResources.SCREEN_HEIGHT - 250;

        backButton = new Button(30, GameResources.SCREEN_HEIGHT - 80, 100, 50, buttonFont, GameResources.BUTTON_TEXT, "Back");
        buyFoodButton = new Button(centerX, startY, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "🍕 Food (10💰)");
        buyToyButton = new Button(centerX, startY - 90, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "🎾 Toy (20💰)");
        buyCleanButton = new Button(centerX, startY - 180, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "🧼 Shampoo (15💰)");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.3f, 1);

        batch.begin();

        // Фон магазина
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT);
        }

        titleFont.setColor(Color.GOLD);
        titleFont.draw(batch, "🛒 SHOP 🛒", GameResources.SCREEN_WIDTH / 2f - 100, GameResources.SCREEN_HEIGHT - 100);

        priceFont.setColor(Color.GOLD);
        priceFont.draw(batch, "💰 " + petEngine.getPet().getCoins() + " coins",
                GameResources.SCREEN_WIDTH / 2f - 80, GameResources.SCREEN_HEIGHT - 160);

        buyFoodButton.render(batch, false);
        buyToyButton.render(batch, false);
        buyCleanButton.render(batch, false);
        backButton.render(batch, false);

        batch.end();

        handleInput();
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = GameResources.SCREEN_HEIGHT - Gdx.input.getY();

            if (backButton.contains(x, y) && screenManager != null) {
                screenManager.backToPreviousScreen();
                return;
            }

            if (buyFoodButton.contains(x, y)) {
                if (petEngine.getPet().getCoins() >= 10) {
                    petEngine.getPet().setCoins(petEngine.getPet().getCoins() - 10);
                    petEngine.feed();
                }
            } else if (buyToyButton.contains(x, y)) {
                if (petEngine.getPet().getCoins() >= 20) {
                    petEngine.getPet().setCoins(petEngine.getPet().getCoins() - 20);
                    petEngine.play();
                }
            } else if (buyCleanButton.contains(x, y)) {
                if (petEngine.getPet().getCoins() >= 15) {
                    petEngine.getPet().setCoins(petEngine.getPet().getCoins() - 15);
                    petEngine.clean();
                }
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
        // Магазин не требует дополнительной инициализации
    }

    @Override
    public CatRoomState getCatRoomState() {
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titleFont != null) titleFont.dispose();
        if (priceFont != null) priceFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}