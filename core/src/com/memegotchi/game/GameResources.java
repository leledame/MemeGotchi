package com.memegotchi.game;

import com.memegotchi.game.buttons.BottomPanelButton;
import com.memegotchi.game.panels.TopPanel;

public class GameResources {
    // Фоны
    public static final String BACKGROUND_DAY = "backgronds/living_room/day.png";
    public static final String BACKGROUND_BEDROOM_DAY = "backgronds/bedroom/day.png";
    public static final String BACKGROUND_KITCHEN_DAY = "backgronds/kitchen/day.png";
    public static final String BACKGROUND_BATHROOM_DAY = "backgronds/bathroom/day.png";
    public static final String BACKGROUND_FISHING_DAY = "backgronds/fishing/fishingday.png";
    public static final String START = "backgronds/start/start_background.png";

    // Персонаж
    public static final String CHARACTER_BASE = "charachters/female_cat/living_room/base.png";

    // Мини-игра
    public static final String FISHING_MINIGAME_BG = "backgronds/fishing/gamefish.png";
    public static final String FISHING_FISH = "backgronds/fishing/fish_for_game.png";
    public static final String FISHING_ZONE = "backgronds/fishing/green_zone.png";

    // Кнопки
    public static final String BUTTON_TEXT = "buttons/text_button/button.png";

    public static final int SCREEN_WIDTH = 720;
    public static final int SCREEN_HEIGHT = 1280;

    private static final String BOTTOM_PANEL_BUTTONS_PATH = "buttons/bottom_panel/";
    private static final String TOP_BUTTONS_PATH = "buttons/";

    public static String getBottomPanelButtonTexturePath(BottomPanelButton.LocationType location) {
        switch (location) {
            case LIVING: return BOTTOM_PANEL_BUTTONS_PATH + "living.png";
            case WALK:   return BOTTOM_PANEL_BUTTONS_PATH + "walk.png";
            case KITCHEN:return BOTTOM_PANEL_BUTTONS_PATH + "kitchen.png";
            case TOILET: return BOTTOM_PANEL_BUTTONS_PATH + "toilet.png";
            case BEDROOM:return BOTTOM_PANEL_BUTTONS_PATH + "bedroom.png";
            default: return null;
        }
    }

    public static String getBottomPanelButtonSelectedTexturePath(BottomPanelButton.LocationType location) {
        switch (location) {
            case LIVING: return BOTTOM_PANEL_BUTTONS_PATH + "living_choice.png";
            case WALK:   return BOTTOM_PANEL_BUTTONS_PATH + "walk_choice.png";
            case KITCHEN:return BOTTOM_PANEL_BUTTONS_PATH + "kitchen_choice.png";
            case TOILET: return BOTTOM_PANEL_BUTTONS_PATH + "toilet_choice.png";
            case BEDROOM:return BOTTOM_PANEL_BUTTONS_PATH + "bedroom_choice.png";
            default: return null;
        }
    }

    public static String getTopButtonTexturePath(TopPanel.TopMenuType menu) {
        if (menu == TopPanel.TopMenuType.SHOP) {
            return TOP_BUTTONS_PATH + "shop.png";
        }
        return null;
    }

    public static String getTopButtonSelectedTexturePath(TopPanel.TopMenuType menu) {
        if (menu == TopPanel.TopMenuType.SHOP) {
            return TOP_BUTTONS_PATH + "shop_selected.png";
        }
        return null;
    }
}