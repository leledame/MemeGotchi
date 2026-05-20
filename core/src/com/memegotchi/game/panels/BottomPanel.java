package com.memegotchi.game.panels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.buttons.BottomPanelButton;

public class BottomPanel extends Panel {
    private static final int BUTTON_COUNT = 5;
    private static final float BORDER_SCALE = 18f;
    private static final float BUTTON_SIZE_RATIO = 0.6f; // кнопка = 60% высоты панели
    private static final float KITCHEN_SCALE = 1.50f;

    private BottomPanelButton[] buttons;
    private int activeButtonIndex = 0;

    public BottomPanel(int screenWidth, int screenHeight, float scale) {
        super(screenWidth, screenHeight, scale, true);
        initializeButtons();
    }

    private void initializeButtons() {
        buttons = new BottomPanelButton[BUTTON_COUNT];
        BottomPanelButton.LocationType[] locations = {
                BottomPanelButton.LocationType.LIVING,
                BottomPanelButton.LocationType.WALK,
                BottomPanelButton.LocationType.KITCHEN,
                BottomPanelButton.LocationType.TOILET,
                BottomPanelButton.LocationType.BEDROOM
        };

        // Масштабируем размер кнопки под реальный экран
        int buttonSize = (int) (panelHeight * 0.6f); // 60% от высоты панели, не зависит от scale
        int slotWidth = screenWidth / BUTTON_COUNT;
        int buttonY = panelY + (panelHeight - buttonSize) / 2;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            int buttonX = i * slotWidth + (slotWidth - buttonSize) / 2;
            if (locations[i] == BottomPanelButton.LocationType.TOILET) buttonX += 30;
            if (locations[i] == BottomPanelButton.LocationType.KITCHEN) buttonX += 10;
            buttons[i] = new BottomPanelButton(buttonX, buttonY, buttonSize, buttonSize, locations[i]);
            buttons[i].loadTextures();
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        int border = Math.max(2, (int) (scale * BORDER_SCALE));

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(1f, 0.8f, 0.835f, 1f);
        shapeRenderer.rect(0, panelY, screenWidth, panelHeight);

        shapeRenderer.setColor(0.8f, 0.56f, 0.58f, 1f);
        shapeRenderer.rect(0, panelY, screenWidth, panelHeight - border);

        shapeRenderer.end();

        batch.begin();
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getLocation() == BottomPanelButton.LocationType.KITCHEN) {
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

        int scaledWidth = (int) (button.getWidth() * KITCHEN_SCALE);
        int scaledHeight = (int) (button.getHeight() * KITCHEN_SCALE);
        int offsetX = (button.getWidth() - scaledWidth) / 2;
        int offsetY = (button.getHeight() - scaledHeight) / 2;

        batch.draw(renderTexture, button.getX() + offsetX, button.getY() + offsetY, scaledWidth, scaledHeight);
    }

    public boolean handleTouch(int touchX, int touchY) {
        boolean changed = false;
        for (int i = 0; i < buttons.length; i++) {
            if (!buttons[i].isAvailable()) continue;

            if (buttons[i].getLocation() == BottomPanelButton.LocationType.KITCHEN) {
                int scaledWidth = (int) (buttons[i].getWidth() * KITCHEN_SCALE);
                int scaledHeight = (int) (buttons[i].getHeight() * KITCHEN_SCALE);
                int offsetX = (buttons[i].getWidth() - scaledWidth) / 2;
                int offsetY = (buttons[i].getHeight() - scaledHeight) / 2;
                int hitX = buttons[i].getX() + offsetX;
                int hitY = buttons[i].getY() + offsetY;

                if (touchX >= hitX && touchX <= hitX + scaledWidth &&
                        touchY >= hitY && touchY <= hitY + scaledHeight) {
                    setActiveButton(i);
                    changed = true;
                    break;
                }
            } else if (buttons[i].contains(touchX, touchY)) {
                setActiveButton(i);
                changed = true;
                break;
            }
        }
        return changed;
    }

    public void setActiveButton(int index) {
        if (index >= 0 && index < BUTTON_COUNT) {
            activeButtonIndex = index;
        }
    }

    /**
     * Устанавливает активную кнопку по типу локации.
     * Используется для синхронизации при смене экрана.
     */
    public void setActiveLocation(BottomPanelButton.LocationType location) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getLocation() == location) {
                activeButtonIndex = i;
                return;
            }
        }
    }

    public int getActiveButtonIndex() {
        return activeButtonIndex;
    }

    public BottomPanelButton.LocationType getActiveLocation() {
        return buttons[activeButtonIndex].getLocation();
    }

    public void setWalkAvailable(boolean available) {
        buttons[1].setAvailable(available); // индекс 1 = WALK
    }

    @Override
    public void dispose() {
        for (BottomPanelButton button : buttons) {
            button.dispose();
        }
    }
}