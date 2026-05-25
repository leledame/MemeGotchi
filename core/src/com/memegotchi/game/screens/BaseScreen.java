package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    protected Stage stage;
    protected TextButton moveButton;
    protected BitmapFont font, messageFont;
    protected BottomPanel bottomPanel;
    protected TopPanel topPanel;
    protected ScreenManager screenManager;
    protected PetEngine petEngine;

    protected BitmapFont statsFont;
    protected BitmapFont coinsFont;
    protected String currentMessage = "";
    protected float messageTimer = 0f;
    private boolean wasTouched = false;
    protected static final float CHARACTER_SCALE = 15f;
    protected static final int WORLD_WIDTH = GameResources.SCREEN_WIDTH;
    protected static final int WORLD_HEIGHT = GameResources.SCREEN_HEIGHT;

    // Массив всех возможных локаций для переключения по кругу
    private static final BottomPanelButton.LocationType[] LOCATIONS = {
            BottomPanelButton.LocationType.LIVING,
            BottomPanelButton.LocationType.BEDROOM,
            BottomPanelButton.LocationType.KITCHEN,
            BottomPanelButton.LocationType.TOILET,
            BottomPanelButton.LocationType.WALK
    };
    private int currentLocationIndex = 0;

    public BaseScreen(PetEngine petEngine) {
        this.petEngine = petEngine;
    }

    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    public void showMessage(String text) {
        currentMessage = text;
        messageTimer = 2.5f;
    }

    @Override
    public void show() {
        disposeResources();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        scale = 1f;

        backgroundTexture = new Texture(getBackgroundPath());
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        if (shouldDrawCharacter()) {
            characterTexture = new Texture(getCharacterPath());
            characterTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // Создаём кнопку Move
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        TextureRegionDrawable buttonBg = new TextureRegionDrawable(
                new TextureRegion(new Texture("buttons/text_button/button.png")));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(buttonBg, buttonBg, buttonBg, font);
        moveButton = new TextButton("MOVE", style);
        moveButton.setSize(90, 35);
        moveButton.setPosition(WORLD_WIDTH / 2f - 230, 90);
        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Переключение на следующую локацию по кругу
                currentLocationIndex = (currentLocationIndex + 1) % LOCATIONS.length;
                BottomPanelButton.LocationType nextLocation = LOCATIONS[currentLocationIndex];
                if (screenManager != null) {
                    screenManager.switchToLocation(nextLocation);
                }
            }
        });

        stage = new Stage();
        stage.addActor(moveButton);
        Gdx.input.setInputProcessor(stage);  // важно для работы кнопки

        bottomPanel = new BottomPanel(WORLD_WIDTH, WORLD_HEIGHT, 1f);
        topPanel = new TopPanel(WORLD_WIDTH, WORLD_HEIGHT, 1f, this, screenManager);

        statsFont = new BitmapFont();
        statsFont.getData().setScale(2.0f);
        coinsFont = new BitmapFont();
        coinsFont.getData().setScale(2.0f);
        messageFont = new BitmapFont();
        messageFont.setColor(Color.PINK);
        messageFont.getData().setScale(1.5f);

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
        int startX = (int) (WORLD_WIDTH * 0.05);
        int startY = (int) (WORLD_HEIGHT * 0.86);
        int lineHeight = 40;

        statsFont.setColor(Color.WHITE);
        statsFont.draw(batch, "Hunger", startX, startY);
        statsFont.setColor(getStatColor(pet.getHunger()));
        statsFont.draw(batch, pet.getHunger() + "%", startX + 100, startY);
        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Happy", startX, startY - lineHeight);
        statsFont.setColor(getStatColor(pet.getHappiness()));
        statsFont.draw(batch, pet.getHappiness() + "%", startX + 100, startY - lineHeight);
        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Energy", startX, startY - lineHeight * 2);
        statsFont.setColor(getStatColor(pet.getEnergy()));
        statsFont.draw(batch, pet.getEnergy() + "%", startX + 100, startY - lineHeight * 2);
        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Clean", startX, startY - lineHeight * 3);
        statsFont.setColor(getStatColor(pet.getCleanliness()));
        statsFont.draw(batch, pet.getCleanliness() + "%", startX + 100, startY - lineHeight * 3);
        statsFont.setColor(Color.WHITE);
    }

    protected void onScreenShow() {}

    protected void handleInput() {
        boolean isTouched = Gdx.input.isTouched();
        if (isTouched && !wasTouched) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();
            if (screenManager instanceof MemeGotchi) {
                MemeGotchi game = (MemeGotchi) screenManager;
                com.badlogic.gdx.math.Vector3 worldCoords = game.camera.unproject(new com.badlogic.gdx.math.Vector3(touchX, touchY, 0));
                touchX = (int) worldCoords.x;
                touchY = (int) worldCoords.y;
            }
            // Нижняя панель
            if (bottomPanel.handleTouch(touchX, touchY)) {
                onBottomPanelLocationChanged(bottomPanel.getActiveLocation());
            }
            // Верхняя панель
            topPanel.handleTouch(touchX, touchY);
            // Клик по персонажу
            if (shouldDrawCharacter() && characterTexture != null) {
                int centerX = WORLD_WIDTH / 2;
                int centerY = WORLD_HEIGHT / 2;
                int touchArea = 300;
                if (touchX > centerX - touchArea && touchX < centerX + touchArea &&
                        touchY > centerY - touchArea && touchY < centerY + touchArea) {
                    petEngine.play();
                    showMessage("+15 happy, -5 energy, -5 hunger");
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
        // Синхронизируем индекс для moveButton
        for (int i = 0; i < LOCATIONS.length; i++) {
            if (LOCATIONS[i] == location) {
                currentLocationIndex = i;
                break;
            }
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.9f, 0.85f, 0.8f, 1);
        batch.setProjectionMatrix(((MemeGotchi) screenManager).camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        if (shouldDrawCharacter() && characterTexture != null) {
            float charDrawWidth = characterTexture.getWidth() * CHARACTER_SCALE;
            float charDrawHeight = characterTexture.getHeight() * CHARACTER_SCALE;
            float charX = (WORLD_WIDTH - charDrawWidth) / 2;
            float charY = (WORLD_HEIGHT - charDrawHeight) / 2 - 30;
            batch.draw(characterTexture, charX, charY, charDrawWidth, charDrawHeight);
        }

        drawStats();
        if (messageTimer > 0) {
            messageTimer -= delta;
            float msgX = WORLD_WIDTH / 2f - messageFont.getRegion().getRegionWidth() / 2f;
            messageFont.draw(batch, currentMessage, msgX, WORLD_HEIGHT - 80);
        }
        batch.end();

        bottomPanel.render(batch, shapeRenderer);
        topPanel.render(batch, shapeRenderer);

        // Рисуем Stage (кнопку Move) поверх всего
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (screenManager instanceof MemeGotchi) {
            MemeGotchi game = (MemeGotchi) screenManager;
            game.viewport.update(width, height, true);
            game.camera.update();
            batch.setProjectionMatrix(game.camera.combined);
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    private void disposeResources() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (characterTexture != null) characterTexture.dispose();
        if (bottomPanel != null) bottomPanel.dispose();
        if (topPanel != null) topPanel.dispose();
        if (statsFont != null) statsFont.dispose();
        if (coinsFont != null) coinsFont.dispose();
        if (messageFont != null) messageFont.dispose();
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
    }

    @Override
    public void dispose() {
        disposeResources();
    }

    public abstract String getBackgroundPath();
    public abstract String getCharacterPath();
    public abstract boolean shouldDrawCharacter();
}