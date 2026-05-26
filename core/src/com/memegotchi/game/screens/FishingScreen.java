package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.engine.PetEngine;

public class FishingScreen extends BaseScreen {
    private static final float SIZE_MULTIPLIER = 5.0f;
    public enum FishPattern { NORMAL, FAST, VERY_FAST }

    // ДОБАВЛЕНО: Поле name для названия рыбы
    public class FishData {
        public Texture texture;
        public FishPattern pattern;
        public int price;
        public String name;

        public FishData(Texture texture, FishPattern pattern, int price, String name) {
            this.texture = texture;
            this.pattern = pattern;
            this.price = price;
            this.name = name;
        }
    }

    private FishData[] fishes = new FishData[9];
    private FishData currentFish;

    private Texture gameFishingBackgroundTexture;
    private Texture fishTexture;
    private Texture greenZoneTexture;

    private Stage uiStage;
    private Stage catchUiStage;
    private TextButton startGameButton;
    private Image caughtFishImage;
    private Label catchMessageLabel;

    private boolean isFishingStarted = false;
    private boolean isCatchScreenOpen = false;

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
    private static final float ZONE_GRAVITY = 4200f;
    private static final float ZONE_SPEED_UP = 5400f;

    private float fishY;
    private float fishTargetY;
    private float fishMoveDuration;
    private float fishStartMoveY;
    private float currentMoveTime = 0f;

    private BitmapFont buttonFont;
    private BitmapFont catchFont;

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
        isCatchScreenOpen = false;
        holdTimer = REQUIRED_HOLD_TIME / 4f;
        fishStartMoveY = 0f;
        currentMoveTime = 0f;

        // Шрифт с чёрным цветом для контраста.
        // ИЗМЕНЕНО: Масштаб увеличен с 3.5f до 4.0f, чтобы текст казался крупнее (имитация жирности).
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(3.5f);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        catchFont = new BitmapFont();
        catchFont.getData().setScale(4.0f);
        catchFont.setColor(Color.WHITE);
        catchFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        gameFishingBackgroundTexture = new Texture("backgronds/fishing/gamefish.png");
        fishTexture = new Texture("backgronds/fishing/fish_for_game.png");
        greenZoneTexture = new Texture("backgronds/fishing/green_zone.png");

        gameFishingBackgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fishTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        greenZoneTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // ДОБАВЛЕНО: Массив с названиями рыб
        String[] fishNames = {
                "Stone Fish", "Bread fish", "Herring",
                "Sky Fish", "Purple Blowfish", "Plush Fish",
                "Royal Fish", "Shadow Fish", "Ruby Fish"
        };

        for (int i = 0; i < 9; i++) {
            Texture tex = new Texture("backgronds/fishing/fishes/fish_" + (i + 1) + ".png");
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            FishPattern pattern;
            int price;
            if (i < 3) {
                pattern = FishPattern.NORMAL;
                price = MathUtils.random(5, 15);
            } else if (i < 6) {
                pattern = FishPattern.FAST;
                price = MathUtils.random(20, 40);
            } else {
                pattern = FishPattern.VERY_FAST;
                price = MathUtils.random(50, 100);
            }
            // ИЗМЕНЕНО: Передаем имя рыбы из массива
            fishes[i] = new FishData(tex, pattern, price, fishNames[i]);
        }
        currentFish = fishes[MathUtils.random(0, 8)];

        setupUI();
        calculateLayout();

