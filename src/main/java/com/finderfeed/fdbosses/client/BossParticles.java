package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningOptions;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedRayOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.sonic_particle.SonicParticleOptions;
import com.finderfeed.fdbosses.FDBosses;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, FDBosses.MOD_ID);


    public static final Supplier<ParticleType<ChesedRayOptions>> CHESED_RAY_ATTACK = PARTICLES.register("chesed_ray",()->new ParticleType<ChesedRayOptions>(true) {
        @Override
        public MapCodec<ChesedRayOptions> codec() {
            return ChesedRayOptions.mapCodec();
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ChesedRayOptions> streamCodec() {
            return ChesedRayOptions.STREAM_CODEC;
        }
    });


    public static final Supplier<ParticleType<ArcLightningOptions>> ARC_LIGHTNING = PARTICLES.register("arc",()-> new ParticleType<>(true) {
        @Override
        public MapCodec<ArcLightningOptions> codec() {
            return ArcLightningOptions.createCodec(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ArcLightningOptions> streamCodec() {
            return ArcLightningOptions.createStreamCodec(this);
        }
    });

    public static final Supplier<ParticleType<SonicParticleOptions>> SONIC_PARTICLE = PARTICLES.register("sonic",()-> new ParticleType<>(true) {
        @Override
        public MapCodec<SonicParticleOptions> codec() {
            return SonicParticleOptions.createCodec();
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SonicParticleOptions> streamCodec() {
            return SonicParticleOptions.STREAM_CODEC;
        }
    });


    public static final Supplier<ParticleType<BigSmokeParticleOptions>> BIS_SMOKE = PARTICLES.register("big_smoke",()-> new ParticleType<>(true) {
        @Override
        public MapCodec<BigSmokeParticleOptions> codec() {
            return BigSmokeParticleOptions.MAP_CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BigSmokeParticleOptions> streamCodec() {
            return BigSmokeParticleOptions.STREAM_CODEC;
        }
    });



}
