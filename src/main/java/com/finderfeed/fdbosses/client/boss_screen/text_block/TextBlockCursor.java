package com.finderfeed.fdbosses.client.boss_screen.text_block;

import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;

public class TextBlockCursor {


    public float initX;

    public float x;
    public float y;

    public TextBlockCursor(float x, float y){
        this.y = y;
        this.initX = x;
        this.x = x;
    }

    public void nextLine(Font font){
        this.nextLine(font.lineHeight);
    }

    public void nextLine(float amount){
        this.y += amount;
        this.x = initX;
    }

    public void addX(float width){
        this.x = Mth.clamp(x + width, initX, Float.MAX_VALUE);
    }

    public boolean shouldGoToNextLine(float width, float borderX){
        return width + x > borderX;
    }

    public float remainingWidth(float borderX){
        return Math.max(0,borderX - x);
    }

}
