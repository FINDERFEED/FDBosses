package com.finderfeed.fdbosses.content.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DelayedSound {

    public SoundEvent soundEvent;
    public SoundSource soundSource;
    public Vec3 pos;
    public float volume;
    public float pitch;

    private int remainingTicks = 0;

    public DelayedSound(SoundEvent soundEvent, SoundSource source, Vec3 pos, float volume, float pitch, int delay){
        this.soundEvent = soundEvent;
        this.soundSource = source;
        this.volume = volume;
        this.pitch = pitch;
        this.pos = pos;
        this.remainingTicks = delay;
    }

    public boolean tick(){
        remainingTicks = Mth.clamp(remainingTicks - 1,0, Integer.MAX_VALUE);
        return remainingTicks == 0;
    }

    public boolean isFinished(){
        return remainingTicks == 0;
    }


    public void setRemainingTicks(int remainingTicks) {
        this.remainingTicks = remainingTicks;
    }
}
