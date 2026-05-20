package com.memegotchi.game.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Texture texture;
    protected Texture selectedTexture;
    protected boolean available = true;

    public Button(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setTextures(Texture texture, Texture selectedTexture) {
        this.texture = texture;
        this.selectedTexture = selectedTexture;
    }

    public void render(SpriteBatch batch, boolean isSelected) {
        if (!available) {
            return;
        }

        Texture renderTexture = isSelected ? selectedTexture : texture;
        if (renderTexture != null) {
            batch.draw(renderTexture, x, y, width, height);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Texture getTexture() {
        return texture;
    }

    public Texture getSelectedTexture() {
        return selectedTexture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean contains(int touchX, int touchY) {
        return touchX >= x && touchX <= x + width &&
               touchY >= y && touchY <= y + height;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (selectedTexture != null) selectedTexture.dispose();
    }
}