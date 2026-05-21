package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.buttons.BottomPanelButton;

public class StartScreen extends ScreenAdapter {
    private final MemeGotchi game;
    private Button startButton;
    private Texture background;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    public StartScreen(MemeGotchi game) {
        this.game = game;
    }

    @Override
    public void show() {
        background = new Texture(GameResources.START);
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2.5f);
        buttonFont.setColor(Color.WHITE);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.5f);
        titleFont.setColor(Color.PINK);

        int centerX = (GameResources.SCREEN_WIDTH - 400) / 2;

        // Убираем дублирующий setText, текст уже передан в конструктор
        startButton = new Button(centerX, 600, 400, 120, buttonFont, GameResources.BUTTON_TEXT, "START");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.15f, 0.2f, 1);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, GameResources.SCREEN_WIDTH, GameResources.SCREEN_HEIGHT);
        }

        titleFont.draw(game.batch, "MEMEGOTCHI",
                GameResources.SCREEN_WIDTH / 2f - 170,
                GameResources.SCREEN_HEIGHT - 200);

        startButton.render(game.batch, false);
        game.batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            int screenX = Gdx.input.getX();
            int screenY = Gdx.input.getY();
            float worldX = screenX * GameResources.SCREEN_WIDTH / (float) Gdx.graphics.getWidth();
            float worldY = GameResources.SCREEN_HEIGHT - (screenY * GameResources.SCREEN_HEIGHT / (float) Gdx.graphics.getHeight());

            if (startButton.contains((int) worldX, (int) worldY)) {
                game.switchToLocation(BottomPanelButton.LocationType.LIVING);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (startButton != null) startButton.dispose();
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}