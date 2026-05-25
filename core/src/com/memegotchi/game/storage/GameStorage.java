package com.memegotchi.game.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.memegotchi.game.model.PetModel;

public class GameStorage {
    private static final String PREFS_NAME = "memegotchi";
    private static final String KEY_HUNGER = "hunger";
    private static final String KEY_HAPPINESS = "happiness";
    private static final String KEY_ENERGY = "energy";
    private static final String KEY_CLEANLINESS = "cleanliness";
    private static final String KEY_COINS = "coins";
    private static final String KEY_LAST_SAVE = "lastSaveTime";
    private static final String KEY_MUSIC_ON = "musicOn";
    private static final String KEY_SOUND_ON = "soundOn";

    public boolean isMusicOn() { return prefs.getBoolean(KEY_MUSIC_ON, true); }
    public boolean isSoundOn() { return prefs.getBoolean(KEY_SOUND_ON, true); }
    public void saveMusicSettings(boolean on) { prefs.putBoolean(KEY_MUSIC_ON, on); prefs.flush(); }
    public void saveSoundSettings(boolean on) { prefs.putBoolean(KEY_SOUND_ON, on); prefs.flush(); }

    private Preferences prefs;

    public GameStorage() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public void save(PetModel pet) {
        prefs.putInteger(KEY_HUNGER, pet.getHunger());
        prefs.putInteger(KEY_HAPPINESS, pet.getHappiness());
        prefs.putInteger(KEY_ENERGY, pet.getEnergy());
        prefs.putInteger(KEY_CLEANLINESS, pet.getCleanliness());
        prefs.putInteger(KEY_COINS, pet.getCoins());
        prefs.putLong(KEY_LAST_SAVE, System.currentTimeMillis());
        prefs.flush();
    }

    public PetModel load() {
        PetModel pet = new PetModel();
        pet.setHunger(prefs.getInteger(KEY_HUNGER, 80));
        pet.setHappiness(prefs.getInteger(KEY_HAPPINESS, 80));
        pet.setEnergy(prefs.getInteger(KEY_ENERGY, 100));
        pet.setCleanliness(prefs.getInteger(KEY_CLEANLINESS, 100));
        pet.setCoins(prefs.getInteger(KEY_COINS, 100));
        pet.setLastUpdateTime(prefs.getLong(KEY_LAST_SAVE, System.currentTimeMillis()));
        return pet;
    }
}