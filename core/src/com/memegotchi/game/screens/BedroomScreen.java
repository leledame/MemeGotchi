package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.engine.PetEngine;

public class BedroomScreen extends BaseScreen {
    private Button sleepButton;

    public BedroomScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_BEDROOM_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }

    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.BEDROOM;
    }

    @Override
    public boolean shouldDrawCharacter() {
        return true;
    }

    @Override
    protected String getCharacterFolder() {
        return "bedroom";
    }

    @Override
    protected boolean hasSleepingTexture() {
        return true;
    }

    @Override
    protected String getNightBackgroundPath() {
        return GameResources.BACKGROUND_BEDROOM_NIGHT;
    }

    @Override
    protected float getCharacterScaleMultiplier() {
        return 0.75f;
    }

    @Override
    protected float getCharacterYShift() {
        return WORLD_HEIGHT * -0.0048f + WORLD_HEIGHT * -0.01f;
    }

    @Override
    protected float getCharacterXShift() {
        return WORLD_WIDTH * 0.01f;
    }

    @Override
    protected void onScreenShow() {
        super.onScreenShow();
        int btnW = 200;
        int btnH = 80;
        int btnX = (WORLD_WIDTH - btnW) / 2 - (int) (WORLD_WIDTH * 0.01f);
        int btnY = (int) (WORLD_HEIGHT * 0.30f);
        sleepButton = new Button(btnX, btnY, btnW, btnH, moveFont, GameResources.BUTTON_TEXT, "Sleep");
        if (sleepButton.getTexture() != null) {
            sleepButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();

        if (catIsHere && sleepButton != null
                && petEngine != null && !petEngine.isSleeping()
                && petEngine.getPet().getEnergy() < 80) {
            batch.begin();
            sleepButton.render(batch, false);
            batch.end();

            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                if (screenManager instanceof MemeGotchi) {
                    BaseScreen.screenToWorld((MemeGotchi) screenManager, touchPos);
                }
                if (sleepButton.contains((int) touchPos.x, (int) touchPos.y)) {
                    petEngine.sleep();
                    startSleeping();
                    showMessage("Zzz...");
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (sleepButton != null) sleepButton.dispose();
        super.dispose();
    }
}
