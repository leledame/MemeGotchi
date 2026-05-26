package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.memegotchi.game.buttons.TopButton;
import com.memegotchi.game.screens.BaseScreen;
import com.memegotchi.game.screens.ScreenManager;

public class TopPanel extends Panel {
    public enum TopMenuType {
        SHOP, SETTINGS
    }

    private static final float BORDER_SCALE = 18f;
    private TopButton shopButton;
    private TopButton settingsButton;
    private ScreenManager screenManager;
    private BitmapFont font;

    public TopPanel(int screenWidth, int screenHeight, float scale, BaseScreen parent, ScreenManager sm) {
        super(screenWidth, screenHeight, scale, false);
        this.screenManager = sm;
        initButtons();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
    }

    private void initButtons() {
        int btnSize = (int) (panelHeight * 0.6f);
        int btnY = panelY + (panelHeight - btnSize) / 2;
        settingsButton = new TopButton(10, btnY, btnSize, btnSize, TopMenuType.SETTINGS);
        shopButton = new TopButton(screenWidth - btnSize - 10, btnY, btnSize, btnSize, TopMenuType.SHOP);
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

        if (shopButton != null) shopButton.render(batch, false);
        if (settingsButton != null) settingsButton.render(batch, false);

        batch.end();
    }

    public boolean handleTouch(int touchX, int touchY) {
        if (shopButton != null && shopButton.contains(touchX, touchY) && screenManager != null) {
            screenManager.switchToShop();
            return true;
        }
        if (settingsButton != null && settingsButton.contains(touchX, touchY) && screenManager != null) {
            screenManager.switchToSettings();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (shopButton != null) shopButton.dispose();
        if (settingsButton != null) settingsButton.dispose();
    }
}
