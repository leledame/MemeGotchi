package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.buttons.Button;
import com.memegotchi.game.engine.PetEngine;

public class BathroomScreen extends BaseScreen {
    private Button washButton;

    public BathroomScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public String getBackgroundPath() {
        return GameResources.BACKGROUND_BATHROOM_DAY;
    }

    @Override
    public String getCharacterPath() {
        return GameResources.CHARACTER_BASE;
    }

    @Override
    public boolean shouldDrawCharacter() {
        return true;
    }
    @Override
    public CatRoomState getCatRoomState() {
        return CatRoomState.TOILET;
    }

    @Override
    protected String getNightBackgroundPath() {
        return GameResources.BACKGROUND_BATHROOM_NIGHT;
    }

    @Override
    protected void onScreenShow() {
        super.onScreenShow();
        int btnW = 300;
        int btnH = 120;
        int btnX = (WORLD_WIDTH - btnW) / 2 - (int) (WORLD_WIDTH * 0.01f);
        int btnY = (int) (WORLD_HEIGHT * 0.40f);
        washButton = new Button(btnX, btnY, btnW, btnH, moveFont, GameResources.BUTTON_TEXT, "Wash");
        if (washButton.getTexture() != null) {
            washButton.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        boolean catIsHere = screenManager != null
                && screenManager.getCurrentCatRoom() == getCatRoomState();

        if (catIsHere && washButton != null
                && petEngine != null && petEngine.getPet() != null
                && petEngine.getPet().hasShampoo()) {
            batch.begin();
            washButton.render(batch, false);
            batch.end();

            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                if (screenManager instanceof MemeGotchi) {
                    BaseScreen.screenToWorld((MemeGotchi) screenManager, touchPos);
                }
                if (washButton.contains((int) touchPos.x, (int) touchPos.y)) {
                    if (petEngine.getPet().useShampoo()) {
                        if (screenManager instanceof MemeGotchi) {
                            ((MemeGotchi) screenManager).getSoundManager().playWash();
                        }
                        int clean = Math.min(100, petEngine.getPet().getCleanliness() + 80);
                        petEngine.getPet().setCleanliness(clean);
                        showMessage("+80 cleanliness!");
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (washButton != null) washButton.dispose();
        super.dispose();
    }
}
