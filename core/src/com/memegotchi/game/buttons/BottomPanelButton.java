package com.memegotchi.game.buttons;

import com.badlogic.gdx.graphics.Texture;

public class BottomPanelButton extends Button {

    public enum LocationType {
        LIVING,
        WALK,
        KITCHEN,
        TOILET,
        BEDROOM
    }

    private LocationType location;

    public BottomPanelButton(int x, int y, int width, int height, LocationType location) {
        super(x, y, width, height);
        this.location = location;
    }

    public void loadTextures() {
        String texturePath = com.memegotchi.game.GameResources.getBottomPanelButtonTexturePath(location);
        String selectedPath = com.memegotchi.game.GameResources.getBottomPanelButtonSelectedTexturePath(location);

        if (texturePath != null) {
            texture = new Texture(texturePath);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        if (selectedPath != null) {
            selectedTexture = new Texture(selectedPath);
            selectedTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public LocationType getLocation() {
        return location;
    }
}