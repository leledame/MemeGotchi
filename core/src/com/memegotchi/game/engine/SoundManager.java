package com.memegotchi.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundManager {
    private Music currentMusic;
    private List<String> playlist;
    private List<String> shuffled;
    private int currentTrackIndex = 0;
    private float musicVolume = 0.5f;

    private Sound clickSound;
    private Sound cashSound;
    private Sound washSound;
    private Sound fishingStartSound;
    private Sound fishingStopSound;
    private Sound sleepingCatSound;
    private Sound nightSound;

    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private boolean isSleeping = false;
    private long sleepingSoundId = -1;
    private long nightSoundId = -1;

    public SoundManager() {
        playlist = new ArrayList<String>();
        for (int i = 1; i <= 8; i++) {
            playlist.add("music/music_" + i + ".mp3");
        }
        reshuffle();
    }

    private void reshuffle() {
        shuffled = new ArrayList<String>(playlist);
        Collections.shuffle(shuffled);
        currentTrackIndex = 0;
    }

    public void loadSettings(boolean musicOn, boolean soundOn) {
        this.musicEnabled = musicOn;
        this.soundEnabled = soundOn;
        loadSounds();
        if (musicEnabled) {
            playNext();
        } else {
            stopMusic();
        }
    }

    private void loadSounds() {
        if (clickSound == null) clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.mp3"));
        if (cashSound == null) cashSound = Gdx.audio.newSound(Gdx.files.internal("sounds/cash-machine.mp3"));
        if (washSound == null) washSound = Gdx.audio.newSound(Gdx.files.internal("sounds/wash.mp3"));
        if (fishingStartSound == null) fishingStartSound = Gdx.audio.newSound(Gdx.files.internal("sounds/fishing_start.mp3"));
        if (fishingStopSound == null) fishingStopSound = Gdx.audio.newSound(Gdx.files.internal("sounds/fishing_stop.mp3"));
        if (sleepingCatSound == null) sleepingCatSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sleeping_cat.mp3"));
        if (nightSound == null) nightSound = Gdx.audio.newSound(Gdx.files.internal("sounds/night.mp3"));
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (musicEnabled) {
            playNext();
        } else {
            stopMusic();
        }
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    private void playNext() {
        if (!musicEnabled) return;
        if (shuffled == null || shuffled.isEmpty()) return;

        stopMusic();

        if (currentTrackIndex >= shuffled.size()) {
            reshuffle();
        }

        String path = shuffled.get(currentTrackIndex);
        currentTrackIndex++;

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setVolume(isSleeping ? musicVolume * 0.3f : musicVolume);
        currentMusic.setLooping(false);
        final Music track = currentMusic;
        currentMusic.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                if (currentMusic == track) {
                    currentMusic = null;
                }
                music.dispose();
                playNext();
            }
        });
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
    }

    public void setSleeping(boolean sleeping) {
        if (isSleeping == sleeping) return;
        isSleeping = sleeping;

        if (currentMusic != null) {
            currentMusic.setVolume(sleeping ? musicVolume * 0.3f : musicVolume);
        }

        if (sleeping) {
            if (soundEnabled && sleepingCatSound != null) {
                sleepingSoundId = sleepingCatSound.play(0.5f);
                sleepingCatSound.setLooping(sleepingSoundId, true);
            }
            if (soundEnabled && nightSound != null) {
                nightSoundId = nightSound.play(0.3f);
                nightSound.setLooping(nightSoundId, true);
            }
        } else {
            if (sleepingSoundId >= 0 && sleepingCatSound != null) {
                sleepingCatSound.stop(sleepingSoundId);
                sleepingSoundId = -1;
            }
            if (nightSoundId >= 0 && nightSound != null) {
                nightSound.stop(nightSoundId);
                nightSoundId = -1;
            }
        }
    }

    public void playClick() {
        if (!soundEnabled || clickSound == null) return;
        clickSound.play(0.5f);
    }

    public void playCash() {
        if (!soundEnabled || cashSound == null) return;
        cashSound.play(0.7f);
    }

    public void playWash() {
        if (!soundEnabled || washSound == null) return;
        washSound.play(0.6f);
    }

    public void playFishingStart() {
        if (!soundEnabled || fishingStartSound == null) return;
        fishingStartSound.play(0.6f);
    }

    public void playFishingStop() {
        if (!soundEnabled || fishingStopSound == null) return;
        fishingStopSound.play(0.6f);
    }

    public void dispose() {
        stopMusic();
        if (sleepingSoundId >= 0 && sleepingCatSound != null) {
            sleepingCatSound.stop(sleepingSoundId);
        }
        if (nightSoundId >= 0 && nightSound != null) {
            nightSound.stop(nightSoundId);
        }
        if (clickSound != null) clickSound.dispose();
        if (cashSound != null) cashSound.dispose();
        if (washSound != null) washSound.dispose();
        if (fishingStartSound != null) fishingStartSound.dispose();
        if (fishingStopSound != null) fishingStopSound.dispose();
        if (sleepingCatSound != null) sleepingCatSound.dispose();
        if (nightSound != null) nightSound.dispose();
    }
}
