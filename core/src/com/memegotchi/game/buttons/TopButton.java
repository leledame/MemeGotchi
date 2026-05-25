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

        // Если для SETTINGS нет текстур, используем стандартную
        if (menu == TopPanel.TopMenuType.SETTINGS && texturePath == null) {
            texturePath = "buttons/settings.png";
            selectedPath = "buttons/settings.png";
        }

        try {
            if (texturePath != null) {
                texture = new Texture(texturePath);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
            if (selectedPath != null && !selectedPath.equals(texturePath)) {
                selectedTexture = new Texture(selectedPath);
                selectedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            } else if (texture != null) {
                selectedTexture = texture;
            }
        } catch (Exception e) {
            texture = null;
            selectedTexture = null;
        }
    }
    public TopPanel.TopMenuType getMenu() {
        return menu;
    }
}
