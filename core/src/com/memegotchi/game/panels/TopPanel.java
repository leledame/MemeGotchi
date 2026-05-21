package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.memegotchi.game.buttons.TopButton;
import com.memegotchi.game.screens.BaseScreen;

public class TopPanel extends Panel {
    private static final float BORDER_SCALE = 18f;
    private TopButton shopButton;
    private BaseScreen parentScreen;
    private BitmapFont font;

    public enum TopMenuType {
        SHOP, KITCHEN
    }

    public TopPanel(int screenWidth, int screenHeight, float scale, BaseScreen parent) {
        super(screenWidth, screenHeight, scale, false);
        this.parentScreen = parent;
        initButton();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
    }

    private void initButton() {
        if (shopButton != null) {
            shopButton.dispose();
        }
        int buttonSize = (int)(panelHeight * 0.7f);
        int buttonX = screenWidth - buttonSize - 20;
        int buttonY = panelY + (panelHeight - buttonSize) / 2;
        shopButton = new TopButton(buttonX, buttonY, buttonSize, buttonSize, TopMenuType.SHOP);
    }

    public boolean handleTouch(int touchX, int touchY) {
        return shopButton != null && shopButton.contains(touchX, touchY);
    }

    public void resize(int width, int height, float scale) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.scale = scale;
        this.panelHeight = (int) (screenHeight * PANEL_SIZE_PERCENT);
        this.panelY = screenHeight - panelHeight;
        initButton();
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        int border = Math.max(2, (int) (scale * BORDER_SCALE));

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Светлый контур
        shapeRenderer.setColor(1f, 0.8f, 0.835f, 1f);
        shapeRenderer.rect(0, panelY, screenWidth, panelHeight);

        // Тёмная панель
        shapeRenderer.setColor(0.8f, 0.56f, 0.58f, 1f);
        shapeRenderer.rect(0, panelY + border, screenWidth, panelHeight - border);

        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, "✨", 15, screenHeight - 25);
        font.draw(batch, "🛒", screenWidth - 80, screenHeight - 25);
        if (shopButton != null) {
            shopButton.render(batch, false);
        }
        batch.end();
    }

    public TopButton getShopButton() {
        return shopButton;
    }

    @Override
    public void dispose() {
        if (shopButton != null) shopButton.dispose();
        if (font != null) font.dispose();
    }
}