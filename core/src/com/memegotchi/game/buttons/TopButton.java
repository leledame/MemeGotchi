package com.memegotchi.game.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.panels.TopPanel;

public class TopButton extends Button {
    private TopPanel.TopMenuType menu;

    public TopButton(int x, int y, int width, int height, TopPanel.TopMenuType menu) {
        super(x, y, width, height);
        this.menu = menu;
        loadTopTextures();
    }

    private void loadTopTextures() {
        String texturePath = GameResources.getTopButtonTexturePath(menu);
        String selectedPath = GameResources.getTopButtonSelectedTexturePath(menu);

        if (texturePath != null) {
            texture = new Texture(texturePath);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        if (selectedPath != null) {
            selectedTexture = new Texture(selectedPath);
            selectedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    public TopPanel.TopMenuType getMenu() {
        return menu;
    }
}
