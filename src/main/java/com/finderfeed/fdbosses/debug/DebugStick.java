package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_mini_ray.ChesedMiniRay;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahStompingController;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_earthquake.GeburahEarthquake;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquake;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword.MalkuthGiantSwordSlash;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashProjectile;
import com.finderfeed.fdbosses.content.projectiles.MalkuthPlayerFireIceBall;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.init.FDRenderTypes;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.ModelSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.BaseModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.fdmodel.FDModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.item_stack.ItemStackAttachmentData;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.render_types.FDRenderType;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.SimplexNoise;

import java.util.*;
import java.util.function.Predicate;

public class DebugStick extends Item {

    private Vec3 p1;
    private Vec3 p2;

    private Vec3 anchor = Vec3.ZERO;

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (level.isClientSide){

//            Vec3 start = player.getEyePosition().add(0,-0.2f,0);
//            Vec3 end = start.add(player.getLookAngle().multiply(100,100,100));
//
//            var options = GeburahRayOptions.builder()
//                    .end(end)
//                    .color(1f,0.8f,0.2f)
//                    .time(0,2,7)
//                    .width(1f)
//                    .build();
//
//            level.addParticle(options, start.x, start.y,start.z,0,0,0);
//

        }

        if (!level.isClientSide){
            Vec3 start = player.getEyePosition().add(0,-0.2f,0);
            Vec3 end = start.add(player.getLookAngle().multiply(100,100,100));
            ClipContext clipContext = new ClipContext(start,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());

            BlockHitResult result = level.clip(clipContext);

            if (result.getType() != HitResult.Type.MISS){
//
//                BossUtil.createOnEarthBlockExplosionEffect(level, result.getLocation(), end.subtract(start).normalize());
//                BossUtil.geburahRayParticles((ServerLevel) level, result.getLocation(), 200, start.subtract(end).normalize());

                float radius = 50;

                for (var geburah : FDTargetFinder.getEntitiesInSphere(GeburahEntity.class, level, player.position(), 200)){
                    var rayController = geburah.getRayController();
                    rayController.shoot(20,List.of(
                            result.getLocation()
//                            result.getLocation().add(
//                                    level.random.nextFloat() * radius * 2 - radius,
//                                    0,
//                                    level.random.nextFloat() * radius * 2 - radius
//                            ),
//                            result.getLocation().add(
//                                    level.random.nextFloat() * radius * 2 - radius,
//                                    0,
//                                    level.random.nextFloat() * radius * 2 - radius
//                            ),
//                            result.getLocation().add(
//                                    level.random.nextFloat() * radius * 2 - radius,
//                                    0,
//                                    level.random.nextFloat() * radius * 2 - radius
//                            ),
//                            result.getLocation().add(
//                                    level.random.nextFloat() * radius * 2 - radius,
//                                    0,
//                                    level.random.nextFloat() * radius * 2 - radius
//                            )
                    ));
//                    GeburahStompingController stompingController = geburah.getStompingController();
//                    stompingController.stompFullCircle(20, true, 1f, 1f);
                }

            }


//            GeburahEarthquake.summon(level, player.getOnPos(), 2,30,1f,1,player.getLookAngle(),FDMathUtil.FPI / 2);



//            Vec3 look = player.getLookAngle();
//            MalkuthPlayerFireIceBall.summon(player, player.getEyePosition().add(look), look.multiply(4,4,4), MalkuthAttackType.FIRE, player.getItemInHand(InteractionHand.MAIN_HAND));

//            Vec3 f = player.getLookAngle().multiply(1,0,1).normalize().multiply(10,10,10);
//
//
//            MalkuthGiantSwordSlash malkuthGiantSwordSlash = MalkuthGiantSwordSlash.summon(level, player.position(), player.getLookAngle(), MalkuthAttackType.ICE, 30);
//            MalkuthGiantSwordSlash malkuthGiantSwordSlash2 = MalkuthGiantSwordSlash.summon(level, player.position().add(f.yRot(-FDMathUtil.FPI/2)), player.getLookAngle(), MalkuthAttackType.FIRE);

//            MalkuthEarthquake malkuthEarthquake = MalkuthEarthquake.summon(level, MalkuthAttackType.FIRE,player.position(), player.getLookAngle().multiply(1,0,1).normalize().multiply(30,30,30),20,
//                    FDMathUtil.FPI, 1);


//            Vec3 pos = player.position().add(5,5,5);
//
//            ChesedMiniRay chesedMiniRay = ChesedMiniRay.summon(level, player, player.getItemInHand(InteractionHand.MAIN_HAND), player);



//            if (p1 == null){
//                p1 = player.position();
//            }else if (p2 == null){
//                p2 = player.position();
//            }else{
//                System.out.println(p2.subtract(p1));
//                p1 = null;
//                p2 = null;
//            }


//            for (int x = -200; x <= 200; x++){
//                for (int z = -200; z <= 200; z++){
//
//                    BlockPos pos = player.getOnPos().above().offset(x,0,z);
//
//                    BlockState state = level.getBlockState(pos.above());
//                    BlockState state1 = level.getBlockState(pos);
//
//                    if (!state.isAir() && state1.isAir()){
//                        level.setBlock(pos, Blocks.DEEPSLATE.defaultBlockState(), Block.UPDATE_CLIENTS);
//                    }
//
//                }
//            }


//            if (player.isCrouching()){
//                anchor = player.position();
//            }else{
//                Vec3 eyePos = player.getEyePosition();
//                Vec3 offs = eyePos.subtract(anchor);
//                Vec3 lookAngle = player.getLookAngle();
//                new CameraPos(eyePos, lookAngle);
//                System.out.println("new CameraPos(base.add(%.3f,%.3f,%.3f), new Vec3(%.3f,%.3f,%.3f))".formatted(
//                        (float)offs.x,
//                        (float)offs.y,
//                        (float)offs.z,
//
//                        (float)lookAngle.x,
//                        (float)lookAngle.y,
//                        (float)lookAngle.z
//                ));
//            }



            if (true) return InteractionResultHolder.consume(player.getItemInHand(hand));

//            int mountainDiameter = 40;
//
//
//            for (int x = 0; x <= mountainDiameter; x++){
//                for (int z = 0; z <= mountainDiameter; z++){
//
//                    double xd = x - mountainDiameter/2f;
//                    double zd = z - mountainDiameter/2f;
//
//                    double radMod = 1 - Math.sqrt(xd * xd + zd * zd) / (mountainDiameter / 2f);
//
//                    radMod = FDEasings.easeOut(FDEasings.easeOut((float) radMod));
//
//                    double xv = (x / (float)mountainDiameter) * Math.PI;
//                    double zv = (z / (float)mountainDiameter) * Math.PI;
//
//                    double res1 = Math.sin(xv) * ( Math.abs(Math.sin(0.25 * xv)) + Math.abs(Math.sin(xv)) + Math.abs(Math.sin(2.5 * xv)) );
//                    double res2 = Math.sin(zv) * ( Math.abs(Math.sin(0.25 * zv)) + Math.abs(Math.sin(zv)) + Math.abs(Math.sin(2.5 * zv)) );
//
//                    double noiseValue = (SimplexNoise.noise((float) xv * 1f,(float) zv * 1f) / 2 + 1) * 2;
//
//                    int height = (int) Math.round((res1 + res2 + noiseValue)/2 * 10 * radMod);
//
//                    BlockPos base = new BlockPos(x,0,z).offset(player.getOnPos());
//
//                    for (int y = 0; y < height;y++){
//
//                        level.setBlock(base.offset(0,y,0),Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//
//
//                    }
//
//                }
//            }

//            int radiusStart = 30;
//            int radiusEnd = 90;
//            int fixedRadiusEnd = radiusEnd;
//            float between = radiusEnd - radiusStart;
//
//
//            float minDoughnutRadius = 5;
//            float maxDougnutRadius = 17;
//
//            int maxHeight = 30;
//            int startMountainPeakHeight = 18;
//
//            float noiseModifier = 0.05337f;
//
//            float noiseOffset = 4332.43f;
//            noiseOffset = 15435.324f;
//
//            int deepslateHeight = 6;
//
//            Random r1 = new Random(432432);
//            Random r2 = new Random(8454534);
//
//            for (int x = -radiusEnd; x <= radiusEnd;x++){
//                for (int z = -radiusEnd; z <= radiusEnd;z++){
//                    double d = Math.sqrt(x * x + z * z);
//                    if (d < radiusStart || d > radiusEnd) continue;
//
//                    double angle = Math.atan2(z,x);
//
//                    float doughnutSinVal = (float) Math.abs(Math.cos(angle * 4));
//                    doughnutSinVal = (float) Math.pow(doughnutSinVal,2);
//
//
//                    float currentDougnutRad = FDMathUtil.lerp(minDoughnutRadius,maxDougnutRadius,doughnutSinVal);
//
//
//
//                    float sinval = 0.7f + 0.3f * FDEasings.easeInOut(( float) Math.abs(Math.cos(angle * 4)));
//
//                    float radval = 1 - Math.abs( (float)d - radiusStart - between/2 ) / between * 2; radval = FDEasings.easeOut(radval);
//
//
//                    float noise = SimplexNoise.noise(x * noiseModifier + noiseOffset, z * noiseModifier + noiseOffset) * 0.5f + 1f;
//
//
//                    int height = Math.round(sinval * maxHeight * (noise * 0.4f + 0.6f) * radval);
//
//
//                    for (int y = 0; y < height;y++){
//
//                        if (y >= 1) {
//                            Vec3 doughnutCenterLine = new Vec3(x, 0, z).normalize().multiply(radiusStart + between / 2, radiusStart + between / 2, radiusStart + between / 2).add(0,1,0);
//                            Vec3 blockAboutToPlacePos = new Vec3(x, y, z);
//                            Vec3 b = blockAboutToPlacePos.subtract(doughnutCenterLine);
//                            if (b.length() < currentDougnutRad) {
//                                continue;
//                            }
//                        }
//
//                        BlockPos pos = player.getOnPos().offset(x,y,z);
//                        BlockState state = Blocks.BLACKSTONE.defaultBlockState();
//
//
//                        double peakheightsin = Math.abs(Math.sin(angle * 8 + Math.PI/4)) * 6;
//
//                        int dheight = deepslateHeight + (int) Math.round(Math.abs(Math.cos(angle * 4)) * 5);
//
//                        if (y == height - 1 && height >= startMountainPeakHeight - peakheightsin){
//
//                            float currentDegrees = (float)Math.toDegrees((float) angle + FDMathUtil.FPI) ;
//                            float zh = currentDegrees % 90;
//
//                            if (zh > 20 && zh < 70){
//
//                                state = Blocks.SNOW_BLOCK.defaultBlockState();
//                                level.setBlock(pos.below(),Blocks.BLUE_ICE.defaultBlockState(),2);
//
//                            }else{
//                                float ch = r1.nextFloat();
//                                if (ch > 0.75){
//                                    state = Blocks.MAGMA_BLOCK.defaultBlockState();
//                                }
//
//                            }
//
//
//                        }else if (y < dheight){
//
//                            float chanceToBlackstone = y / (float) dheight;
//                            chanceToBlackstone = chanceToBlackstone * chanceToBlackstone;
//
//                            float p = r2.nextFloat();
//
//                            if (p > chanceToBlackstone){
//                                state = Blocks.DEEPSLATE.defaultBlockState();
//                            }
//
//                        }
//
//                        level.setBlock(pos, state, 2);
//
//                    }
//
//
//                }
//
//
//
//
//
//
//
//
//            }
//
//
//            int craterRadius = 8;
//
//            for (int i = 0; i < 4;i++){
//
//                Vec3 d = new Vec3(radiusStart + between/2,0,0).yRot(i * FDMathUtil.FPI / 2);
//
//                Vec3 posDown = player.position().add(d);
//                Vec3 posUp = posDown.add(0,maxHeight,0);
//
//                ClipContext clipContext = new ClipContext(posUp,posDown, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
//
//                var ctx = level.clip(clipContext);
//
//                BlockPos craterPosStart = ctx.getBlockPos();
//
//                for (int x = -craterRadius;x < craterRadius;x++){
//                    for (int y = -craterRadius;y < craterRadius;y++){
//                        for (int z = -craterRadius;z < craterRadius;z++){
//
//                            if (Math.sqrt(x*x + y*y + z*z) > craterRadius) continue;
//
//                            BlockPos removePos = craterPosStart.offset(x,y,z);
//
//                            level.removeBlock(removePos, false);
//
//                        }
//                    }
//                }
//
//
//            }









//            UUID uuid1 = UUID.fromString("87b5dfdc-ed5b-4c04-9bcd-394cb7e47851");
//            UUID uuid2 = UUID.fromString("5389128f-b6a0-4029-b1cf-e6141e24db97");
//            UUID uuid3 = UUID.fromString("6606e335-52c1-4e46-8898-07c519d22e55");
//            UUID uuid4 = UUID.fromString("063b3de6-a3c6-4ba0-b4d2-de651bdacbff");
//            UUID uuid5 = UUID.fromString("659b684d-f36e-4f94-8310-11e8a32139d1");
//            UUID uuid6 = UUID.fromString("5e35c81e-c4da-40bf-ace3-4818320cbfd5");
//
//            Vec3 basePos = player.position().add(0,player.getEyeHeight(),0);
//            List<Entity> entities = FDHelpers.traceEntities(level, basePos,basePos.add(player.getLookAngle().multiply(3,3,3)),0,Entity::isAlive);
//            for (Entity entity : entities){
//                if (entity instanceof ChesedEntity chesedEntity){
//                    ModelSystem modelSystem = chesedEntity.getModelSystem();
//                    if (player.isCrouching()) {
//                        modelSystem.removeAttachment(uuid1);
//                        modelSystem.removeAttachment(uuid2);
//                        modelSystem.removeAttachment(uuid3);
//                    }else{
//                        modelSystem.attachToLayer(0, "r16", uuid1, FDModelAttachmentData.create(new BaseModelAttachmentData()
//                                .translation(0,0,0)
//                                .scale(0.5f,0.5f,0.5f)
//                                .rotation(0,0,FDMathUtil.FPI + 2 * FDMathUtil.FPI/4), BossModels.CHESED.get())
//                                .color(1f,1f,0f,1f)
//                                .renderType(FDRenderTypes.ENTITY_CUTOUT_NO_CULL.get())
//                                .texture(FDBosses.location("textures/entities/chesed.png")));
//
//                        modelSystem.attachToLayer(0, "r11", uuid2, new ItemStackAttachmentData( new BaseModelAttachmentData()
//                                .translation(-1,0,0)
//                                .scale(2,2,2)
//                                .rotation(FDMathUtil.FPI,0,-FDMathUtil.FPI/4), Items.DIAMOND_AXE.getDefaultInstance()));
//                        modelSystem.attachToLayer(0, "t", uuid3, new ItemStackAttachmentData(new BaseModelAttachmentData(), Items.DIAMOND_AXE.getDefaultInstance()));
//                    }
//                }
//            }


            BlockPos base = player.getOnPos();
//
//
//            for (int x = 0; x < 300;x++){
//                for (int y = 0; y < 20;y++){
//                    boolean wasInterrupted = false;
//                    for (int z = 0; z < 300;z++){
//                        BlockPos setPos = base.offset(x,y,z);
//                        BlockState state = level.getBlockState(setPos);
//                        if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)){
//                            level.setBlock(setPos,Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
//                        }else{
//                            wasInterrupted = true;
//                            break;
//                        }
//                    }
//                    if (wasInterrupted) {
//                        for (int z = 200; z > 0; z--) {
//                            BlockPos setPos = base.offset(x, y, z);
//                            BlockState state = level.getBlockState(setPos);
//                            if (state.isAir()  || state.is(Blocks.STRUCTURE_VOID)) {
//                                level.setBlock(setPos, Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
//                            } else {
//                                break;
//                            }
//                        }
//                    }
//                }
//            }

            List<Block> allowedToReplace = List.of(
                    Blocks.BLACKSTONE,
                    Blocks.POLISHED_BLACKSTONE,
                    Blocks.POLISHED_BLACKSTONE_BRICKS
            );

            int rad = 40;

            int height = 9;

            for (int x = -rad; x <= rad; x++){
                for (int z = -rad; z <= rad; z++){


                    BlockState state = level.getBlockState(base);

                    Block block = state.getBlock();

                    if (!allowedToReplace.contains(block)) continue;





                }
            }



        }

        return super.use(level, player, hand);
    }
}
