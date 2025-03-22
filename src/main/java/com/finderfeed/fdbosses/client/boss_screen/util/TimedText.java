package com.finderfeed.fdbosses.client.boss_screen.util;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TimedText {

    public Component text;

    public int currentTime;

    public TimedText(){
    }

    public void setText(Component component, int time){
        this.currentTime = time;
        this.text = component;
    }

    public void tick(){
        if (this.currentTime == 0){
            text = null;
        }
        this.currentTime = Mth.clamp(currentTime - 1, 0, Integer.MAX_VALUE);
    }

}
