package com.finderfeed.fdbosses.content.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ServerSoundInstance {

    public SoundEvent soundEvent;
    public SoundSource soundSource;
    public float volume;
    public float pitch;

    public ServerSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch){
        this.soundEvent = soundEvent;
        this.soundSource = source;
        this.volume = volume;
        this.pitch = pitch;
    }

}
