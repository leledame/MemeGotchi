package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TopPanel extends Panel {
    private static final float BORDER_SCALE = 18f;

    public enum TopMenuType {
        SHOP, KITCHEN
    }

    public TopPanel(int screenWidth, int screenHeight, float scale) {
        super(screenWidth, screenHeight, scale, false);
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
    }

    @Override
    public void dispose() {
    }
}
