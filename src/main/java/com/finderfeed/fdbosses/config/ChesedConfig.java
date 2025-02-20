package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.ReflectiveSerializable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ChesedConfig implements ReflectiveSerializable<ChesedConfig> {


    public float electricSphereDamage = 10f;

    public float rockfallRayDamage = 10f;

    public float rockfallRockDamage = 5f;

    public float earthquakeRayDamage = 5f;

    public float eartquakeDamage = 5f;

    public float rollAttackDamage = 10f;

    public float blockAttackDamage = 10;

    public float rayDamage = 10;

    public boolean finalAttackHeals = true;

    public float finalAttackDamagePercentPerMonolith = 0.25f;



    @Override
    public Codec<ChesedConfig> reflectiveCodec() {
        return null;
    }
}
