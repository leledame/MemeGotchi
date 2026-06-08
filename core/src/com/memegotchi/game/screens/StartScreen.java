package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.FontBuilder;
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

        buttonFont = FontBuilder.generate(36, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf");
        titleFont = FontBuilder.generate(50, Color.PINK, "fonts/segoe-ui-emoji_0.ttf");


        int centerX = (GameResources.SCREEN_WIDTH - 600) / 2;

        startButton = new Button(centerX, 600, 600, 180, buttonFont, GameResources.BUTTON_TEXT, "START");
        startButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
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

        titleFont.draw(game.batch, "✨ MEMEGOTCHI ✨",
                GameResources.SCREEN_WIDTH / 2f - 230,
                GameResources.SCREEN_HEIGHT - 250);

        startButton.render(game.batch, false);
        game.batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            com.badlogic.gdx.math.Vector3 touchPos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            BaseScreen.screenToWorld(game, touchPos);
            int worldX = (int) touchPos.x;
            int worldY = (int) touchPos.y;

            if (startButton.contains(worldX, worldY)) {
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