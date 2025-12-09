package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.boss_screen.text_block_processors.BossConfigFloatValueProcessor;
import com.finderfeed.fdbosses.client.boss_screen.text_block_processors.MobEffectTextProcessor;
import com.finderfeed.fdbosses.client.overlay.ElectrifiedOverlay;
import com.finderfeed.fdbosses.client.overlay.GeburahSinsOverlay;
import com.finderfeed.fdbosses.client.overlay.MalkuthWeaknessOverlay;
import com.finderfeed.fdbosses.client.particles.FlameWithStoneParticle;
import com.finderfeed.fdbosses.client.particles.IceChunkParticle;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticle;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedAttackRayParticle;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticle;
import com.finderfeed.fdbosses.client.particles.malkuth_slash.MalkuthHorizontalSlashParticle;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticle;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticle;
import com.finderfeed.fdbosses.client.particles.sonic_particle.SonicParticle;
import com.finderfeed.fdbosses.client.particles.square_preparation_particle.RectanglePreparationParticle;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticle;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_crystal.ChesedCrystalEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_mini_ray.ChesedMiniRayRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_one_shot_vertical_ray.ChesedOneShotVerticalRayRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_vertical_ray.ChesedVerticalRayAttackRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlockRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.flying_block_entity.FlyingBlockEntityRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.kinetic_field.ChesedKineticFieldRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector.ChesedRayReflector;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector.RayReflectorRenderer;
import com.finderfeed.fdbosses.content.entities.chesed_sword_buff.FlyingSwordRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahBossSpawner;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.casts.GeburahCastingCircleRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.chain_trap.ChainTrapSummonProjectile;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_bell.GeburahBell;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_bell.GeburahBellRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_earthquake.GeburahEarthquakeRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_explosive_crystal.GeburahSinCrystal;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_opening_floor.GeburahOpeningFloorRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile.JudgementBallExplosionParticle;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile.JudgementBallProjectile;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.content.entities.geburah.justice_hammer.JusticeHammerAttack;
import com.finderfeed.fdbosses.content.entities.geburah.justice_hammer.JusticeHammerAttackRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayStrikeDecalParticle;
import com.finderfeed.fdbosses.content.entities.geburah.respawn_point_setter_block.GeburahRespiteBlockEntityRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahRotatingWeaponsBoneController;
import com.finderfeed.fdbosses.content.entities.geburah.chain_trap.GeburahChainTrapRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayParticle;
import com.finderfeed.fdbosses.content.entities.geburah.scales_controller.GeburahScalesBoneController;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner.MalkuthBossSpawner;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boulder.MalkuthBoulderRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonProjectileRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain.MalkuthChainRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquakeRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball.MalkuthFireballRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_floor.MalkuthFloorRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword.MalkuthGiantSwordSlashRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform.MalkuthPlatform;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal.MalkuthRepairCrystal;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal.MalkuthRepairCrystalRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal.MalkuthRepairEntityRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashRenderer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_warrior.MalkuthWarriorEntity;
import com.finderfeed.fdbosses.content.projectiles.renderers.MalkuthPlayerFireIceBallRenderer;
import com.finderfeed.fdbosses.content.tile_entities.ChesedTrophyTileEntity;
import com.finderfeed.fdbosses.content.tile_entities.MalkuthTrophyBlockEntity;
import com.finderfeed.fdbosses.content.tile_entities.TrophyBlockEntity;
import com.finderfeed.fdbosses.ik_2d.InverseKinematics2BoneTransform;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdbosses.content.projectiles.renderers.BlockProjectileRenderer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadBoneTransformation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityRenderLayerOptions;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityRendererBuilder;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityTransformation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDModelItemRenderer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDModelItemRendererOptions;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.renderer.FDBlockEntityRendererBuilder;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.renderer.FDBlockEntityTransformation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.renderer.FDBlockRenderLayerOptions;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_parser.TextBlockProcessors;
import com.finderfeed.fdlib.systems.trails.FDTrailRenderer;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.NullEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Random;
import org.joml.Vector3f;

