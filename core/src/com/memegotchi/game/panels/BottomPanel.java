package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.memegotchi.game.buttons.BottomPanelButton;

public class BottomPanel extends Panel {
    private static final int BUTTON_COUNT = 5;
    private static final float BORDER_SCALE = 18f;
    private static final float WALK_SCALE = 1.50f;

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
        int buttonY = panelY + (panelHeight - buttonSize) / 2;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            int buttonX = i * slotWidth + (slotWidth - buttonSize) / 2;
            if (locations[i] == BottomPanelButton.LocationType.TOILET) buttonX += 30;
            if (locations[i] == BottomPanelButton.LocationType.WALK) buttonX += 10;
            if (locations[i] == BottomPanelButton.LocationType.KITCHEN) buttonX += (int)(screenWidth * 0.035f);
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
            if (buttons[i].getLocation() == BottomPanelButton.LocationType.WALK) {
                renderScaledButton(batch, buttons[i], i == activeButtonIndex);
            } else {
                buttons[i].render(batch, i == activeButtonIndex);
            }
        }
        batch.end();
    }

    private void renderScaledButton(SpriteBatch batch, BottomPanelButton button, boolean isSelected) {
        Texture renderTexture = isSelected ? button.getSelectedTexture() : button.getTexture();
        if (renderTexture == null) return;

        int scaledWidth = (int) (button.getWidth() * WALK_SCALE);
        int scaledHeight = (int) (button.getHeight() * WALK_SCALE);
        int offsetX = (button.getWidth() - scaledWidth) / 2;
        int offsetY = (button.getHeight() - scaledHeight) / 2;

        batch.draw(renderTexture, button.getX() + offsetX, button.getY() + offsetY, scaledWidth, scaledHeight);
    }

    public boolean handleTouch(int touchX, int touchY) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null || !buttons[i].isAvailable()) continue;

            if (buttons[i].getLocation() == BottomPanelButton.LocationType.WALK) {
                int scaledWidth = (int) (buttons[i].getWidth() * WALK_SCALE);
                int scaledHeight = (int) (buttons[i].getHeight() * WALK_SCALE);
                int offsetX = (buttons[i].getWidth() - scaledWidth) / 2;
                int offsetY = (buttons[i].getHeight() - scaledHeight) / 2;
                int hitX = buttons[i].getX() + offsetX;
                int hitY = buttons[i].getY() + offsetY;

                if (touchX >= hitX && touchX <= hitX + scaledWidth &&
                        touchY >= hitY && touchY <= hitY + scaledHeight) {
                    setActiveButton(i);
                    return true;
                }
            } else if (buttons[i].contains(touchX, touchY)) {
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