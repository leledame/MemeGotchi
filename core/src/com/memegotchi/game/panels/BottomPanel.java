package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.memegotchi.game.buttons.BottomPanelButton;

public class BottomPanel extends Panel {
    private static final int BUTTON_COUNT = 5;
    private static final float BORDER_SCALE = 18f;

    private BottomPanelButton[] buttons;
    private int activeButtonIndex = 0;

    public BottomPanel(int screenWidth, int screenHeight, float scale) {
        super(screenWidth, screenHeight, scale, true);
        initializeButtons();
    }

    private void initializeButtons() {
        if (buttons != null) {
            for (BottomPanelButton button : buttons) {
                if (button != null) button.dispose();
            }
        }

        buttons = new BottomPanelButton[BUTTON_COUNT];
        BottomPanelButton.LocationType[] locations = {
                BottomPanelButton.LocationType.LIVING,
                BottomPanelButton.LocationType.WALK,
                BottomPanelButton.LocationType.KITCHEN,
                BottomPanelButton.LocationType.TOILET,
                BottomPanelButton.LocationType.BEDROOM
        };

        int buttonSize = (int) (panelHeight *   0.6f);
        int slotWidth = screenWidth / BUTTON_COUNT;
        int buttonY = panelY + (int)((panelHeight - buttonSize) * 0.4f);

        for (int i = 0; i < BUTTON_COUNT; i++) {
            int buttonX = i * slotWidth + (slotWidth - buttonSize) / 2;
            buttons[i] = new BottomPanelButton(buttonX, buttonY, buttonSize, buttonSize, locations[i]);
            buttons[i].loadTextures();
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        int border = Math.max(2, (int) (scale * BORDER_SCALE));

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Светлый контур (снизу)
        shapeRenderer.setColor(1f, 0.8f, 0.835f, 1f);
        shapeRenderer.rect(0, panelY, screenWidth, panelHeight);

        // Тёмная панель (сверху)
        shapeRenderer.setColor(0.8f, 0.56f, 0.58f, 1f);
        shapeRenderer.rect(0, panelY, screenWidth, panelHeight - border);

        shapeRenderer.end();

        batch.begin();
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null) continue;
            buttons[i].render(batch, i == activeButtonIndex);
        }
        batch.end();
    }

    public boolean handleTouch(int touchX, int touchY) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null || !buttons[i].isAvailable()) continue;
            if (buttons[i].contains(touchX, touchY)) {
                setActiveButton(i);
                return true;
            }
        }
        return false;
    }

    public void setActiveButton(int index) {
        if (index >= 0 && index < BUTTON_COUNT) {
            activeButtonIndex = index;
        }
    }

    public void setActiveLocation(BottomPanelButton.LocationType location) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null && buttons[i].getLocation() == location) {
                activeButtonIndex = i;
                return;
            }
        }
    }

    public BottomPanelButton.LocationType getActiveLocation() {
        if (buttons[activeButtonIndex] != null) {
            return buttons[activeButtonIndex].getLocation();
        }
        return BottomPanelButton.LocationType.LIVING;
    }

    public void setWalkAvailable(boolean available) {
        if (buttons[1] != null) {
            buttons[1].setAvailable(available);
        }
    }

    public void resize(int width, int height, float scale) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.scale = scale;
        this.panelHeight = (int) (screenHeight * PANEL_SIZE_PERCENT);
        this.panelY = 0;
        initializeButtons();
    }

    @Override
    public void dispose() {
        if (buttons != null) {
            for (BottomPanelButton button : buttons) {
                if (button != null) button.dispose();
            }
        }
    }
}