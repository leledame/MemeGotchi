package com.memegotchi.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private Music backgroundMusic;
    private Sound eatSound;
    private Sound tapSound;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    public void loadSettings(boolean musicOn, boolean soundOn) {
        this.musicEnabled = musicOn;
        this.soundEnabled = soundOn;
        if (musicEnabled) {
            playBackground();
        } else {
            stopBackground();
        }
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (musicEnabled) {
            playBackground();
        } else {
            stopBackground();
        }
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public void playBackground() {
        if (!musicEnabled) return;
        if (backgroundMusic == null) {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
        }
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void stopBackground() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    public void playEatSound() {
        if (!soundEnabled) return;
        if (eatSound == null) {
            eatSound = Gdx.audio.newSound(Gdx.files.internal("sounds/eat.wav"));
        }
        eatSound.play(0.7f);
    }

    public void playTapSound() {
        if (!soundEnabled) return;
        if (tapSound == null) {
            tapSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tap.wav"));
        }
        tapSound.play(0.5f);
    }

    public void dispose() {
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (eatSound != null) eatSound.dispose();
        if (tapSound != null) tapSound.dispose();
    }
}