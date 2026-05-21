package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;

public class SkinScreen {
    private MemeGotchi game;
    private Button backButton;
    BitmapFont font;

    public SkinScreen(MemeGotchi game) {
        font = new BitmapFont();
        font.getData().setScale(2f);
        this.game = game;
        backButton = new Button(500, 100, 440, 70, font, GameResources.BUTTON_TEXT, "Back");
    }

    public void render(float delta) {
        ScreenUtils.clear(0.3f, 0.2f, 0.4f, 1);

        game.batch.begin();
        backButton.render(game.batch, false);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = GameResources.SCREEN_HEIGHT - Gdx.input.getY();
            if (backButton.contains(x, y)) {
                game.setScreen(game.livingRoomScreen);
            }
        }
    }

    public void dispose() {
        backButton.dispose();
    }
}