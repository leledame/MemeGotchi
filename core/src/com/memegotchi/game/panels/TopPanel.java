package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.memegotchi.game.buttons.TopButton;
import com.memegotchi.game.screens.BaseScreen;
import com.memegotchi.game.screens.ScreenManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TopPanel extends Panel {
    private static final float BORDER_SCALE = 18f;
    private TopButton shopButton;
    private TopButton settingsButton;
    private ScreenManager screenManager;      // добавлено поле
    private BitmapFont font;
    private BitmapFont timeFont;

    private String timeString = "";
    private long lastTimeUpdate = 0;
    private SimpleDateFormat timeFormat;

    public enum TopMenuType { SHOP, SETTINGS }

    // Конструктор теперь принимает ScreenManager
    public TopPanel(int screenWidth, int screenHeight, float scale, BaseScreen parent, ScreenManager sm) {
        super(screenWidth, screenHeight, scale, false);
        this.screenManager = sm;               // сохраняем
        initButtons();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        timeFont = new BitmapFont();
        timeFont.getData().setScale(1.8f);
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        updateTimeString();
    }

    private void initButtons() {
        if (shopButton != null) shopButton.dispose();
        if (settingsButton != null) settingsButton.dispose();

        int buttonSize = (int)(panelHeight * 0.7f);
        int spacing = 20;
        int shopX = screenWidth - buttonSize - spacing;
        int settingsX = shopX - buttonSize - spacing;
        int buttonY = panelY + (panelHeight - buttonSize) / 2;

        shopButton = new TopButton(shopX, buttonY, buttonSize, buttonSize, TopMenuType.SHOP);
        settingsButton = new TopButton(settingsX, buttonY, buttonSize, buttonSize, TopMenuType.SETTINGS);
    }

    private void updateTimeString() {
        timeString = timeFormat.format(new Date());
    }

    public boolean handleTouch(int touchX, int touchY) {
        if (shopButton != null && shopButton.contains(touchX, touchY)) {
            if (screenManager != null) screenManager.switchToShop();
            return true;
        }
        if (settingsButton != null && settingsButton.contains(touchX, touchY)) {
            if (screenManager != null) screenManager.switchToSettings();
            return true;
        }
        return false;
    }

    public void resize(int width, int height, float scale) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.scale = scale;
        this.panelHeight = (int) (screenHeight * PANEL_SIZE_PERCENT);
        this.panelY = screenHeight - panelHeight;
        initButtons();
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        int border = Math.max(2, (int)(scale * BORDER_SCALE));
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 0.8f, 0.835f, 1f);
        shapeRenderer.rect(0, panelY, screenWidth, panelHeight);
        shapeRenderer.setColor(0.8f, 0.56f, 0.58f, 1f);
        shapeRenderer.rect(0, panelY + border, screenWidth, panelHeight - border);
        shapeRenderer.end();

        batch.begin();

        // Часы
        long now = System.currentTimeMillis();
        if (now - lastTimeUpdate >= 1000) {
            lastTimeUpdate = now;
            updateTimeString();
        }
        timeFont.setColor(Color.WHITE);
        timeFont.draw(batch, timeString, 15, screenHeight - 25);

        // Иконка ✨
        font.setColor(Color.BLACK);
        font.draw(batch, "✨", 15 + timeFont.getRegion().getRegionWidth() * 1.2f, screenHeight - 25);

        // Кнопки
        if (shopButton != null) shopButton.render(batch, false);
        if (settingsButton != null) settingsButton.render(batch, false);

        batch.end();
    }

    @Override
    public void dispose() {
        if (shopButton != null) shopButton.dispose();
        if (settingsButton != null) settingsButton.dispose();
        if (font != null) font.dispose();
        if (timeFont != null) timeFont.dispose();
    }
}