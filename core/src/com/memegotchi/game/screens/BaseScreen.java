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
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.engine.PetEngine;
import com.memegotchi.game.panels.BottomPanel;
import com.memegotchi.game.panels.TopPanel;

public abstract class BaseScreen extends ScreenAdapter {
    protected SpriteBatch batch;
    protected ShapeRenderer shapeRenderer;
    protected Texture backgroundTexture;
    protected Texture currentCharacterTexture;
    private Texture baseTexture, happyTexture, sadTexture, sleepyTexture;

    protected float scale;
    protected Button moveButton;
    protected BitmapFont font, messageFont, moveFont;
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

        baseTexture = new Texture("charachters/female_cat/" + getCharacterFolder() + "/base.png");
        happyTexture = new Texture("charachters/female_cat/" + getCharacterFolder() + "/happy.png");
        sadTexture = new Texture("charachters/female_cat/" + getCharacterFolder() + "/sad.png");
        sleepyTexture = new Texture("charachters/female_cat/" + getCharacterFolder() + "/sleepy.png");
        baseTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        happyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        sadTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        sleepyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        currentCharacterTexture = baseTexture;

        bottomPanel = new BottomPanel(WORLD_WIDTH, WORLD_HEIGHT, 1f);
        topPanel = new TopPanel(WORLD_WIDTH, WORLD_HEIGHT, 1f, this, screenManager);

