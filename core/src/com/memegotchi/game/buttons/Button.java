package com.memegotchi.game.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Texture texture;
    protected Texture selectedTexture;
    protected boolean available = true;
    protected String text;
    protected BitmapFont font;
    protected GlyphLayout layout = new GlyphLayout();

    // Конструктор 1: только координаты и размер
    public Button(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Конструктор 2: с текстом и шрифтом (без текстуры)
    public Button(int x, int y, int width, int height, BitmapFont font, String text) {
        this(x, y, width, height);
        this.font = font;
        this.text = text;
    }

    // Конструктор 3: с путем к текстуре
    public Button(int x, int y, int width, int height, String texturePath) {
        this(x, y, width, height);
        setTexture(texturePath);
    }

    // Конструктор 4: с путем к текстуре и текстом
    public Button(int x, int y, int width, int height, BitmapFont font, String texturePath, String text) {
        this(x, y, width, height);
        this.font = font;
        this.text = text;
        setTexture(texturePath);
    }

    // Конструктор 5: с разными текстурами для обычного и выбранного состояния
    public Button(int x, int y, int width, int height, String texturePath, String selectedTexturePath) {
        this(x, y, width, height);
        setTextures(texturePath, selectedTexturePath);
    }

    public void setTexture(String texturePath) {
        if (texturePath != null) {
            this.texture = new Texture(texturePath);
            this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            this.selectedTexture = this.texture;
        }
    }

    public void setTextures(String texturePath, String selectedTexturePath) {
        if (texturePath != null) {
            this.texture = new Texture(texturePath);
            this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (selectedTexturePath != null) {
            this.selectedTexture = new Texture(selectedTexturePath);
            this.selectedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    public void setTextures(Texture texture, Texture selectedTexture) {
        this.texture = texture;
        this.selectedTexture = selectedTexture;
    }

    public void setText(String text, BitmapFont font) {
        this.text = text;
        this.font = font;
    }

    public void render(SpriteBatch batch, boolean isSelected) {
        if (!available) return;

        Texture renderTexture = isSelected ? selectedTexture : texture;
        if (renderTexture != null) {
            batch.draw(renderTexture, x, y, width, height);
        }

        // Рисуем текст поверх кнопки
        if (text != null && font != null) {
            layout.setText(font, text);
            float textX = x + (width - layout.width) / 2;
            float textY = y + (height + layout.height) / 2;
            font.draw(batch, text, textX, textY);
        }
    }

    public void draw(SpriteBatch batch) {
        render(batch, false);
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
        return touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (selectedTexture != null && selectedTexture != texture) selectedTexture.dispose();
    }
}