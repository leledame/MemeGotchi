package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.BottomPanelButton;
import com.memegotchi.game.engine.PetEngine;
import com.memegotchi.game.panels.BottomPanel;
import com.memegotchi.game.panels.TopPanel;

public abstract class BaseScreen extends ScreenAdapter {
    protected SpriteBatch batch;
    protected ShapeRenderer shapeRenderer;
    protected Texture backgroundTexture;
    protected Texture characterTexture;
    protected float scale;
    protected BottomPanel bottomPanel;
    protected TopPanel topPanel;
    protected ScreenManager screenManager;
    protected PetEngine petEngine;

    protected BitmapFont statsFont;
    protected BitmapFont coinsFont;

    private boolean wasTouched = false;
    protected static final float CHARACTER_SCALE = 15f;

    // Фиксированные размеры игры
    protected static final int WORLD_WIDTH = GameResources.SCREEN_WIDTH;
    protected static final int WORLD_HEIGHT = GameResources.SCREEN_HEIGHT;

    public BaseScreen(PetEngine petEngine) {
        this.petEngine = petEngine;
    }

    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        disposeResources();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Используем мировые координаты (фиксированные)
        scale = 1f; // Не используем масштаб, так как FitViewport уже масштабирует

        backgroundTexture = new Texture(getBackgroundPath());
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        if (shouldDrawCharacter()) {
            characterTexture = new Texture(getCharacterPath());
            characterTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // Панели создаются с мировыми координатами
        bottomPanel = new BottomPanel(WORLD_WIDTH, WORLD_HEIGHT, 1f);
        topPanel = new TopPanel(WORLD_WIDTH, WORLD_HEIGHT, 1f, this);

        statsFont = new BitmapFont();
        statsFont.getData().setScale(1.5f);

        coinsFont = new BitmapFont();
        coinsFont.getData().setScale(1.8f);

        wasTouched = false;
        onScreenShow();
    }

    public abstract CatRoomState getCatRoomState();

    private Color getStatColor(int value) {
        if (value >= 70) return Color.GREEN;
        if (value >= 40) return Color.YELLOW;
        return Color.RED;
    }

    protected void drawStats() {
        if (petEngine == null) return;

        var pet = petEngine.getPet();
        int startX = (int) (GameResources.SCREEN_WIDTH * 0.05);
        int startY = (int) (WORLD_HEIGHT * 0.86);
        int lineHeight = 40;

        statsFont.setColor(Color.WHITE);

        // Голод 🍕
        statsFont.draw(batch, "🍕", startX, startY);
        statsFont.setColor(getStatColor(pet.getHunger()));
        statsFont.draw(batch, pet.getHunger() + "%", startX + 45, startY);
        statsFont.setColor(Color.WHITE);

        // Счастье 😊
        statsFont.draw(batch, "😊", startX, startY - lineHeight);
        statsFont.setColor(getStatColor(pet.getHappiness()));
        statsFont.draw(batch, pet.getHappiness() + "%", startX + 45, startY - lineHeight);
        statsFont.setColor(Color.WHITE);

        // Энергия ⚡
        statsFont.draw(batch, "⚡", startX, startY - lineHeight * 2);
        statsFont.setColor(getStatColor(pet.getEnergy()));
        statsFont.draw(batch, pet.getEnergy() + "%", startX + 45, startY - lineHeight * 2);
        statsFont.setColor(Color.WHITE);

        // Чистота 🧼
        statsFont.draw(batch, "🧼", startX, startY - lineHeight * 3);
        statsFont.setColor(getStatColor(pet.getCleanliness()));
        statsFont.draw(batch, pet.getCleanliness() + "%", startX + 45, startY - lineHeight * 3);
        statsFont.setColor(Color.WHITE);

        // Монеты
        coinsFont.setColor(Color.GOLD);
        coinsFont.draw(batch, "💰 " + pet.getCoins(), WORLD_WIDTH - 130, WORLD_HEIGHT - 40);
    }

