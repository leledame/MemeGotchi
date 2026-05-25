package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.engine.PetEngine;

public class FishingScreen extends BaseScreen {
    private static final float SIZE_MULTIPLIER = 5.0f;

    private Texture gameFishingBackgroundTexture;
    private Texture fishTexture;
    private Texture greenZoneTexture;
    private TextButton startGameButton;
    private Stage uiStage;

    private boolean isFishingStarted = false;
    private float holdTimer = 0f;
    private static final float REQUIRED_HOLD_TIME = 10.0f;

    private float firstColumnCenterX;
    private float secondColumnCenterX;
    private float columnWidth;
    private float frameBottomY;
    private float frameTopY;

    private float zoneY;
    private float zoneHeight;
    private float zoneSpeed = 0;
    private static final float ZONE_GRAVITY = 2800f;
    private static final float ZONE_SPEED_UP = 3600f;

    private float fishY;
    private float fishTargetY;
    private float fishSpeed;
    private long fishMoveStartTime;
    private float fishMoveDuration;
    private static final float MAX_FISH_SPEED = 2400f;

    private BitmapFont buttonFont;

    public FishingScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_FISHING_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }

    @Override
    public boolean shouldDrawCharacter() {
        return true;
    }

    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.WALK_MINIGAME;
    }

    @Override
    protected void onScreenShow() {
        super.onScreenShow();
        isFishingStarted = false;
        holdTimer = 0f;

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        gameFishingBackgroundTexture = new Texture("backgronds/fishing/gamefish.png");
        fishTexture = new Texture("backgronds/fishing/fish_for_game.png");
        greenZoneTexture = new Texture("backgronds/fishing/green_zone.png");

        gameFishingBackgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fishTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        greenZoneTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Создаём стиль для кнопки
        TextButtonStyle startStyle = new TextButtonStyle();
        try {
            Texture buttonTexture = new Texture("buttons/text_button/button.png");
            buttonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            startStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            startStyle.down = startStyle.up;
        } catch (Exception e) {
            // Если текстуры нет, будет просто текст
        }
        startStyle.font = buttonFont;

        startGameButton = new TextButton("START", startStyle);
        startGameButton.setSize(280, 90); // фиксированный размер

        // Позиционирование: по центру по горизонтали, Y = 550 (чуть ниже середины)
        float btnX = (GameResources.SCREEN_WIDTH - startGameButton.getWidth()) / 2f;
        float btnY = 400 ;
        startGameButton.setPosition(btnX, btnY);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFishingStarted = true;
                startGameButton.setVisible(false);
            }
        });

        // Создаём Stage, синхронизированный с камерой игры
        if (screenManager instanceof MemeGotchi) {
            MemeGotchi game = (MemeGotchi) screenManager;
            uiStage = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(
                    GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT, game.camera));
        } else {
            uiStage = new Stage();
        }
        uiStage.addActor(startGameButton);
        Gdx.input.setInputProcessor(uiStage);

        calculateLayout();
    }

    @Override
    public void render(float delta) {
        super.render(delta); // рисует фон, персонажа, статы

        // Отрисовываем UI-слой (кнопку), только если игра не началась
        if (uiStage != null && !isFishingStarted) {
            uiStage.act(delta);
            uiStage.draw();
        }

        if (!isFishingStarted) {
            return;
        }

        updateZonePhysics();

        float baseScale = scale * SIZE_MULTIPLIER;
        float mgDrawWidth = gameFishingBackgroundTexture.getWidth() * baseScale;
        float mgDrawHeight = gameFishingBackgroundTexture.getHeight() * baseScale;
        float mgX = (GameResources.SCREEN_WIDTH - mgDrawWidth) / 2f;
        float mgY = (GameResources.SCREEN_HEIGHT - mgDrawHeight) / 2f;

        batch.begin();
        batch.draw(gameFishingBackgroundTexture, mgX, mgY, mgDrawWidth, mgDrawHeight);

        if (TimeUtils.timeSinceNanos(fishMoveStartTime) * MathUtils.nanoToSec >= fishMoveDuration) {
            generateNewFishTarget();
        }
        fishY += fishSpeed * delta;

        float fishHalfH = (fishTexture.getHeight() * baseScale) / 2f;
        if (fishY < frameBottomY + fishHalfH) {
            fishY = frameBottomY + fishHalfH;
            fishSpeed *= -0.5f;
        }
        if (fishY > frameTopY - fishHalfH) {
            fishY = frameTopY - fishHalfH;
            fishSpeed *= -0.5f;
        }

        if (fishY >= zoneY && fishY <= zoneY + zoneHeight) {
            holdTimer += delta;
        } else {
            holdTimer -= delta * 0.5f;
        }
        holdTimer = MathUtils.clamp(holdTimer, 0, REQUIRED_HOLD_TIME);

        float zoneWidth = columnWidth * 0.45f;
        batch.draw(greenZoneTexture, firstColumnCenterX - (zoneWidth / 2f), zoneY, zoneWidth, zoneHeight);

        float fishW = fishTexture.getWidth() * baseScale;
        float fishH = fishTexture.getHeight() * baseScale;
        batch.draw(fishTexture, firstColumnCenterX - (fishW / 2f), fishY - (fishH / 2f), fishW, fishH);

        float barMaxHeight = frameTopY - frameBottomY;
        float progressH = (holdTimer / REQUIRED_HOLD_TIME) * barMaxHeight;
        float barWidth = zoneWidth;
        batch.draw(greenZoneTexture, secondColumnCenterX - (barWidth / 2f), frameBottomY, barWidth, progressH);

        batch.end();

        if (holdTimer >= REQUIRED_HOLD_TIME) {
            isFishingStarted = false;
            holdTimer = 0;
            if (petEngine != null) {
                petEngine.getPet().setCoins(petEngine.getPet().getCoins() + 10);
            }
            if (screenManager != null) {
                screenManager.backToPreviousScreen();
            }
        }
    }

    private void updateZonePhysics() {
        if (isFishingStarted) {
            float dt = Gdx.graphics.getDeltaTime();
            if (Gdx.input.isTouched()) {
                zoneSpeed += (ZONE_SPEED_UP - ZONE_GRAVITY) * dt;
            } else {
                zoneSpeed -= ZONE_GRAVITY * dt;
            }
            zoneY += zoneSpeed * dt;

            if (zoneY < frameBottomY) {
                zoneY = frameBottomY;
                zoneSpeed = 0;
            }
            if (zoneY + zoneHeight > frameTopY) {
                zoneY = frameTopY - zoneHeight;
                zoneSpeed = 0;
            }
        }
    }

    private void calculateLayout() {
        float baseScale = scale * SIZE_MULTIPLIER;

        float mgDrawWidth = gameFishingBackgroundTexture.getWidth() * baseScale;
        float mgDrawHeight = gameFishingBackgroundTexture.getHeight() * baseScale;
        float mgX = (GameResources.SCREEN_WIDTH - mgDrawWidth) / 2f;
        float mgY = (GameResources.SCREEN_HEIGHT - mgDrawHeight) / 2f;

        columnWidth = mgDrawWidth / 3.0f;

        float fishOffsetX = -32f;
        firstColumnCenterX = mgX + columnWidth + (columnWidth * 0.4f) + fishOffsetX;

        float progressOffsetX = -97f;
        secondColumnCenterX = mgX + (columnWidth * 2) + (columnWidth * 0.2f) + progressOffsetX;

        float bottomBoundaryOffsetY = 54f;
        frameBottomY = mgY + bottomBoundaryOffsetY;

        float topBoundaryOffsetY = -30f;
        frameTopY = mgY + mgDrawHeight + topBoundaryOffsetY;

        zoneHeight = greenZoneTexture.getHeight() * baseScale;

        zoneY = frameBottomY;
        float fishHalfHeight = (fishTexture.getHeight() * baseScale) / 2f;
        fishY = frameBottomY + fishHalfHeight;
        generateNewFishTarget();
    }

    private void generateNewFishTarget() {
        float baseScale = scale * SIZE_MULTIPLIER;
        float fishHalfHeight = (fishTexture.getHeight() * baseScale) / 2f;
        fishTargetY = MathUtils.random(frameBottomY + fishHalfHeight, frameTopY - fishHalfHeight);
        fishMoveStartTime = TimeUtils.nanoTime();
        fishMoveDuration = MathUtils.random(0.8f, 2.0f);

        float timeToReach = MathUtils.random(0.4f, 1.2f);
        fishSpeed = (fishTargetY - fishY) / timeToReach;
        fishSpeed = MathUtils.clamp(fishSpeed, -MAX_FISH_SPEED, MAX_FISH_SPEED);
    }

    @Override
    public void hide() {
        super.hide();
        if (uiStage != null) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        if (gameFishingBackgroundTexture != null) gameFishingBackgroundTexture.dispose();
        if (fishTexture != null) fishTexture.dispose();
        if (greenZoneTexture != null) greenZoneTexture.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (uiStage != null) uiStage.dispose();
        super.dispose();
    }
}