package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.FontBuilder;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.buttons.BottomPanelButton;
import com.memegotchi.game.engine.PetEngine;

public class ShopScreen extends BaseScreen {
    private Button backButton;
    private Button buyFoodButton;
    private Button buyCleanButton;
    private Button buyEnergyButton;
    private BitmapFont titleFont;
    private BitmapFont priceFont;
    private BitmapFont buttonFont;

    public ShopScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public void show() {
        super.show();

        titleFont = FontBuilder.generate(52, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf");
        priceFont = FontBuilder.generate(28, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf");
        buttonFont = FontBuilder.generate(40, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf");

        int buttonWidth = 500;
        int buttonHeight = 90;
        int centerX = (GameResources.SCREEN_WIDTH - buttonWidth) / 2;
        int downOffset = (int)(GameResources.SCREEN_HEIGHT * 0.01f);
        int startY = GameResources.SCREEN_HEIGHT - 300 - downOffset;

        backButton = new Button(25, GameResources.SCREEN_HEIGHT - 110 - downOffset, 280, 90, buttonFont, GameResources.BUTTON_TEXT, "BACK");
        buyFoodButton = new Button(centerX, startY, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "Food (5$)");
        buyCleanButton = new Button(centerX, startY - 100, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "Shampoo (15$)");
        buyEnergyButton = new Button(centerX, startY - 200, buttonWidth, buttonHeight, buttonFont, GameResources.BUTTON_TEXT, "Energy Potion (15$)");

        buyFoodButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buyCleanButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buyEnergyButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
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

        int downOffset = (int)(GameResources.SCREEN_HEIGHT * 0.01f);
        titleFont.setColor(Color.GOLD);
        layout.setText(titleFont, "SHOP");
        float shopY = GameResources.SCREEN_HEIGHT - 130 - downOffset;
        titleFont.draw(batch, "SHOP", (GameResources.SCREEN_WIDTH - layout.width) / 2f, shopY);
        float shopBottomY = shopY - layout.height;

        float buttonTopY = (GameResources.SCREEN_HEIGHT - 300 - downOffset) + 90;
        float gapCenterY = (shopBottomY + buttonTopY) / 2f;
        layout.setText(priceFont, petEngine.getPet().getCoins() + " coins");
        float coinsY = gapCenterY - (priceFont.getAscent() + priceFont.getDescent()) / 2f;
        priceFont.draw(batch, petEngine.getPet().getCoins() + " coins",
                (GameResources.SCREEN_WIDTH - layout.width) / 2f, coinsY);

        buyFoodButton.render(batch, false);
        buyCleanButton.render(batch, false);
        buyEnergyButton.render(batch, false);
        backButton.render(batch, false);

        batch.end();

        handleInput();
    }

    @Override
    protected void handleInput() {
            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                if (screenManager instanceof MemeGotchi) {
                    BaseScreen.screenToWorld((MemeGotchi) screenManager, touchPos);
                }
            int x = (int) touchPos.x;
            int y = (int) touchPos.y;

            if (backButton.contains(x, y) && screenManager != null) {
                screenManager.backToPreviousScreen();
                return;
            }
            if (buyFoodButton.contains(x, y)) {
                if (petEngine.getPet().getCoins() >= 5) {
                    petEngine.getPet().setCoins(petEngine.getPet().getCoins() - 5);
                    petEngine.getPet().addFood();
                    if (screenManager instanceof MemeGotchi) {
                        ((MemeGotchi) screenManager).getSoundManager().playCash();
                    }
                    screenManager.switchToLocation(BottomPanelButton.LocationType.KITCHEN);
                    return;
                } else showMessage("Not enough coins!");

            } else if (buyCleanButton.contains(x, y)) {
                if (petEngine.getPet().getCoins() >= 15) {
                    petEngine.getPet().setCoins(petEngine.getPet().getCoins() - 15);
                    petEngine.getPet().addShampoo();
                    if (screenManager instanceof MemeGotchi) {
                        ((MemeGotchi) screenManager).getSoundManager().playCash();
                    }
                    showMessage("+1 Shampoo!");
                } else {
                    showMessage("Not enough coins!");
                }

            } else if (buyEnergyButton.contains(x, y)) {
                if (petEngine.getPet().getCoins() >= 15) {
                    if (petEngine.getPet().getEnergy() < 100) {
                        petEngine.getPet().setCoins(petEngine.getPet().getCoins() - 15);
                        petEngine.getPet().setEnergy(100);
                        if (screenManager instanceof MemeGotchi) {
                            ((MemeGotchi) screenManager).getSoundManager().playCash();
                        }
                        showMessage("Energy restored to 100%!");
                    } else {
                        showMessage("Already full energy!");
                    }
                } else {
                    showMessage("Not enough coins!");
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