    protected void onScreenShow() {}

    protected void handleInput() {
        boolean isTouched = Gdx.input.isTouched();

        if (isTouched && !wasTouched) {
            // Получаем координаты касания в мировых координатах
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();

            // Конвертируем экранные координаты в мировые
            if (screenManager instanceof MemeGotchi) {
                MemeGotchi game = (MemeGotchi) screenManager;
                com.badlogic.gdx.math.Vector3 worldCoords = game.camera.unproject(new com.badlogic.gdx.math.Vector3(touchX, touchY, 0));
                touchX = (int) worldCoords.x;
                touchY = (int) worldCoords.y;
            }

            if (bottomPanel.handleTouch(touchX, touchY)) {
                onBottomPanelLocationChanged(bottomPanel.getActiveLocation());
            }

            if (topPanel.handleTouch(touchX, touchY)) {
                if (screenManager != null) {
                    screenManager.switchToShop();
                }
            }

            // Клик по персонажу для игры
            if (shouldDrawCharacter() && characterTexture != null) {
                int centerX = WORLD_WIDTH / 2;
                int centerY = WORLD_HEIGHT / 2;
                int touchArea = 300;
                if (touchX > centerX - touchArea && touchX < centerX + touchArea &&
                        touchY > centerY - touchArea && touchY < centerY + touchArea) {
                    petEngine.play();
                }
            }
        }

        wasTouched = isTouched;
    }

    protected void onBottomPanelLocationChanged(BottomPanelButton.LocationType location) {
        if (screenManager != null) {
            screenManager.switchToLocation(location);
        }
    }

    public void setActiveLocation(BottomPanelButton.LocationType location) {
        if (bottomPanel != null) {
            bottomPanel.setActiveLocation(location);
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.9f, 0.85f, 0.8f, 1);

        batch.setProjectionMatrix(((MemeGotchi) screenManager).camera.combined);

        batch.begin();
        // Рисуем фон на весь мир
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        if (shouldDrawCharacter() && characterTexture != null) {
            float charDrawWidth = characterTexture.getWidth() * CHARACTER_SCALE;
            float charDrawHeight = characterTexture.getHeight() * CHARACTER_SCALE;
            float charX = (WORLD_WIDTH - charDrawWidth) / 2;
            float charY = (WORLD_HEIGHT - charDrawHeight) / 2 - 30;
            batch.draw(characterTexture, charX, charY, charDrawWidth, charDrawHeight);
        }

        drawStats();
        batch.end();

        bottomPanel.render(batch, shapeRenderer);
        topPanel.render(batch, shapeRenderer);
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем вьюпорт через MemeGotchi
        if (screenManager instanceof MemeGotchi) {
            MemeGotchi game = (MemeGotchi) screenManager;
            game.viewport.update(width, height, true);
            game.camera.update();
            batch.setProjectionMatrix(game.camera.combined);
        }
    }

    @Override
    public void hide() {}

    private void disposeResources() {
        if (batch != null) { batch.dispose(); batch = null; }
        if (shapeRenderer != null) { shapeRenderer.dispose(); shapeRenderer = null; }
        if (backgroundTexture != null) { backgroundTexture.dispose(); backgroundTexture = null; }
        if (characterTexture != null) { characterTexture.dispose(); characterTexture = null; }
        if (bottomPanel != null) { bottomPanel.dispose(); bottomPanel = null; }
        if (topPanel != null) { topPanel.dispose(); topPanel = null; }
        if (statsFont != null) { statsFont.dispose(); statsFont = null; }
        if (coinsFont != null) { coinsFont.dispose(); coinsFont = null; }
    }

    @Override
    public void dispose() {
        disposeResources();
    }

    public abstract String getBackgroundPath();
    public abstract String getCharacterPath();
    public abstract boolean shouldDrawCharacter();
}