@EventBusSubscriber(value = Dist.CLIENT,modid = FDBosses.MOD_ID)
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

        event.registerItem(FDModelItemRenderer.createExtensions(FDModelItemRendererOptions.create()
                .addModel(BossModels.MALKUTH_SCREEN,RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_screen.png")))
                .addModel(BossModels.MALKUTH_SCREEN,RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth_screen_emissive.png")))
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
                        return new Vector3f(-0.2f,-0.1f,0);
                    }else if (ctx == ItemDisplayContext.GROUND){
                        return new Vector3f();
                    }
                    return new Vector3f(0.1f,0f,0f);
                })
        ), BossItems.MALKUTH_TROPHY.get());
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        event.enqueueWork(()->{
            TextBlockProcessors.register(FDBosses.location("effect"),new MobEffectTextProcessor());
            TextBlockProcessors.register(FDBosses.location("config_float"),new BossConfigFloatValueProcessor());


            ItemProperties.register(BossItems.GEBURAH_EXPLOSIVE_CRYSTAL.get(), FDBosses.location("exploding"), (stack, level, entity, id)->{
                int k = (stack.getDamageValue() / 10) % 2;
                return k;
            });



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

            FDBlockEntityTransformation<MalkuthTrophyBlockEntity> malkuthTransform = (trophy, matrices, pticks)->{
                matrices.scale(0.3f,0.3f,0.3f);
                baseTransform.apply(trophy,matrices,pticks);
            };


            BlockEntityRenderers.register((BlockEntityType<MalkuthTrophyBlockEntity>)BossTileEntities.MALKUTH_TROPHY.get(),
                    FDBlockEntityRendererBuilder.<MalkuthTrophyBlockEntity>builder()
                    .addLayer(FDBlockRenderLayerOptions.<MalkuthTrophyBlockEntity>builder()
                            .model(BossModels.MALKUTH_SCREEN)
                            .renderType(RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_screen.png")))
                            .transformation(malkuthTransform)
                            .build()
                    )
                    .addLayer(FDBlockRenderLayerOptions.<MalkuthTrophyBlockEntity>builder()
                            .model(BossModels.MALKUTH_SCREEN)
                            .light(LightTexture.FULL_BRIGHT)
                            .renderType((entity,pticks)->{
                                return RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth_screen_emissive.png"));
                            })
                            .transformation(malkuthTransform)
                            .build())
                    .build());
        });
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event){
        event.registerBelow(VanillaGuiLayers.HOTBAR,FDBosses.location("electrified"),new ElectrifiedOverlay());
        event.registerBelow(VanillaGuiLayers.HOTBAR,FDBosses.location("malkuth_weakness"),new MalkuthWeaknessOverlay());
        event.registerBelow(VanillaGuiLayers.HOTBAR,FDBosses.location("geburah_sin_overlay"),new GeburahSinsOverlay());
    }


    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event){
        event.registerSpecial(BossParticles.RUSH_PARTICLE.get(), new RushParticle.Factory());
        event.registerSpriteSet(BossParticles.BIS_SMOKE.get(), BigSmokeParticle.Factory::new);
        event.registerSpriteSet(BossParticles.SONIC_PARTICLE.get(), SonicParticle.Factory::new);
        event.registerSpriteSet(BossParticles.FLAME_WITH_STONE.get(), FlameWithStoneParticle.Factory::new);
        event.registerSpriteSet(BossParticles.ICE_CHUNK.get(), IceChunkParticle.Factory::new);
        event.registerSpecial(BossParticles.ARC_LIGHTNING.get(), new ArcLightningParticle.Factory());
        event.registerSpecial(BossParticles.CHESED_RAY_ATTACK.get(), new ChesedAttackRayParticle.Factory());
        event.registerSpecial(BossParticles.MALKUTH_HORIZONTAL_SLASH.get(), new MalkuthHorizontalSlashParticle.Factory());
        event.registerSpecial(BossParticles.ARC_ATTACK_PREPARATION_PARTICLE.get(), new ArcAttackPreparationParticle.Factory());
        event.registerSpecial(BossParticles.RECTANGLE_PREPARATION_PARTICLE.get(), new RectanglePreparationParticle.Factory());
        event.registerSpecial(BossParticles.STRIPE_PARTICLE.get(), new StripeParticle.Factory());
        event.registerSpecial(BossParticles.GEBURAH_RAY_ATTACK.get(), new GeburahRayParticle.Factory());
        event.registerSpecial(BossParticles.JUDGEMENT_BALL_EXPLOSION.get(), new JudgementBallExplosionParticle.Factory());
        event.registerSpecial(BossParticles.GEBURAH_RAY_DECAL.get(), new GeburahRayStrikeDecalParticle.Factory());
        event.registerSpecial(BossParticles.COLORED_JUMPING_PARTICLE.get(), new ColoredJumpingParticle.Factory());
    }

    @SubscribeEvent
    public static void addRenderers(EntityRenderersEvent.RegisterRenderers event){

        event.registerBlockEntityRenderer(BossTileEntities.GEBURAH_RESPITE_BLOCK.get(), GeburahRespiteBlockEntityRenderer::new);

        event.registerEntityRenderer(BossEntities.GEBURAH_OPENING_FLOOR.get(), GeburahOpeningFloorRenderer::new);
        event.registerEntityRenderer(BossEntities.GEBURAH_CASTING_CIRCLE_SIN_CRYSTAL.get(), GeburahCastingCircleRenderer::new);
        event.registerEntityRenderer(BossEntities.GEBURAH_CASTING_CIRCLE_CHAIN_TRAP.get(), GeburahCastingCircleRenderer::new);
        event.registerEntityRenderer(BossEntities.GEBURAH_CASTING_CIRCLE_RAY.get(), GeburahCastingCircleRenderer::new);
        event.registerEntityRenderer(BossEntities.GEBURAH_CASTING_CIRCLE_JUDGEMENT_BIRD.get(), GeburahCastingCircleRenderer::new);

        event.registerEntityRenderer(BossEntities.GEBURAH_BOSS_SPAWNER.get(), FDEntityRendererBuilder.builder()
                .addLayer(FDEntityRenderLayerOptions.builder()
                        .model(BossModels.JUSTITIA)
                        .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/geburah/justitia.png")))
                        .renderCondition((entity -> {
                            return ((BossSpawnerEntity)entity).isActive();
                        }))
                        .build())
                .addLayer(FDEntityRenderLayerOptions.builder()
                        .model(BossModels.JUSTITIA)
                        .renderType(RenderType.text(FDBosses.location("textures/entities/geburah/justitia_max_light.png")))
                        .light(LightTexture.FULL_BRIGHT)
                        .renderCondition((entity -> {
                            return ((BossSpawnerEntity)entity).isActive();
                        }))
                        .build())
                .build());

        event.registerEntityRenderer(BossEntities.GEBURAH_BELL.get(), FDEntityRendererBuilder.<GeburahBell>builder()
                        .addLayer(FDEntityRenderLayerOptions.<GeburahBell>builder()
                                .ignoreHurtOverlay(true)
                                .model(BossModels.GEBURAH_BELL)
                                .renderType(((geburahBell, v) -> {
                                    return RenderType.entityTranslucentCull(FDBosses.location("textures/entities/geburah/geburah_bell.png"));
                                }))
                                .color(((geburahBell, v) -> {

                                    int appearDuration = 20;

                                    float alpha;

                                    if (!geburahBell.isDeadOrDying()) {
                                        alpha = FDEasings.easeOut(Mth.clamp((geburahBell.tickCount + v) / appearDuration, 0, 1));
                                    }else{
                                        alpha = 1 - FDEasings.easeOut(Mth.clamp((geburahBell.deathTime + v) / BossAnims.GEBURAH_BELL_RING.get().getAnimTime(), 0, 1));
                                    }

                                    if (geburahBell.isRed()){
                                        return new FDColor(1f,0.3f,0.1f,alpha * 0.75f);
                                    }else{
                                        return new FDColor(0.3f,0.8f,1f,alpha * 0.75f);
                                    }
                                }))
                                .transformation(((geburahBell, matrices, pticks) -> {
                                    int appearDuration = 20;
                                    float t = Mth.clamp((geburahBell.tickCount + pticks) / appearDuration,0,1);

                                    Random random = new Random(geburahBell.getId() * 34L);
                                    float rndFly = (float) Math.sin(random.nextFloat() * FDMathUtil.FPI * 2 + (geburahBell.tickCount + pticks) / 10f) * 0.1f;

                                    matrices.translate(0,(1-FDEasings.easeOut(t)) * 0.25 + rndFly,0);
                                }))
                                .build())

                        .freeRender(new GeburahBellRenderer())

                .build());

        event.registerEntityRenderer(BossEntities.JUDGEMENT_BIRD.get(), FDEntityRendererBuilder.<JudgementBirdEntity>builder()
                        .addLayer(FDEntityRenderLayerOptions.<JudgementBirdEntity>builder()
                                .model(BossModels.JUDGEMENT_BIRD)
                                .renderType(RenderType.entityTranslucent(FDBosses.location("textures/entities/geburah/judgement_bird.png")))
                                .transformation(((judgementBirdEntity, poseStack, v) -> {
                                    poseStack.translate(0,judgementBirdEntity.getBbHeight() / 2,0);
                                }))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<JudgementBirdEntity>builder()
                                .model(BossModels.JUDGEMENT_BIRD_LAYER)
                                .light(LightTexture.FULL_BRIGHT)
                                .renderType(RenderType.eyes(FDBosses.location("textures/entities/geburah/judgement_bird_emissive.png")))
                                .transformation(((judgementBirdEntity, poseStack, v) -> {
                                    poseStack.translate(0,judgementBirdEntity.getBbHeight() / 2,0);
                                }))
                                .build())

                .build());

        event.registerEntityRenderer(BossEntities.GEBURAH_CHAIN_TRAP_SUMMON_PROJECTILE.get(), FDEntityRendererBuilder.<ChainTrapSummonProjectile>builder()
                .addLayer(FDEntityRenderLayerOptions.<ChainTrapSummonProjectile>builder()
                        .renderType(RenderType.text(FDBosses.location("textures/entities/geburah/crystal_of_sin_entity.png")))
                        .model(BossModels.JUDGEMENT_BALL)
                        .light(LightTexture.FULL_BRIGHT)
                        .transformation(((judgementBallProjectile, poseStack, v) -> {
                            poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);

                            float time = (judgementBallProjectile.tickCount + v) * 4f + judgementBallProjectile.getId();
                            poseStack.mulPose(Axis.YP.rotationDegrees(time));
                            poseStack.mulPose(Axis.ZP.rotationDegrees(time));

                            poseStack.scale(0.5f,0.5f,0.5f);
                        }))
                        .build())

                .freeRender(((judgementBallProjectile, v, v1, poseStack, multiBufferSource, i) -> {
                    poseStack.pushPose();
                    poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);
                    FDTrailRenderer.renderTrail(judgementBallProjectile, judgementBallProjectile.trail,
                            multiBufferSource.getBuffer(RenderType.lightning()),
                            poseStack,0.1f,4,10,v1,
                            new FDColor(0.5f,0.7f,0.9f,0f),
                            new FDColor(0.5f,0.7f,0.9f,0.7f)
                    );
                    poseStack.popPose();
                }))
                .shouldRender(((explosiveCrystal, frustum, v, v1, v2) -> {
                    return frustum.isVisible(explosiveCrystal.getBoundingBox().inflate(10));
                }))
                .build());









        event.registerEntityRenderer(BossEntities.GEBURAH_SIN_CRYSTAL.get(), FDEntityRendererBuilder.<GeburahSinCrystal>builder()
                .addLayer(FDEntityRenderLayerOptions.<GeburahSinCrystal>builder()
                        .renderType(RenderType.text(FDBosses.location("textures/entities/geburah/crystal_of_sin_entity.png")))
                        .model(BossModels.JUDGEMENT_BALL)
                        .light(LightTexture.FULL_BRIGHT)
                        .transformation(((judgementBallProjectile, poseStack, v) -> {
                            poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);

                            float time = (judgementBallProjectile.tickCount + v) * 4f + judgementBallProjectile.getId();
                            poseStack.mulPose(Axis.YP.rotationDegrees(time));
                            poseStack.mulPose(Axis.ZP.rotationDegrees(time));

                            poseStack.scale(0.5f,0.5f,0.5f);
                        }))
                        .build())
                .addLayer(FDEntityRenderLayerOptions.<GeburahSinCrystal>builder()
                        .renderType(RenderType.text(FDBosses.location("textures/entities/geburah/crystal_of_sin_entity_layer.png")))
                        .model(BossModels.JUDGEMENT_BALL_LAYER)
                        .light(LightTexture.FULL_BRIGHT)
                        .transformation(((judgementBallProjectile, poseStack, v) -> {
                            poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);

                            float time = (judgementBallProjectile.tickCount + v) * 4f + judgementBallProjectile.getId();
                            poseStack.mulPose(Axis.YP.rotationDegrees(time));
                            poseStack.mulPose(Axis.ZP.rotationDegrees(time));

                            poseStack.scale(0.5f,0.5f,0.5f);
                        }))
                        .build())

                .freeRender(((judgementBallProjectile, v, v1, poseStack, multiBufferSource, i) -> {
                    poseStack.pushPose();
                    poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);
                    FDTrailRenderer.renderTrail(judgementBallProjectile, judgementBallProjectile.trail,
                            multiBufferSource.getBuffer(RenderType.lightning()),
                            poseStack,0.1f,4,10,v1,
                            new FDColor(0.5f,0.7f,0.9f,0f),
                            new FDColor(0.5f,0.7f,0.9f,0.7f)
                    );
                    poseStack.popPose();
                }))
                .shouldRender(((explosiveCrystal, frustum, v, v1, v2) -> {
                    return frustum.isVisible(explosiveCrystal.getBoundingBox().inflate(10));
                }))
                .build());


        event.registerEntityRenderer(BossEntities.JUSTICE_HAMMER.get(), FDEntityRendererBuilder.<JusticeHammerAttack>builder()
                        .addLayer(FDEntityRenderLayerOptions.<JusticeHammerAttack>builder()
                                .renderType(RenderType.lightning())
                                .model(BossModels.JUSTICE_HAMMER)
                                .transformation(((justiceHammerAttack, poseStack, v) -> {
                                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                                }))
                                .color(((justiceHammerAttack, v) -> {
                                    float alpha = JusticeHammerAttack.EASING.apply(justiceHammerAttack.tickCount + v);
                                    return new FDColor(0.3f,0.7f,1f,alpha * 0.75f);
                                }))
                                .light(LightTexture.FULL_BRIGHT)
                                .build())
                        .freeRender(new JusticeHammerAttackRenderer())
                        .shouldRender(((justiceHammerAttack, frustum, v, v1, v2) -> {
                            return frustum.isVisible(justiceHammerAttack.getBoundingBox().inflate(30));
                        }))
                .build());

        event.registerEntityRenderer(BossEntities.GEBURAH_JUDGEMENT_BALL.get(), FDEntityRendererBuilder.<JudgementBallProjectile>builder()
                        .addLayer(FDEntityRenderLayerOptions.<JudgementBallProjectile>builder()
                                .renderType(RenderType.text(FDBosses.location("textures/entities/geburah/judgement_ball.png")))
                                .model(BossModels.JUDGEMENT_BALL)
                                .light(LightTexture.FULL_BRIGHT)
                                .transformation(((judgementBallProjectile, poseStack, v) -> {
                                    poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);
                                    FDRenderUtil.applyMovementMatrixRotations(poseStack, judgementBallProjectile.getDeltaMovement());
                                }))
                                .build())

                        .addLayer(FDEntityRenderLayerOptions.<JudgementBallProjectile>builder()
                                .renderType(((judgementBallProjectile, v) -> {
                                    float tick = (judgementBallProjectile.tickCount + v) % 8;
                                    return RenderType.text(FDBosses.location("textures/entities/geburah/judgement_ball_" + (int)Math.floor(tick / 2) + ".png"));
                                }))
                                .model(BossModels.JUDGEMENT_BALL_LAYER)
                                .transformation(((judgementBallProjectile, poseStack, v) -> {
                                    poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);
                                    FDRenderUtil.applyMovementMatrixRotations(poseStack, judgementBallProjectile.getDeltaMovement());
                                }))
                                .light(LightTexture.FULL_BRIGHT)
                                .build())

                        .freeRender(((judgementBallProjectile, v, v1, poseStack, multiBufferSource, i) -> {
                            poseStack.pushPose();
                            poseStack.translate(0,judgementBallProjectile.getBbHeight()/2,0);
                            FDTrailRenderer.renderTrail(judgementBallProjectile, judgementBallProjectile.trail,
                                    multiBufferSource.getBuffer(RenderType.lightning()),
                                    poseStack,0.15f,4,10,v1,
                                    new FDColor(0.5f,0.7f,0.9f,0f),
                                    new FDColor(0.5f,0.7f,0.9f,0.7f)
                            );
                            poseStack.popPose();
                        }))
                        .shouldRender(((judgementBallProjectile, frustum, v, v1, v2) -> {
                            return frustum.isVisible(judgementBallProjectile.getBoundingBox().inflate(10));
                        }))
                .build());

        event.registerEntityRenderer(BossEntities.GEBURAH_EARTHQUAKE.get(), GeburahEarthquakeRenderer::new);

        event.registerEntityRenderer(BossEntities.GEBURAH_CHAIN_TRAP.get(), GeburahChainTrapRenderer::new);

        event.registerEntityRenderer(BossEntities.GEBURAH.get(), FDEntityRendererBuilder.<GeburahEntity>builder()
                .addLayer(FDEntityRenderLayerOptions.<GeburahEntity>builder()
                        .model(BossModels.GEBURAH)
                        .ignoreHurtOverlay(true)
                        .renderType(((geburah, v) -> {
                            return RenderType.entityCutout(FDBosses.location("textures/entities/geburah/geburah.png"));
                        }))
                        .addBoneController("rotating_weapons", new GeburahRotatingWeaponsBoneController())
                        .addBoneController("scales_plates", new GeburahScalesBoneController())
                        .build())

                .freeRender(new GeburahRenderer())
                .shouldRender(((geburahEntity, frustum, v, v1, v2) -> true))
                .build());

        EntityRendererProvider<MalkuthWarriorEntity> warriorRenderer = FDEntityRendererBuilder.<MalkuthWarriorEntity>builder()
                .addLayer(FDEntityRenderLayerOptions.<MalkuthWarriorEntity>builder()
                        .model(BossModels.MALKUTH_WARRIOR)
                        .renderType(RenderType.entityCutoutNoCull(FDBosses.location("textures/entities/malkuth/malkuth_warrior.png")))
                        .addBoneController("head", new HeadBoneTransformation<>())
                        .build())
                .addLayer(FDEntityRenderLayerOptions.<MalkuthWarriorEntity>builder()
                        .model(BossModels.MALKUTH_WARRIOR)
                        .renderType((entity, pticks) -> {
                            if (entity.getEntityData().get(MalkuthWarriorEntity.WARRIOR_TYPE).isFire()) {
                                return RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_warrior_fire.png"));
                            }else{
                                return RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_warrior_ice.png"));
                            }
                        })
                        .light(LightTexture.FULL_BRIGHT)
                        .addBoneController("head", new HeadBoneTransformation<>())
                        .build())
                .build();

        event.registerEntityRenderer(BossEntities.FIRE_MALKUTH_WARRIOR.get(), warriorRenderer);
        event.registerEntityRenderer(BossEntities.ICE_MALKUTH_WARRIOR.get(), warriorRenderer);

        event.registerEntityRenderer(BossEntities.MALKUTH_REPAIR_CRYSTAL.get(), FDEntityRendererBuilder.<MalkuthRepairCrystal>builder()
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthRepairCrystal>builder()
                                .model(BossModels.MALKUTH_REPAIR_CRYSTAL)
                                .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_crystal_base.png")))
                                .ignoreHurtOverlay(true)
                                .build())

                        .addLayer(FDEntityRenderLayerOptions.<MalkuthRepairCrystal>builder()
                                .model(BossModels.MALKUTH_REPAIR_CRYSTAL)
                                .renderType(((malkuthRepairCrystal, v) -> {
                                    return malkuthRepairCrystal.getCrystalType().isFire() ?
                                            RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth_crystal_fire.png")) :
                                            RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth_crystal_ice.png"));
                                }))
                                .ignoreHurtOverlay(true)
                                .light(LightTexture.FULL_BRIGHT)
                                .color(((malkuthRepairCrystal, v) -> {
                                    return new FDColor(1,0.75f,0.75f,0.5f);
                                }))
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthRepairCrystal>builder()
                                .model(BossModels.MALKUTH_REPAIR_CRYSTAL)
                                .renderType(((malkuthRepairCrystal, v) -> {
                                    return malkuthRepairCrystal.getCrystalType().isFire() ?
                                            RenderType.eyes(FDBosses.location("textures/entities/malkuth/malkuth_crystal_fire.png")) :
                                            RenderType.eyes(FDBosses.location("textures/entities/malkuth/malkuth_crystal_ice.png"));
                                }))
                                .ignoreHurtOverlay(true)
                                .light(LightTexture.FULL_BRIGHT)
                                .color(((malkuthRepairCrystal, v) -> {
                                    return new FDColor(1,1,1,1);
                                }))
                                .build())
                .freeRender(new MalkuthRepairCrystalRenderer())
                .build());

        FDEntityTransformation<MalkuthPlatform> platformTransform = (platform, matrices, v) -> {

            float time = platform.tickCount + v;

            Random random = new Random(platform.getId() * 33L);
            int timeToRise = 20 + random.nextInt(10);
            float p = Math.clamp((platform.tickCount + v) / timeToRise,0, 1);

            float platformFloat = FDEasings.easeIn(p) * (float) Math.sin(time/25 + random.nextFloat() * FDMathUtil.FPI * 2) * 0.05f;

            float translation = -5 + 5 * FDEasings.easeOutBack(p) + platformFloat;

            float randomYRot = (platform.getId() % 4) * 90;

            matrices.translate(0,translation,0);
            matrices.mulPose(Axis.YP.rotationDegrees(randomYRot));

            int d = random.nextInt(2) == 1 ? -1 : 1;

            float someRot = -15 * d + FDEasings.easeOutBack(p) * 15 * d;
            matrices.mulPose(Axis.XN.rotationDegrees(someRot));
            matrices.mulPose(Axis.YN.rotationDegrees(someRot));

        };
        event.registerEntityRenderer(BossEntities.MALKUTH_PLATFORM.get(), FDEntityRendererBuilder.<MalkuthPlatform>builder()
                .addLayer(FDEntityRenderLayerOptions.<MalkuthPlatform>builder()
                        .model(BossModels.MALKUTH_PLATFORM)
                        .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_platform.png")))
                        .transformation(platformTransform)
                        .build())
                .addLayer(FDEntityRenderLayerOptions.<MalkuthPlatform>builder()
                        .model(BossModels.MALKUTH_PLATFORM)
                        .renderType(RenderType.eyes(FDBosses.location("textures/entities/malkuth/malkuth_platform_emissive.png")))
                        .transformation(platformTransform)
                        .build())
                        .shouldRender(((malkuthPlatform, frustum, v, v1, v2) -> {
                            return frustum.isVisible(new AABB(-5,-5,-5,5,5,5).move(malkuthPlatform.position()));
                        }))
                .build());

        event.registerEntityRenderer(BossEntities.MALKUTH_BOSS_SPAWNER.get(), FDEntityRendererBuilder.<MalkuthBossSpawner>builder()


                .addLayer(FDEntityRenderLayerOptions.<MalkuthBossSpawner>builder()
                        .model(BossModels.MALKUTH_SPAWNER)
                        .transformation(((malkuthBossSpawner, poseStack, v) -> poseStack.mulPose(Axis.YP.rotationDegrees(90))))
                                .renderType(RenderType.eyes(FDBosses.location("textures/entities/malkuth/malkuth_boss_spawner_emissive.png")))
                                .light(LightTexture.FULL_BRIGHT)
                                .build())


                .addLayer(FDEntityRenderLayerOptions.<MalkuthBossSpawner>builder()
                        .model(BossModels.MALKUTH_SPAWNER)
                        .transformation(((malkuthBossSpawner, poseStack, v) -> poseStack.mulPose(Axis.YP.rotationDegrees(90))))
                        .renderType(RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_boss_spawner_solid.png")))
                        .build())

                .shouldRender(((malkuthBossSpawner, frustum, v, v1, v2) -> {
                            return malkuthBossSpawner.isActive();
                }))
                .build());

        event.registerEntityRenderer(BossEntities.MALKUTH_CANNON.get(), FDEntityRendererBuilder.<MalkuthCannonEntity>builder()
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthCannonEntity>builder()
                                .renderType((entity,pticks)->{
                                    if (entity.getCannonType().isFire()) {
                                        return RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_cannon.png"));
                                    }else{
                                        return RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_cannon_ice.png"));
                                    }
                                })
                                .model(BossModels.MALKUTH_CANNON)
                                .ignoreHurtOverlay(true)
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthCannonEntity>builder()
                                .renderType((entity,pticks)->{
                                    return RenderType.eyes(FDBosses.location("textures/entities/malkuth/malkuth_cannon_emissive.png"));
                                })
                                .renderCondition((m)->m.getCannonType().isFire())
                                .model(BossModels.MALKUTH_CANNON)
                                .ignoreHurtOverlay(true)
                                .build())
                        .freeRender(new MalkuthCannonRenderer())
                .build());

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
                                .ignoreHurtOverlay(true)
                                .build())
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthEntity>builder()
                                .model(BossModels.MALKUTH)
                                .renderType(RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_emissive.png")))
                                .addBoneController("head", new HeadBoneTransformation<>())
                                .addBoneController("leg_right_holder", rightLegIK)
                                .addBoneController("leg_left_holder", leftLegIK)
                                .ignoreHurtOverlay(true)
                                .light(LightTexture.FULL_BRIGHT)
                                .build())
                .build());

        event.registerEntityRenderer(BossEntities.MALKUTH_CRUSH.get(), FDEntityRendererBuilder.<MalkuthCrushAttack>builder()
                        .addLayer(FDEntityRenderLayerOptions.<MalkuthCrushAttack>builder()
                                .transformation(((malkuthCrushAttack, poseStack, v) -> {
                                    Vec3i nrm = malkuthCrushAttack.getEntityData().get(MalkuthCrushAttack.DIRECTION).getNormal();
                                    FDRenderUtil.applyMovementMatrixRotations(poseStack, new Vec3(nrm.getX(),nrm.getY(), nrm.getZ()));
                                }))
                                .light(LightTexture.FULL_BRIGHT)
                                .model(BossModels.MALKUTH_CRUSH_ATTACK)
                                .renderType(((malkuthCrushAttack, v) -> {
                                    if (malkuthCrushAttack.isBothFireAndIce()){
                                        return RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_crash.png"));
                                    }else if (malkuthCrushAttack.isFire()){
                                        return RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_crush_fire.png"));
                                    }else{
                                        return RenderType.entityCutout(FDBosses.location("textures/entities/malkuth/malkuth_crush_ice.png"));
                                    }
                                }))
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
        event.registerEntityRenderer(BossEntities.EYE_OF_MALKUTH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_VERTICAL_RAY_ATTACK.get(), ChesedVerticalRayAttackRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_ONE_SHOT_VERTICAL_RAY_ATTACK.get(), ChesedOneShotVerticalRayRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_FIRE_TRAIL.get(), NullEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.FLYING_SWORD.get(), FlyingSwordRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_SLASH.get(), MalkuthSlashRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_CHAIN.get(), MalkuthChainRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_CANNON_PROJECTILE.get(), MalkuthCannonProjectileRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_GIANT_SWORD.get(), MalkuthGiantSwordSlashRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_BOULDER.get(), MalkuthBoulderRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_EARTHQUAKE.get(), MalkuthEarthquakeRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_FLOOR.get(), MalkuthFloorRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_FIREBALL.get(), MalkuthFireballRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_PLAYER_FIREBALL.get(), MalkuthPlayerFireIceBallRenderer::new);
        event.registerEntityRenderer(BossEntities.MALKUTH_REPAIR_ENTITY.get(), MalkuthRepairEntityRenderer::new);
        event.registerEntityRenderer(BossEntities.CHESED_MINI_RAY.get(), ChesedMiniRayRenderer::new);
    }
}