        statsFont = new BitmapFont();
        statsFont.getData().setScale(2.0f);
        statsFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        coinsFont = new BitmapFont();
        coinsFont.getData().setScale(2.0f);
        coinsFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        messageFont = new BitmapFont();
        messageFont.setColor(Color.PINK);
        messageFont.getData().setScale(1.5f);
        messageFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        moveFont = new BitmapFont();
        moveFont.getData().setScale(2.0f);
        moveFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        int moveBtnW = 240;
        int moveBtnH = 90;
        int moveBtnX = (WORLD_WIDTH - moveBtnW) / 2;
        int moveBtnY = 200;
        moveButton = new Button(moveBtnX, moveBtnY, moveBtnW, moveBtnH, moveFont, GameResources.BUTTON_TEXT, "Move");
        moveButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        wasTouched = false;
        onScreenShow();
    }

    private void updateCharacterEmotion() {
        if (petEngine == null) return;
        var pet = petEngine.getPet();
        if (pet.getEnergy() <= 20) {
            currentCharacterTexture = sleepyTexture;
        } else if (pet.getHunger() <= 20 || pet.getCleanliness() <= 20) {
            currentCharacterTexture = sadTexture;
        } else if (pet.getHappiness() >= 80) {
            currentCharacterTexture = happyTexture;
        } else {
            currentCharacterTexture = baseTexture;
        }
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
        int startX = (int) (WORLD_WIDTH * 0.03);
        int startY = (int) (WORLD_HEIGHT * 0.86);
        int lineHeight = 40;

        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Mood", startX, startY);
        statsFont.setColor(getStatColor(pet.getHappiness()));
        statsFont.draw(batch, pet.getHappiness() + "%", startX + 160, startY);
        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Sleepiness", startX, startY - lineHeight);
        statsFont.setColor(getStatColor(pet.getEnergy()));
        statsFont.draw(batch, pet.getEnergy() + "%", startX + 160, startY - lineHeight);
        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Hunger", startX, startY - lineHeight * 2);
        statsFont.setColor(getStatColor(pet.getHunger()));
        statsFont.draw(batch, pet.getHunger() + "%", startX + 160, startY - lineHeight * 2);
        statsFont.setColor(Color.WHITE);

        statsFont.draw(batch, "Dirtiness", startX, startY - lineHeight * 3);
        statsFont.setColor(getStatColor(pet.getCleanliness()));
        statsFont.draw(batch, pet.getCleanliness() + "%", startX + 160, startY - lineHeight * 3);
        statsFont.setColor(Color.WHITE);

        coinsFont.setColor(Color.GOLD);
        coinsFont.draw(batch, "Coins: " + pet.getCoins(), WORLD_WIDTH - 150, WORLD_HEIGHT - 40);
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

            if (topPanel != null && topPanel.handleTouch(touchX, touchY)) {
                wasTouched = isTouched;
                return;
            }

            boolean catIsHere = screenManager != null
                    && screenManager.getCurrentCatRoom() == getCatRoomState();

            if (!catIsHere && moveButton != null && moveButton.contains(touchX, touchY)) {
                if (screenManager != null) {
                    screenManager.setCatRoom(getCatRoomState());
                }
                wasTouched = isTouched;
                return;
            }

            if (bottomPanel.handleTouch(touchX, touchY)) {
                onBottomPanelLocationChanged(bottomPanel.getActiveLocation());
                wasTouched = isTouched;
                return;
            }

            if (catIsHere && shouldDrawCharacter() && currentCharacterTexture != null) {
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
        if (screenManager != null) screenManager.switchToLocation(location);
    }

    public void setActiveLocation(BottomPanelButton.LocationType location) {
        if (bottomPanel != null) bottomPanel.setActiveLocation(location);
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
        updateCharacterEmotion();
        ScreenUtils.clear(0.9f, 0.85f, 0.8f, 1);
        batch.setProjectionMatrix(((MemeGotchi) screenManager).camera.combined);

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        if (catIsHere && shouldDrawCharacter() && currentCharacterTexture != null) {
            float scaleMul = getCharacterScaleMultiplier();
            float charDrawWidth = currentCharacterTexture.getWidth() * CHARACTER_SCALE * scaleMul;
            float charDrawHeight = currentCharacterTexture.getHeight() * CHARACTER_SCALE * scaleMul;
            float charX = (WORLD_WIDTH - charDrawWidth) / 2;
            float charY = (WORLD_HEIGHT - charDrawHeight) / 2 - 30 + getCharacterYShift();
            batch.draw(currentCharacterTexture, charX, charY, charDrawWidth, charDrawHeight);
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

        if (!catIsHere && moveButton != null) {
            batch.begin();
            moveButton.render(batch, false);
            batch.end();
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
        if (batch != null) { batch.dispose(); batch = null; }
        if (shapeRenderer != null) { shapeRenderer.dispose(); shapeRenderer = null; }
        if (backgroundTexture != null) { backgroundTexture.dispose(); backgroundTexture = null; }
        if (baseTexture != null) { baseTexture.dispose(); baseTexture = null; }
        if (happyTexture != null) { happyTexture.dispose(); happyTexture = null; }
        if (sadTexture != null) { sadTexture.dispose(); sadTexture = null; }
        if (sleepyTexture != null) { sleepyTexture.dispose(); sleepyTexture = null; }
        if (currentCharacterTexture != null) { currentCharacterTexture.dispose(); currentCharacterTexture = null; }
        if (bottomPanel != null) { bottomPanel.dispose(); bottomPanel = null; }
        if (topPanel != null) { topPanel.dispose(); topPanel = null; }
        if (statsFont != null) { statsFont.dispose(); statsFont = null; }
        if (coinsFont != null) { coinsFont.dispose(); coinsFont = null; }
        if (messageFont != null) { messageFont.dispose(); messageFont = null; }
        if (moveFont != null) { moveFont.dispose(); moveFont = null; }
        if (font != null) { font.dispose(); font = null; }
        if (moveButton != null) { moveButton.dispose(); moveButton = null; }
    }

    @Override
    public void dispose() {
        disposeResources();
    }

    public abstract String getBackgroundPath();
    public abstract String getCharacterPath();
    public abstract boolean shouldDrawCharacter();

    protected float getCharacterScaleMultiplier() {
        return 1.0f;
    }

    protected float getCharacterYShift() {
        return 0f;
    }

    protected String getCharacterFolder() {
        return "living_room";
    }
}
