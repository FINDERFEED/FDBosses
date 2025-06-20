package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.boss_screen.text_block_processors.BossConfigFloatValueProcessor;
import com.finderfeed.fdbosses.client.boss_screen.text_block_processors.MobEffectTextProcessor;
import com.finderfeed.fdbosses.client.overlay.ElectrifiedOverlay;
import com.finderfeed.fdbosses.client.particles.FlameWithStoneParticle;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedAttackRayParticle;
import com.finderfeed.fdbosses.client.particles.malkuth_slash.MalkuthHorizontalSlashParticle;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticle;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticle;
import com.finderfeed.fdbosses.client.particles.sonic_particle.SonicParticle;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_crystal.ChesedCrystalEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_one_shot_vertical_ray.ChesedOneShotVerticalRayRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_vertical_ray.ChesedVerticalRayAttackRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlockRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.flying_block_entity.FlyingBlockEntityRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.kinetic_field.ChesedKineticFieldRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector.ChesedRayReflector;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector.RayReflectorRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_sword_buff.FlyingSwordRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashRenderer;
import com.finderfeed.fdbosses.content.tile_entities.ChesedTrophyTileEntity;
import com.finderfeed.fdbosses.content.tile_entities.TrophyBlockEntity;
import com.finderfeed.fdbosses.ik_2d.InverseKinematics2BoneTransform;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdbosses.content.projectiles.renderers.BlockProjectileRenderer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadBoneTransformation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityRenderLayerOptions;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityRendererBuilder;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDModelItemRenderer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDModelItemRendererOptions;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.renderer.FDBlockEntityRendererBuilder;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.renderer.FDBlockEntityTransformation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.renderer.FDBlockRenderLayerOptions;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_parser.TextBlockProcessors;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.NullEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Vector3f;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT,modid = FDBosses.MOD_ID)
public class BossClientModEvents {


    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event){
        event.registerItem(FDModelItemRenderer.createExtensions(FDModelItemRendererOptions.create()
                .addModel(BossModels.CHESED,RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")))
                .addModel(BossModels.CHESED_CRYSTAL_LAYER,RenderType.eyes(FDBosses.location("textures/entities/chesed_crystals.png")))
                .setScale((ctx)->{
                    if (ctx == ItemDisplayContext.GROUND){
                        return 0.15f;
                    }
                    return 0.25f;
                })
                .addRotation((itemDisplayContext -> {
                    if (itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                        return 180f;
                    }else if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
                        return 40f;
                    }else if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND){
                        return -40f;
                    }
                    return 0f;
                }))
                .addTranslation((ctx)->{
                    if (ctx == ItemDisplayContext.GUI){
                        return new Vector3f(0.1f,-0.1f,0);
                    }else if (ctx == ItemDisplayContext.GROUND){
                        return new Vector3f();
                    }
                    return new Vector3f(0.1f,0f,0f);
                })
        ), BossItems.CHESED_TROPHY.get());
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        event.enqueueWork(()->{
            TextBlockProcessors.register(FDBosses.location("effect"),new MobEffectTextProcessor());
            TextBlockProcessors.register(FDBosses.location("config_float"),new BossConfigFloatValueProcessor());







            FDBlockEntityTransformation<TrophyBlockEntity> baseTransform = (trophy,matrices,pticks)->{
                matrices.mulPose(Axis.YP.rotationDegrees(trophy.getAngleFromState()));
            };

            FDBlockEntityTransformation<ChesedTrophyTileEntity> chesedTransform = (trophy,matrices,pticks)->{
                matrices.scale(0.3f,0.3f,0.3f);
                baseTransform.apply(trophy,matrices,pticks);
            };

            BlockEntityRenderers.register((BlockEntityType<ChesedTrophyTileEntity>)BossTileEntities.CHESED_TROPHY.get(),
                    FDBlockEntityRendererBuilder.<ChesedTrophyTileEntity>builder()
                    .addLayer(FDBlockRenderLayerOptions.<ChesedTrophyTileEntity>builder()
                            .model(BossModels.CHESED)
                            .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")))
                            .transformation(chesedTransform)
                            .build()
                    )
                    .addLayer(FDBlockRenderLayerOptions.<ChesedTrophyTileEntity>builder()
                            .model(BossModels.CHESED_CRYSTAL_LAYER)
                            .renderType((entity,pticks)->{
                                return RenderType.eyes(FDBosses.location("textures/entities/chesed_crystals.png"));
                            })
                            .color((entity,pticks)->{
                                return new FDColor(1f,1f,1f,1f);
                            })
                            .transformation(chesedTransform)
                            .build())
                    .build());
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
        event.registerSpriteSet(BossParticles.FLAME_WITH_STONE.get(), FlameWithStoneParticle.Factory::new);
        event.registerSpecial(BossParticles.ARC_LIGHTNING.get(), new ArcLightningParticle.Factory());
        event.registerSpecial(BossParticles.CHESED_RAY_ATTACK.get(), new ChesedAttackRayParticle.Factory());
        event.registerSpecial(BossParticles.MALKUTH_HORIZONTAL_SLASH.get(), new MalkuthHorizontalSlashParticle.Factory());
    }

    @SubscribeEvent
    public static void addRenderers(EntityRenderersEvent.RegisterRenderers event){

        var rightLegIK = new InverseKinematics2BoneTransform<MalkuthEntity>(
                Direction.Axis.Z,
                Direction.Axis.X,
                "leg_right_control_start",
                "leg_right_control_end",
                "leg_right",
                "leg_right_lower",
                "leg_right_boot",
                false
        );
        var leftLegIK = new InverseKinematics2BoneTransform<MalkuthEntity>(
                Direction.Axis.Z,
                Direction.Axis.X,
                "leg_left_control_start",
                "leg_left_control_end",
                "leg_left",
                "leg_left_lower",
                "leg_left_boot",
                false
        );
        event.registerEntityRenderer(BossEntities.MALKUTH.get(), FDEntityRendererBuilder.<MalkuthEntity>builder()
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthEntity>builder()
                                .model(BossModels.MALKUTH)
                                .renderType(RenderType.entityCutoutNoCull(FDBosses.location("textures/entities/malkuth/malkuth_solid.png")))
                                .addBoneController("head", new HeadBoneTransformation<>())
                                .addBoneController("leg_right_holder", rightLegIK)
                                .addBoneController("leg_left_holder", leftLegIK)
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthEntity>builder()
                                .model(BossModels.MALKUTH)
                                .renderType(RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_emissive.png")))
                                .addBoneController("head", new HeadBoneTransformation<>())
                                .addBoneController("leg_right_holder", rightLegIK)
                                .addBoneController("leg_left_holder", leftLegIK)
                                .light(LightTexture.FULL_BRIGHT)
                                .build())
                .build());

        event.registerEntityRenderer(BossEntities.MALKUTH_CRUSH.get(), FDEntityRendererBuilder.<MalkuthCrushAttack>builder()
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthCrushAttack>builder()
                                .light(LightTexture.FULL_BRIGHT)
                                .model(BossModels.MALKUTH_CRUSH_ATTACK)
                                .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_crash.png")))
                                .build())
                .build());

        event.registerEntityRenderer(BossEntities.CHESED.get(),
                FDEntityRendererBuilder.<ChesedEntity>builder()
                        .shouldRender(((entity, frustum, x, y, z) -> {
                            return true;
                        }))
                        .addLayer(FDEntityRenderLayerOptions.<ChesedEntity>builder()
                                .model(BossModels.CHESED)
                                .ignoreHurtOverlay(true)
                                .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")))
                                .build()
                        )
                        .addLayer(FDEntityRenderLayerOptions.<ChesedEntity>builder()
                                .model(BossModels.CHESED_CRYSTAL_LAYER)
                                .ignoreHurtOverlay(true)
                                .renderType((entity,pticks)->{
                                    return RenderType.entityTranslucent(FDBosses.location("textures/entities/chesed_crystals.png"));
                                })
                                .color((entity,pticks)->{
                                    return new FDColor(1f,1f,1f,0f);
                                })
                                .renderCondition((chesedEntity -> {
                                    return true;
                                }))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<ChesedEntity>builder()
                                .model(BossModels.CHESED_CRYSTAL_LAYER)
                                .ignoreHurtOverlay(true)
                                .renderType((entity,pticks)->{
                                    return RenderType.eyes(FDBosses.location("textures/entities/chesed_crystals.png"));
                                })
                                .color((entity,pticks)->{
                                    return new FDColor(1f,1f,1f,1f);
                                })
                                .renderCondition((chesedEntity -> {
                                    return true;
                                }))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<ChesedEntity>builder()
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
                        .freeRender(new ChesedRenderer())
                        .build()
        );
        event.registerEntityRenderer(BossEntities.CHESED_ELECTRIC_SPHERE.get(),FDEntityRendererBuilder.builder()
                        .addLayer(FDEntityRenderLayerOptions.builder()
                                .model(BossModels.CHESED_ELECTRIC_SPHERE)
                                .transformation((entity,matrices,pticks)->{
                                    matrices.translate(0,0.5f,0);
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


        event.registerEntityRenderer(BossEntities.CHESED_RAY_REFLECTOR.get(), FDEntityRendererBuilder.<ChesedRayReflector>builder()
                        .addLayer(FDEntityRenderLayerOptions.<ChesedRayReflector>builder()
                                .transformation(((reflector, poseStack, v) -> poseStack.scale(0.8f,0.8f,0.8f)))
                                .model(BossModels.CHESED_RAY_REFLECTOR)
                                .renderType((e,p)->{
                                    return RenderType.entityCutout(FDBosses.location("textures/entities/chesed_ray_reflector.png"));
                                })
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<ChesedRayReflector>builder()
                                .model(BossModels.CHESED_RAY_REFLECTOR)
                                .transformation(((reflector, poseStack, v) -> poseStack.scale(0.8f,0.8f,0.8f)))
                                .renderType((e,p)->{
                                    return RenderType.entityTranslucent(FDBosses.location("textures/entities/chesed_ray_reflector_crystals.png"));
                                })
                                .color((entity,pticks)->{
                                    return new FDColor(1f,1f,1f,0f);
                                })
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<ChesedRayReflector>builder()
                                .model(BossModels.CHESED_RAY_REFLECTOR)
                                .transformation(((reflector, poseStack, v) -> poseStack.scale(0.8f,0.8f,0.8f)))
                                .renderType((e,p)->{
                                    return RenderType.eyes(FDBosses.location("textures/entities/chesed_ray_reflector_crystals.png"));
                                })
                                .color((entity,pticks)->{
                                    float animTime = BossAnims.RAY_REFLECTOR_ACTIVATE.get().getAnimTime();

                                    float t = FDMathUtil.lerp(entity.activeTickerO,entity.getActiveTicker(),pticks);

                                    float p = t / animTime;


                                    float r = FDMathUtil.lerp(0.5f,0.3f,p);
                                    float g = FDMathUtil.lerp(0.5f,0.95f,p);
                                    float b = FDMathUtil.lerp(0.5f,1f,p);

                                    if (entity.isActivating()) {
                                        float offsetTime = t - animTime / 2;
                                        float t2 = Math.clamp(offsetTime / 10, 0, 1);

                                        float p2;

                                        if (t2 <= 0.25){
                                            p2 = 1 - (float) Math.pow(4 * t2 - 1,2);
                                        }else{
                                            p2 = 1 - (float) Math.pow(1.335 * (t2 - 0.25),2);
                                        }

                                        r = FDMathUtil.lerp(r,1,p2);
                                        g = FDMathUtil.lerp(g,1,p2);
                                        b = FDMathUtil.lerp(b,1,p2);

                                    }

                                    return new FDColor(r,g,b,1f);
                                })
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<ChesedRayReflector>builder()
                                .model(BossModels.CHESED_RAY_REFLECTOR)
                                .renderCondition((entity)->{
                                    int ticksToReveal = 3;

                                    float animTime = BossAnims.RAY_REFLECTOR_ACTIVATE.get().getAnimTime();
                                    return entity.getActiveTicker() > animTime - ticksToReveal;
                                })
                                .transformation(((reflector, poseStack, v) -> poseStack.scale(0.8f,0.8f,0.8f)))
                                .renderType((e,p)->{
                                    return RenderType.entityTranslucent(FDBosses.location("textures/entities/chesed_ray_reflector_crosshair.png"));
                                })
                                .color((entity,pticks)->{

                                    int ticksToReveal = 3;

                                    float animTime = BossAnims.RAY_REFLECTOR_ACTIVATE.get().getAnimTime();
                                    float t = Mth.clamp(entity.getActiveTicker() + pticks - (animTime - ticksToReveal), 0, ticksToReveal);


                                    return new FDColor(0.3f,0.95f,1f,t / ticksToReveal);
                                })
                                .build())
                        .freeRender(new RayReflectorRenderer())
                .build());

        event.registerEntityRenderer(BossEntities.EARTH_SHATTER.get(), EarthShatterRenderer::new);
        event.registerEntityRenderer(BossEntities.BLOCK_PROJECTILE.get(), BlockProjectileRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_FALLING_BLOCK.get(), ChesedFallingBlockRenderer::new);
        event.registerEntityRenderer(BossEntities.FLYING_BLOCK.get(), FlyingBlockEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.RADIAL_EARTHQUAKE.get(), NullEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.EYE_OF_CHESED.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_VERTICAL_RAY_ATTACK.get(), ChesedVerticalRayAttackRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_ONE_SHOT_VERTICAL_RAY_ATTACK.get(), ChesedOneShotVerticalRayRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_FIRE_TRAIL.get(), NullEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.FLYING_SWORD.get(), FlyingSwordRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_SLASH.get(), MalkuthSlashRenderer::new);
    }
}
