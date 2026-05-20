package com.memegotchi.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.buttons.BottomPanelButton;
import com.memegotchi.game.panels.BottomPanel;
import com.memegotchi.game.panels.TopPanel;

public abstract class BaseScreen implements Screen {
    protected SpriteBatch batch;
    protected ShapeRenderer shapeRenderer;
    protected Texture backgroundTexture;
    protected Texture characterTexture;
    protected float scale;
    protected BottomPanel bottomPanel;
    protected TopPanel topPanel;
    protected Stage stage;
    protected TextButton moveButton;
    protected BitmapFont font;
    protected int screenWidth;
    protected int screenHeight;
    protected ScreenManager screenManager;

    private boolean wasTouched = false;

    protected static final float CHARACTER_SCALE = 25f;
    protected static final float BORDER_SCALE = 18f;

    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        disposeResources();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        float scaleX = (float) screenWidth / getBaseScreenWidth();
        float scaleY = (float) screenHeight / getBaseScreenHeight();
        scale = Math.min(scaleX, scaleY);

        backgroundTexture = new Texture(getBackgroundPath());
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        if (shouldDrawCharacter()) {
            characterTexture = new Texture(getCharacterPath());
            characterTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        bottomPanel = new BottomPanel(screenWidth, screenHeight, scale);
        topPanel = new TopPanel(screenWidth, screenHeight, scale);

        font = new BitmapFont();

        TextureRegionDrawable buttonBg = new TextureRegionDrawable(
                new TextureRegion(new Texture("buttons/text_button/button.png")));
        TextButtonStyle style = new TextButtonStyle(buttonBg, buttonBg, buttonBg, font);
        moveButton = new TextButton("Move", style);
        moveButton.pack();

        moveButton.setTransform(true);
        moveButton.setScale(2.5f);

        moveButton.setPosition(screenWidth / 2f - moveButton.getWidth() * 1.25f, 250);
        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (screenManager != null) {
                    screenManager.setCatRoom(getCatRoomState());
                }
            }
        });

        stage = new Stage();
        stage.addActor(moveButton);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        wasTouched = false;

        onScreenShow();
    }

    protected void onScreenShow() {}

    protected int getBaseScreenWidth() {
        return com.memegotchi.game.GameResources.SCREEN_WIDTH;
    }

    protected int getBaseScreenHeight() {
        return com.memegotchi.game.GameResources.SCREEN_HEIGHT;
    }

    protected void handleInput() {
        boolean isTouched = Gdx.input.isTouched();

        if (isTouched && !wasTouched) {
            int touchX = Gdx.input.getX();
            int touchY = screenHeight - Gdx.input.getY();

            if (bottomPanel.handleTouch(touchX, touchY)) {
                onBottomPanelLocationChanged(bottomPanel.getActiveLocation());
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

    public abstract CatRoomState getCatRoomState();

    @Override
    public void render(float delta) {
        handleInput();

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();
        moveButton.setVisible(!catIsHere);

        ScreenUtils.clear(1, 1, 1, 1);

        int bgDrawWidth = (int) (getBaseScreenWidth() * scale);
        int bgDrawHeight = (int) (getBaseScreenHeight() * scale);
        int bgX = (screenWidth - bgDrawWidth) / 2;
        int bgY = (screenHeight - bgDrawHeight) / 2;

        batch.begin();
        batch.draw(backgroundTexture, bgX, bgY, bgDrawWidth, bgDrawHeight);

        if (catIsHere && characterTexture != null) {
            float charDrawWidth = characterTexture.getWidth() * scale * CHARACTER_SCALE;
            float charDrawHeight = characterTexture.getHeight() * scale * CHARACTER_SCALE;
            batch.draw(characterTexture, bgX + (bgDrawWidth - (int) charDrawWidth) / 2,
                    bgY + (bgDrawHeight - (int) charDrawHeight) / 2 - 30, charDrawWidth, charDrawHeight);
        }
        batch.end();

        bottomPanel.render(batch, shapeRenderer);
        topPanel.render(batch, shapeRenderer);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    private void disposeResources() {
        if (batch != null) { batch.dispose(); batch = null; }
        if (shapeRenderer != null) { shapeRenderer.dispose(); shapeRenderer = null; }
        if (backgroundTexture != null) { backgroundTexture.dispose(); backgroundTexture = null; }
        if (characterTexture != null) { characterTexture.dispose(); characterTexture = null; }
        if (bottomPanel != null) { bottomPanel.dispose(); bottomPanel = null; }
        if (topPanel != null) { topPanel.dispose(); topPanel = null; }
        if (font != null) { font.dispose(); font = null; }
        if (stage != null) { stage.dispose(); stage = null; }
    }

    @Override
    public void dispose() {
        disposeResources();
    }

    public abstract String getBackgroundPath();
    public abstract String getCharacterPath();
    public abstract boolean shouldDrawCharacter();
}