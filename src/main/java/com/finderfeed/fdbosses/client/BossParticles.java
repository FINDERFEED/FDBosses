package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.client.particles.DecalParticleOptions;
import com.finderfeed.fdbosses.client.particles.DecalParticleType;
import com.finderfeed.fdbosses.client.particles.GravityOptionsParticleType;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningOptions;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticle;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedRayOptions;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticleOptions;
import com.finderfeed.fdbosses.client.particles.malkuth_slash.MalkuthHorizontalSlashOptions;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.sonic_particle.SonicParticleOptions;
import com.finderfeed.fdbosses.FDBosses;

import com.finderfeed.fdbosses.client.particles.square_preparation_particle.RectanglePreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.client.particles.vanilla_like.ASParticleType;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile.JudgementBallExplosionParticleOptions;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
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

    public static final Supplier<ParticleType<RushParticleOptions>> RUSH_PARTICLE = PARTICLES.register("rush_particle",()->new ParticleType<RushParticleOptions>(true) {
        @Override
        public MapCodec<RushParticleOptions> codec() {
            return RushParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RushParticleOptions> streamCodec() {
            return RushParticleOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ParticleType<MalkuthHorizontalSlashOptions>> MALKUTH_HORIZONTAL_SLASH = PARTICLES.register("malkuth_horizontal_slash",()->new ParticleType<MalkuthHorizontalSlashOptions>(true) {
        @Override
        public MapCodec<MalkuthHorizontalSlashOptions> codec() {
            return MalkuthHorizontalSlashOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MalkuthHorizontalSlashOptions> streamCodec() {
            return MalkuthHorizontalSlashOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ParticleType<ArcAttackPreparationParticleOptions>> ARC_ATTACK_PREPARATION_PARTICLE = PARTICLES.register("arc_attack_preparation_particle",()->new ParticleType<ArcAttackPreparationParticleOptions>(true) {
        @Override
        public MapCodec<ArcAttackPreparationParticleOptions> codec() {
            return ArcAttackPreparationParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ArcAttackPreparationParticleOptions> streamCodec() {
            return ArcAttackPreparationParticleOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ParticleType<RectanglePreparationParticleOptions>> RECTANGLE_PREPARATION_PARTICLE = PARTICLES.register("rectangle_preparation_particle",()->new ParticleType<RectanglePreparationParticleOptions>(true) {
        @Override
        public MapCodec<RectanglePreparationParticleOptions> codec() {
            return RectanglePreparationParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RectanglePreparationParticleOptions> streamCodec() {
            return RectanglePreparationParticleOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ParticleType<StripeParticleOptions>> STRIPE_PARTICLE = PARTICLES.register("stripe_particle",()->new ParticleType<StripeParticleOptions>(true) {
        @Override
        public MapCodec<StripeParticleOptions> codec() {
            return StripeParticleOptions.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StripeParticleOptions> streamCodec() {
            return StripeParticleOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ParticleType<GravityParticleOptions>> FLAME_WITH_STONE = PARTICLES.register("flame_with_stone",()->new GravityOptionsParticleType(true));
    public static final Supplier<ParticleType<GravityParticleOptions>> ICE_CHUNK = PARTICLES.register("ice_chunk",()->new GravityOptionsParticleType(true));



    public static final Supplier<ParticleType<GeburahRayOptions>> GEBURAH_RAY_ATTACK = PARTICLES.register("geburah_ray",()->new ParticleType<GeburahRayOptions>(true) {
        @Override
        public MapCodec<GeburahRayOptions> codec() {
            return GeburahRayOptions.mapCodec();
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GeburahRayOptions> streamCodec() {
            return GeburahRayOptions.STREAM_CODEC;
        }
    });


    public static final Supplier<ParticleType<JudgementBallExplosionParticleOptions>> JUDGEMENT_BALL_EXPLOSION = PARTICLES.register("judgement_ball_explosion",()->new ParticleType<JudgementBallExplosionParticleOptions>(true) {
        @Override
        public MapCodec<JudgementBallExplosionParticleOptions> codec() {
            return JudgementBallExplosionParticleOptions.MAP_CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, JudgementBallExplosionParticleOptions> streamCodec() {
            return JudgementBallExplosionParticleOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ParticleType<DecalParticleOptions>> GEBURAH_RAY_DECAL = PARTICLES.register("geburah_ray_decal", () ->new DecalParticleType(true));

    public static final Supplier<ParticleType<ColoredJumpingParticleOptions>> COLORED_JUMPING_PARTICLE = PARTICLES.register("colored_jumping_particles", () -> new ParticleType<ColoredJumpingParticleOptions>(true) {
        @Override
        public MapCodec<ColoredJumpingParticleOptions> codec() {
            return ColoredJumpingParticleOptions.MAP_CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ColoredJumpingParticleOptions> streamCodec() {
            return ColoredJumpingParticleOptions.STREAM_CODEC;
        }
    });

    public static final Supplier<ASParticleType> SMALL_GEAR = PARTICLES.register("small_gear", ()-> new ASParticleType(true));
    public static final Supplier<ASParticleType> GEAR = PARTICLES.register("gear", ()-> new ASParticleType(true));

}
