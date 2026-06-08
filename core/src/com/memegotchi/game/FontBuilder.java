package com.memegotchi.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontBuilder {

    // Строка со всеми необходимыми символами: буквы, цифры, знаки препинания и эмодзи
    private static final String CHARS =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "0123456789" +
                    "!@#$%^&*()_+\\-=\\[\\]{};:'\"\\|,.<>/?~`" +
                    " \n" +
                    // Эмодзи (популярные) – можно добавить любые, которые нужны
                    "😀😁😂😃😄😅😆😉😊😋😎😍😘😗😙😚☺️🙂🤗🤔😐😑😶🙄😏😣😥😮🤐😯😪😫😴😌😛😜😝🤤😒😓😔😕🙃🤑😲☹️🙁😖😞😟😤😢😭😦😧😨😩🤯😬😰😱🥵🥶😳🤪😵😡😠🤬😷🤒🤕🤢🤮🤧😇🤠🤡🥳🥴🥺🤥🤫🤭🧐🤨😈👿👹👺💀👻👽🤖💩😺😸😹😻😼😽🙀😿😾🙈🙉🙊💋💌💘💝💖💗💓💞💕💟❣️❤️🧡💛💚💙💜🤎🖤🤍💔❤️‍🔥❤️‍🩹💯💢💥💫💦💨🕳️💣💬👁️‍🗨️🗨️🗯️💭💤" +
                    "🍕😊⚡🧼💰🛒🎾✨🛒";

    public static BitmapFont generate(int size, Color color, String fontPath) {
        return generate(size, color, fontPath, 0, null);
    }

    public static BitmapFont generate(int size, Color color, String fontPath, float borderWidth, Color borderColor) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        parameter.characters = CHARS;
        if (borderWidth > 0 && borderColor != null) {
            parameter.borderWidth = borderWidth;
            parameter.borderColor = borderColor;
        }
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}