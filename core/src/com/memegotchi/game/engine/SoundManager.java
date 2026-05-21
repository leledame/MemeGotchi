package com.memegotchi.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private Music backgroundMusic;
    private Sound eatSound;
    private Sound tapSound;

    public void playBackground() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    public void playEatSound() {
        eatSound = Gdx.audio.newSound(Gdx.files.internal("sounds/eat.wav"));
        eatSound.play(0.7f);
    }
}