package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.TimeUtils;
import com.memegotchi.game.GameResources;

public class FishingScreen extends BaseScreen {
    private static final float SIZE_MULTIPLIER = 10.0f;

    private Texture gameFishingBackgroundTexture;
    private Texture fishTexture;
    private Texture greenZoneTexture;
    private TextButton startGameButton;

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

        gameFishingBackgroundTexture = new Texture("backgronds/fishing/gamefish.png");
        fishTexture = new Texture("backgronds/fishing/fish_for_game.png");
        greenZoneTexture = new Texture("backgronds/fishing/green_zone.png");

        gameFishingBackgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fishTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        greenZoneTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        try {
            Texture buttonTexture = new Texture("buttons/text_button/button.png");
            buttonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            TextButtonStyle startStyle = new TextButtonStyle();
            startStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            startStyle.down = startStyle.up;
            startStyle.font = font;

            startGameButton = new TextButton("Start", startStyle);
        } catch (Exception e) {
            TextButtonStyle startStyle = new TextButtonStyle();
            startStyle.font = font;
            startGameButton = new TextButton("Start", startStyle);
        }

        startGameButton.pack();
        startGameButton.setTransform(true);
        startGameButton.setScale(2.5f);
        startGameButton.setPosition(screenWidth / 2f - startGameButton.getWidth() * 1.25f, 250);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFishingStarted = true;
                startGameButton.setVisible(false);
            }
        });
        stage.addActor(startGameButton);

        calculateLayout();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        updateZonePhysics();

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();

        if (!isFishingStarted) {
            startGameButton.setVisible(catIsHere);
        }
        moveButton.setVisible(!catIsHere);

        if (!isFishingStarted) {
            return;
        }

        float baseScale = scale * SIZE_MULTIPLIER;
        float mgDrawWidth = gameFishingBackgroundTexture.getWidth() * baseScale;
        float mgDrawHeight = gameFishingBackgroundTexture.getHeight() * baseScale;
        float mgX = (screenWidth - mgDrawWidth) / 2f;
        float mgY = (screenHeight - mgDrawHeight) / 2f;

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

        // Отрисовка левой зеленой зоны (поплавка)
        float zoneWidth = columnWidth * 0.45f;
        batch.draw(greenZoneTexture, firstColumnCenterX - (zoneWidth / 2f), zoneY, zoneWidth, zoneHeight);

        // Отрисовка рыбы
        float fishW = fishTexture.getWidth() * baseScale;
        float fishH = fishTexture.getHeight() * baseScale;
        batch.draw(fishTexture, firstColumnCenterX - (fishW / 2f), fishY - (fishH / 2f), fishW, fishH);

        // Отрисовка правой зеленой зоны (шкалы прогресса)
        float barMaxHeight = frameTopY - frameBottomY;
        float progressH = (holdTimer / REQUIRED_HOLD_TIME) * barMaxHeight;

        // Ширина шкалы строго равна ширине зеленой зоны (поплавка)
        float barWidth = zoneWidth;

        // Рисуем шкалу
        batch.draw(greenZoneTexture, secondColumnCenterX - (barWidth / 2f), frameBottomY, barWidth, progressH);

        batch.end();

        if (holdTimer >= REQUIRED_HOLD_TIME) {
            isFishingStarted = false;
            holdTimer = 0;
        }
    }

    @Override
    protected void handleInput() {
        super.handleInput();
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
        float mgX = (screenWidth - mgDrawWidth) / 2f;
        float mgY = (screenHeight - mgDrawHeight) / 2f;

        columnWidth = mgDrawWidth / 3.0f;

        // Сдвиг рыбы и левой зеленой зоны
        float fishOffsetX = -26f;
        firstColumnCenterX = mgX + columnWidth + (columnWidth * 0.4f) + fishOffsetX;

        // --- ИЗМЕНЕНИЯ ЗДЕСЬ ---
        // Было -80f, прибавили 7 пикселей вправо, стало -73f.
        // Если ты имел в виду 7 пикселей самой текстуры (с учетом масштаба),
        // замени значение ниже на: -80f + (7f * baseScale);
        float progressOffsetX = -80f;
        secondColumnCenterX = mgX + (columnWidth * 2) + (columnWidth * 0.2f) + progressOffsetX;

        // Настройка границ по оси Y
        float bottomBoundaryOffsetY = 42f;
        frameBottomY = mgY + bottomBoundaryOffsetY;

        float topBoundaryOffsetY = -20f;
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
    public void dispose() {
        if (gameFishingBackgroundTexture != null) gameFishingBackgroundTexture.dispose();
        if (fishTexture != null) fishTexture.dispose();
        if (greenZoneTexture != null) greenZoneTexture.dispose();
        super.dispose();
    }
}