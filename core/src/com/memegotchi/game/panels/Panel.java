package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Panel {
    protected int screenWidth;
    protected int screenHeight;
    protected int panelHeight;
    protected int panelY;
    protected float scale;

    protected static final float PANEL_SIZE_PERCENT = 0.12f;

    public Panel(int screenWidth, int screenHeight, float scale, boolean isBottom) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scale = scale;
        this.panelHeight = (int) (screenHeight * PANEL_SIZE_PERCENT);
        this.panelY = isBottom ? 0 : screenHeight - panelHeight;
    }

    public abstract void render(SpriteBatch batch, ShapeRenderer shapeRenderer);

    public abstract void dispose();

    public int getPanelHeight() {
        return panelHeight;
    }

    public int getPanelY() {
        return panelY;
    }
}
