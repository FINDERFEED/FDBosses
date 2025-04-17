package com.finderfeed.fdbosses.content.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RepeatedSound {

    public int ticker = 0;

    public int timesToRepeat = 0;
    public int repeatFrequency;
    public ServerSoundInstance sound;
    public Vec3 pos;

    public RepeatedSound(SoundEvent soundEvent, SoundSource source, Vec3 pos, float volume, float pitch, int repeatFrequency, int timesToRepeat){
        this.sound = new ServerSoundInstance(soundEvent,source,volume,pitch);
        this.pos = pos;
        this.repeatFrequency = repeatFrequency;
        this.timesToRepeat = timesToRepeat;
    }

    public boolean tick(Level level){
        if (this.isFinished()){
            return true;
        }

        if (ticker++ % repeatFrequency == 0){
            level.playSound(null, pos.x,pos.y,pos.z,sound.soundEvent, sound.soundSource, sound.volume, sound.pitch);
        }

        return false;
    }

    public boolean isFinished(){
        return ticker / repeatFrequency + 1 >= timesToRepeat;
    }

}
