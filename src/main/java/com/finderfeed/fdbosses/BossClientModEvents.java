package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.boss_screen.text_block_processors.BossConfigFloatValueProcessor;
import com.finderfeed.fdbosses.client.boss_screen.text_block_processors.MobEffectTextProcessor;
import com.finderfeed.fdbosses.client.overlay.ElectrifiedOverlay;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedAttackRayParticle;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticle;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticle;
import com.finderfeed.fdbosses.client.particles.sonic_particle.SonicParticle;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_crystal.ChesedCrystalEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_vertical_ray.ChesedVerticalRayAttackRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlockRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.flying_block_entity.FlyingBlockEntityRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.kinetic_field.ChesedKineticFieldRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_sword_buff.FlyingSwordRenderer;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdbosses.content.projectiles.renderers.BlockProjectileRenderer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityRenderLayerOptions;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityRendererBuilder;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_parser.TextBlockProcessors;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.NullEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT,modid = FDBosses.MOD_ID)
public class BossClientModEvents {


    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        event.enqueueWork(()->{
            TextBlockProcessors.register(FDBosses.location("effect"),new MobEffectTextProcessor());
            TextBlockProcessors.register(FDBosses.location("config_float"),new BossConfigFloatValueProcessor());
        });
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event){
        event.registerBelow(VanillaGuiLayers.HOTBAR,FDBosses.location("electrified"),new ElectrifiedOverlay());
    }


    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event){
        event.registerSpecial(BossParticles.RUSH_PARTICLE.get(), new RushParticle.Factory());
        event.registerSpriteSet(BossParticles.BIS_SMOKE.get(), BigSmokeParticle.Factory::new);
        event.registerSpriteSet(BossParticles.SONIC_PARTICLE.get(), SonicParticle.Factory::new);
        event.registerSpecial(BossParticles.ARC_LIGHTNING.get(), new ArcLightningParticle.Factory());
        event.registerSpecial(BossParticles.CHESED_RAY_ATTACK.get(), new ChesedAttackRayParticle.Factory());
    }

    @SubscribeEvent
    public static void addRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(BossEntities.CHESED.get(),
                FDEntityRendererBuilder.builder()
                        .shouldRender(((entity, frustum, x, y, z) -> {
                            return true;
                        }))
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED)
                                .ignoreHurtOverlay(true)
                                .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")))
                                .build()
                        )
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_CRYSTAL_LAYER)
                                .ignoreHurtOverlay(true)
                                .renderType((entity,pticks)->{
                                    return RenderType.entityTranslucent(FDBosses.location("textures/entities/chesed_crystals.png"));
                                })
                                .color((entity,pticks)->{
                                    return new FDColor(1f,1f,1f,0f);
                                })
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_CRYSTAL_LAYER)
                                .ignoreHurtOverlay(true)
                                .renderType((entity,pticks)->{
                                    return RenderType.eyes(FDBosses.location("textures/entities/chesed_crystals.png"));
                                })
                                .color((entity,pticks)->{
                                    return new FDColor(1f,1f,1f,1f);
                                })
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_INFLATED)
                                .ignoreHurtOverlay(true)
                                .renderType((entity,pticks)->{
                                    return RenderType.entityTranslucentEmissive(FDBosses.location("textures/entities/chesed_white_tex.png"));
                                })
                                .color((entity,pticks)->{
                                    ChesedEntity e = (ChesedEntity) entity;
                                    float alpha = FDMathUtil.lerp(e.drainPercentOld,e.getMonolithDrainPercent(),pticks) * 0.3f;
                                    return new FDColor(0.1f,1f,1f,alpha);
                                })
                                .renderCondition((entity -> {
                                    return ((ChesedEntity)entity).getMonolithDrainPercent() != 0;
                                }))
                                .build()
                        )
                        .build()
        );
        event.registerEntityRenderer(BossEntities.CHESED_ELECTRIC_SPHERE.get(),FDEntityRendererBuilder.builder()
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_ELECTRIC_SPHERE)
                                .transformation((entity,matrices,pticks)->{
                                    float time = entity.tickCount + pticks;
                                    float md = 16;
                                    float scale = Mth.clamp(time / 20f,0,1) * ((float)Math.sin(time * 2) / md + (1 - 1 / md));
                                    matrices.scale(scale,scale,scale);
                                })
                                .renderType(RenderType.entityTranslucentCull(FDBosses.location("textures/entities/electric_orb.png")))
                                .build())
                .build());
        event.registerEntityRenderer(BossEntities.CHESED_CRYSTAL.get(),FDEntityRendererBuilder.builder()
                        .shouldRender(((entity, frustum, x, y, z) -> true))
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_CRYSTAL)
                                .renderCondition((entity -> true))
                                .transformation(((entity, stack, partialTicks) -> {
                                    FDRenderUtil.applyMovementMatrixRotations(stack,((ChesedCrystalEntity)entity).getCrystalFacingDirection());
                                    stack.mulPose(FDRenderUtil.rotationDegrees(FDRenderUtil.YP(),entity.getId() * 42.343f));
                                    stack.scale(3.2f,3,3.2f);
                                }))
                                .renderType(RenderType.eyes(FDBosses.location("textures/entities/chesed_crystal.png")))
                                .build())
                .build());
        event.registerEntityRenderer(BossEntities.CHESED_MONOLITH.get(),FDEntityRendererBuilder.builder()
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_MONOLITH)
                                .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/chesed_monolith.png")))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_MONOLITH)
                                .renderType(RenderType.eyes(FDBosses.location("textures/entities/chesed_monolith_emissive.png")))
                                .build())
                .build());



        event.registerEntityRenderer(BossEntities.CHESED_KINETIC_FIELD.get(), FDEntityRendererBuilder.builder()
                .addLayer(FDEntityRenderLayerOptions.builder()
                        .model(BossModels.CHESED_KINETIC_FIELD)
                        .renderType(RenderType.eyes(FDBosses.location("textures/entities/kinetic_field_spear.png")))
                        .build())
                        .shouldRender(((entity, frustum, v, v1, v2) -> {
                            return true;
                        }))
                        .freeRender(ChesedKineticFieldRenderer::render)
                .build());

        event.registerEntityRenderer(BossEntities.CHESED_BOSS_SPAWNER.get(), FDEntityRendererBuilder.builder()
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.BOSS_SPAWNER)
                                .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/boss_spawner.png")))
                                .renderCondition((entity -> {
                                    return ((BossSpawnerEntity)entity).isActive();
                                }))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.BOSS_SPAWNER)
                                .renderType(RenderType.entityTranslucentCull(FDBosses.location("textures/entities/boss_spawner_crystals.png")))
                                .renderCondition((entity -> {
                                    return ((BossSpawnerEntity)entity).isActive();
                                }))
                                .color(((entity, v) -> {
                                    return new FDColor(1,1,1f,0.25f);
                                }))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.BOSS_SPAWNER_CRYSTAL_LAYER)
                                .renderType(RenderType.eyes(FDBosses.location("textures/entities/boss_spawner_crystals.png")))
                                .renderCondition((entity -> {
                                    return ((BossSpawnerEntity)entity).isActive();
                                }))
                                .build())
                .build());

        event.registerEntityRenderer(BossEntities.CHESED_RAY_REFLECTOR.get(), FDEntityRendererBuilder.builder()
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_RAY_REFLECTOR)
                                .renderType((e,p)->{
                                    return RenderType.entityCutout(FDBosses.location("textures/entities/chesed_ray_reflector.png"));
                                })
                                .build())
                .build());

        event.registerEntityRenderer(BossEntities.EARTH_SHATTER.get(), EarthShatterRenderer::new);
        event.registerEntityRenderer(BossEntities.BLOCK_PROJECTILE.get(), BlockProjectileRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_FALLING_BLOCK.get(), ChesedFallingBlockRenderer::new);
        event.registerEntityRenderer(BossEntities.FLYING_BLOCK.get(), FlyingBlockEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.RADIAL_EARTHQUAKE.get(), NullEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_VERTICAL_RAY_ATTACK.get(), ChesedVerticalRayAttackRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_ONE_SHOT_VERTICAL_RAY_ATTACK.get(), NullEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_FIRE_TRAIL.get(), NullEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.FLYING_SWORD.get(), FlyingSwordRenderer::new);
    }
}
