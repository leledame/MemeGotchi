package com.memegotchi.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.memegotchi.game.FontBuilder;
import com.memegotchi.game.GameResources;
import com.memegotchi.game.MemeGotchi;
import com.memegotchi.game.engine.PetEngine;
import com.memegotchi.game.storage.GameStorage;

public class SettingsScreen extends BaseScreen {
    private Stage uiStage;
    private Skin skin;
    private Label musicLabel, soundLabel;
    private TextButton musicButton, soundButton, backButton;
    private boolean musicOn, soundOn;
    private GameStorage storage;

    public SettingsScreen(PetEngine petEngine) {
        super(petEngine);
    }

    @Override
    public void show() {
        super.show();
        storage = new GameStorage();
        musicOn = storage.isMusicOn();
        soundOn = storage.isSoundOn();

        uiStage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, ((MemeGotchi)screenManager).camera));
        Gdx.input.setInputProcessor(uiStage);

        skin = new Skin();
        // Простая текстурная кожа (можно создать через текстуру кнопки)
        TextButton.TextButtonStyle textStyle = new TextButton.TextButtonStyle();
        textStyle.font = FontBuilder.generate(36, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf");
        textStyle.up = textStyle.down = textStyle.over = null; // без текстуры, только текст
        skin.add("default", textStyle);

        Table table = new Table();
        table.setFillParent(true);
        uiStage.addActor(table);

        Label title = new Label("Settings", new Label.LabelStyle(FontBuilder.generate(60, Color.PINK, "fonts/segoe-ui-emoji_0.ttf"), Color.PINK));
        table.add(title).padBottom(50).row();

        musicLabel = new Label("Music: " + (musicOn ? "ON" : "OFF"), new Label.LabelStyle(FontBuilder.generate(40, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf"), Color.WHITE));
        musicButton = new TextButton("Toggle", textStyle);
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicOn = !musicOn;
                storage.saveMusicSettings(musicOn);
                musicLabel.setText("Music: " + (musicOn ? "ON" : "OFF"));
                if (screenManager instanceof MemeGotchi) {
                    ((MemeGotchi) screenManager).updateMusic();
                }
            }
        });
        table.add(musicLabel).pad(10);
        table.add(musicButton).pad(10).width(150).height(60);
        table.row();

        soundLabel = new Label("Sound: " + (soundOn ? "ON" : "OFF"), new Label.LabelStyle(FontBuilder.generate(40, Color.WHITE, "fonts/segoe-ui-emoji_0.ttf"), Color.WHITE));
        soundButton = new TextButton("Toggle", textStyle);
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundOn = !soundOn;
                storage.saveSoundSettings(soundOn);
                soundLabel.setText("Sound: " + (soundOn ? "ON" : "OFF"));
                if (screenManager instanceof MemeGotchi) {
                    ((MemeGotchi) screenManager).updateSound();
                }
            }
        });
        table.add(soundLabel).pad(10);
        table.add(soundButton).pad(10).width(150).height(60);
        table.row();

        backButton = new TextButton("Back", textStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenManager.backToPreviousScreen();
            }
        });
        table.add(backButton).colspan(2).padTop(50).width(200).height(80);
    }

    @Override
    public void render(float delta) {
        super.render(delta); // рисует фон и статы
        if (uiStage != null) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (uiStage != null) Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (uiStage != null) uiStage.dispose();
        if (skin != null) skin.dispose();
    }

    @Override public String getBackgroundPath() { return GameResources.BACKGROUND_DAY; }
    @Override public String getCharacterPath() { return GameResources.CHARACTER_BASE; }
    @Override public boolean shouldDrawCharacter() { return true; }
    @Override public CatRoomState getCatRoomState() { return CatRoomState.LIVING; }
}