        if (uiStage != null) {
            Gdx.input.setInputProcessor(uiStage);
        }
    }

    private void setupUI() {
        TextButtonStyle btnStyle = new TextButtonStyle();
        try {
            Texture buttonTexture = new Texture("buttons/text_button/button.png");
            buttonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            btnStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            btnStyle.down = btnStyle.up;
        } catch (Exception e) {
            // Если текстуры нет, кнопка будет просто текстом
        }
        btnStyle.font = catchFont;
        btnStyle.fontColor = Color.WHITE;

        startGameButton = new TextButton("START", btnStyle);
        startGameButton.setSize(280, 90);
        float btnX = (GameResources.SCREEN_WIDTH - startGameButton.getWidth()) / 2f;
        startGameButton.setPosition(btnX, 400);
        startGameButton.setVisible(true);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentFish = fishes[MathUtils.random(0, 8)];
                isFishingStarted = true;
                startGameButton.setVisible(false);
                float baseScale = scale * SIZE_MULTIPLIER;
                float fishHalfHeight = (fishTexture.getHeight() * baseScale) / 2f;
                fishY = frameBottomY + fishHalfHeight;
                zoneY = frameBottomY;
                generateNewFishTarget();
            }
        });

        if (screenManager instanceof MemeGotchi) {
            MemeGotchi game = (MemeGotchi) screenManager;
            uiStage = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(
                    GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT, game.camera));
            catchUiStage = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(
                    GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT, game.camera));
        } else {
            uiStage = new Stage();
            catchUiStage = new Stage();
        }
        uiStage.addActor(startGameButton);

        Label.LabelStyle labelStyle = new Label.LabelStyle(catchFont, Color.WHITE);
        catchMessageLabel = new Label("", labelStyle);
        catchMessageLabel.setAlignment(Align.center);
        catchMessageLabel.setPosition(0, 600);
        catchMessageLabel.setSize(GameResources.SCREEN_WIDTH, 100);

        caughtFishImage = new Image();
        caughtFishImage.setSize(200, 200);
        caughtFishImage.setPosition((GameResources.SCREEN_WIDTH - 200) / 2f, 380);

        TextButtonStyle catchBtnStyle = new TextButtonStyle();
        try {
            Texture catchBtnTexture = new Texture("buttons/text_button/button.png");
            catchBtnTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            catchBtnStyle.up = new TextureRegionDrawable(new TextureRegion(catchBtnTexture));
            catchBtnStyle.down = catchBtnStyle.up;
        } catch (Exception e) {
        }
        catchBtnStyle.font = catchFont;
        catchBtnStyle.fontColor = Color.WHITE;

        TextButton sellButton = new TextButton("SELL", catchBtnStyle);
        sellButton.setSize(200, 80);
        sellButton.setPosition(GameResources.SCREEN_WIDTH / 2f + 20, 260);
        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (petEngine != null && petEngine.getPet() != null) {
                    petEngine.getPet().setCoins(petEngine.getPet().getCoins() + currentFish.price);
                }
                if (screenManager != null) screenManager.backToPreviousScreen();
            }
        });

        TextButton lootButton = new TextButton("LOOT", catchBtnStyle);
        lootButton.setSize(200, 80);
        lootButton.setPosition(GameResources.SCREEN_WIDTH / 2f - 220, 260);
        lootButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (petEngine != null && petEngine.getPet() != null && currentFish != null) {
                    petEngine.getPet().addFish(currentFish.name);
                }
                if (screenManager != null) {
                    screenManager.backToPreviousScreen();
                }
            }
        });

        catchUiStage.addActor(catchMessageLabel);
        catchUiStage.addActor(caughtFishImage);
        catchUiStage.addActor(sellButton);
        catchUiStage.addActor(lootButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (isCatchScreenOpen) {
            catchUiStage.act(delta);
            catchUiStage.draw();
            return;
        }

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();

        if (!isFishingStarted && uiStage != null && catIsHere) {
            uiStage.act(delta);
            uiStage.draw();
        }

        if (!isFishingStarted) return;

        updateZonePhysics();

        float baseScale = scale * SIZE_MULTIPLIER;
        float mgDrawWidth = gameFishingBackgroundTexture.getWidth() * baseScale;
        float mgDrawHeight = gameFishingBackgroundTexture.getHeight() * baseScale;
        float mgX = (GameResources.SCREEN_WIDTH - mgDrawWidth) / 2f;
        float mgY = (GameResources.SCREEN_HEIGHT - mgDrawHeight) / 2f;

        batch.begin();
        batch.draw(gameFishingBackgroundTexture, mgX, mgY, mgDrawWidth, mgDrawHeight);

        currentMoveTime += delta;
        if (currentMoveTime >= fishMoveDuration) {
            generateNewFishTarget();
        } else {
            float alpha = currentMoveTime / fishMoveDuration;
            alpha = MathUtils.clamp(alpha, 0f, 1f);
            fishY = Interpolation.smoother.apply(fishStartMoveY, fishTargetY, alpha);
        }

        if (fishY >= zoneY && fishY <= zoneY + zoneHeight) {
            holdTimer += delta * 1.5f;
        } else {
            holdTimer -= delta * 1.0f;
        }
        holdTimer = MathUtils.clamp(holdTimer, 0, REQUIRED_HOLD_TIME);

        if (holdTimer <= 0) {
            isFishingStarted = false;
            holdTimer = REQUIRED_HOLD_TIME / 4f;
            if (startGameButton != null) startGameButton.setVisible(true);
            Gdx.input.setInputProcessor(uiStage);
            batch.end();
            return;
        }

        float zoneWidth = columnWidth * 0.45f;
        batch.draw(greenZoneTexture, firstColumnCenterX - zoneWidth / 2f, zoneY, zoneWidth, zoneHeight);

        float fishW = fishTexture.getWidth() * baseScale;
        float fishH = fishTexture.getHeight() * baseScale;
        batch.draw(fishTexture, firstColumnCenterX - fishW / 2f, fishY - fishH / 2f, fishW, fishH);

        float barMaxHeight = frameTopY - frameBottomY;
        float progressH = (holdTimer / REQUIRED_HOLD_TIME) * barMaxHeight;
        float barWidth = zoneWidth;
        batch.draw(greenZoneTexture, secondColumnCenterX - barWidth / 2f, frameBottomY, barWidth, progressH);

        batch.end();

        if (holdTimer >= REQUIRED_HOLD_TIME) {
            isFishingStarted = false;
            isCatchScreenOpen = true;
            caughtFishImage.setDrawable(new TextureRegionDrawable(new TextureRegion(currentFish.texture)));

            // ИЗМЕНЕНО: Выводим название пойманной рыбы (в верхнем регистре) вместо "YOU CAUGHT A FISH!"
            catchMessageLabel.setText(currentFish.name.toUpperCase() + " CAUGHT!\nPRICE: " + currentFish.price + " COINS");

            Gdx.input.setInputProcessor(catchUiStage);
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
        firstColumnCenterX = mgX + columnWidth + columnWidth * 0.4f - 32f;
        secondColumnCenterX = mgX + columnWidth * 2 + columnWidth * 0.2f - 97f;

        frameBottomY = mgY + 54f;
        frameTopY = mgY + mgDrawHeight - 30f;
        zoneHeight = greenZoneTexture.getHeight() * baseScale;
        zoneY = frameBottomY;

        float fishHalfHeight = (fishTexture.getHeight() * baseScale) / 2f;
        fishY = frameBottomY + fishHalfHeight;
    }

    private void generateNewFishTarget() {
        float baseScale = scale * SIZE_MULTIPLIER;
        float fishHalfHeight = (fishTexture.getHeight() * baseScale) / 2f;
        fishTargetY = MathUtils.random(frameBottomY + fishHalfHeight, frameTopY - fishHalfHeight);
        fishStartMoveY = fishY;
        currentMoveTime = 0f;

        float minDuration = 1.0f, maxDuration = 2.0f;
        switch (currentFish.pattern) {
            case NORMAL:
                minDuration = 1.8f;
                maxDuration = 3.5f;
                break;
            case FAST:
            case VERY_FAST:
                minDuration = 0.6f;
                maxDuration = 1.2f;
                break;
        }
        fishMoveDuration = MathUtils.random(minDuration, maxDuration);
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (gameFishingBackgroundTexture != null) gameFishingBackgroundTexture.dispose();
        if (fishTexture != null) fishTexture.dispose();
        if (greenZoneTexture != null) greenZoneTexture.dispose();
        if (fishes != null) {
            for (FishData fish : fishes) {
                if (fish != null && fish.texture != null) fish.texture.dispose();
            }
        }
        if (buttonFont != null) buttonFont.dispose();
        if (catchFont != null) catchFont.dispose();
        if (uiStage != null) uiStage.dispose();
        if (catchUiStage != null) catchUiStage.dispose();
        super.dispose();
    }
}