package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.engine.PetEngine;

public class KitchenScreen extends BaseScreen {
    private Button eatButton;

    public KitchenScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_KITCHEN_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }
    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.KITCHEN;
    }
    @Override
    public boolean shouldDrawCharacter() {
        return true;
    }

    @Override
    protected String getCharacterFolder() {
        return "kitchen";
    }

    @Override
    protected float getCharacterScaleMultiplier() {
        return 0.95f;
    }

    @Override
    protected float getCharacterYShift() {
        return WORLD_HEIGHT * -0.0048f;
    }

    @Override
    protected String getNightBackgroundPath() {
        return GameResources.BACKGROUND_KITCHEN_NIGHT;
    }

    @Override
    protected void onScreenShow() {
        super.onScreenShow();
        int btnW = 200;
        int btnH = 80;
        int btnX = (WORLD_WIDTH - btnW) / 2 - (int) (WORLD_WIDTH * 0.01f);
        int btnY = (int) (WORLD_HEIGHT * 0.40f);
        eatButton = new Button(btnX, btnY, btnW, btnH, moveFont, GameResources.BUTTON_TEXT, "Eat");
        if (eatButton.getTexture() != null) {
            eatButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();

        if (catIsHere && eatButton != null
                && petEngine != null && petEngine.getPet() != null
                && petEngine.getPet().hasFish()) {
            batch.begin();
            eatButton.render(batch, false);
            batch.end();

            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                if (screenManager instanceof MemeGotchi) {
                    ((MemeGotchi) screenManager).camera.unproject(touchPos);
                }
                if (eatButton.contains((int) touchPos.x, (int) touchPos.y)) {
                    String fish = petEngine.getPet().takeTopFish();
                    if (fish != null) {
                        petEngine.feed();
                        showMessage("Ate " + fish + "! +20 hunger");
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (eatButton != null) eatButton.dispose();
        super.dispose();
    }
}
