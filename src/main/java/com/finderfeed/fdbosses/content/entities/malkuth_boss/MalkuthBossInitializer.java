package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadControllerContainer;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class MalkuthBossInitializer extends BossInitializer<MalkuthEntity> {


    public MalkuthBossInitializer(MalkuthEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {

        MalkuthEntity malkuth = this.getBoss();

        Vec3 v1 = new Vec3(-6.5,-1,-25).add(malkuth.position());
        Vec3 v2 = new Vec3(6.5,-1,-25).add(malkuth.position());

        AABB aabb1 = new AABB(-5,-5,-5,5,5,5).move(v1);
        AABB aabb2 = new AABB(-5,-5,-5,5,5,5).move(v2);

        if (malkuth.level().getEntitiesOfClass(MalkuthCannonEntity.class, aabb1).isEmpty()) {
            MalkuthCannonEntity cannon1 = MalkuthCannonEntity.summon(malkuth.level(), v1, v1.add(0, 0, 100), MalkuthAttackType.FIRE);
            cannon1.setPlayerControlled(true);
        }
        if (malkuth.level().getEntitiesOfClass(MalkuthCannonEntity.class, aabb2).isEmpty()) {
            MalkuthCannonEntity cannon2 = MalkuthCannonEntity.summon(malkuth.level(), v2, v2.add(0, 0, 100), MalkuthAttackType.ICE);
            cannon2.setPlayerControlled(true);
        }

        var cutsceneData = this.constructCutscene();

        for (var player : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, malkuth.level(), malkuth.spawnPosition.add(0,-2,0),30,30)){
            FDLibCalls.startCutsceneForPlayer(player, cutsceneData);
        }



    }

    private CutsceneData constructCutscene(){

        Vec3 base = this.getBoss().spawnPosition;

        CutsceneData data1 = CutsceneData.create()
                .time(60)
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 0,5,15)
                .addScreenEffect(40, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 20,0,0)
                .addCameraPos(new CameraPos(base.add(10.281,11.647,22.042), new Vec3(0.623,-0.037,0.781)))
                .addCameraPos(new CameraPos(base.add(6.313,11.647,23.500), new Vec3(0.623,-0.037,0.781)));

        CutsceneData data2 = CutsceneData.create()
                .time(60)
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 0,5,15)
                .addScreenEffect(40, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 20,0,0)
                .addCameraPos(new CameraPos(base.add(-10.281,11.647,22.042), new Vec3(-0.623,-0.037,0.781)))
                .addCameraPos(new CameraPos(base.add(-6.313,11.647,23.500), new Vec3(-0.623,-0.037,0.781)))
                ;

        CutsceneData data3 = CutsceneData.create()
                .time(40)
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 0,5,15)
                .addCameraPos(new CameraPos(base.add(0,26.296,36.434), new Vec3(0,0.280,0.960)))
                ;

        var lastCameraPos = new CameraPos(base.add(0,2.663,-7.178), new Vec3(0,0.017,1.000));

        CutsceneData data4 = CutsceneData.create()
                .time(50)
                .lookEasing(EasingType.EASE_IN_OUT)
                .timeEasing(EasingType.EASE_IN_OUT)
                .addCameraPos(new CameraPos(base.add(0,26.296,36.434), new Vec3(0,0.280,0.960)))
                .addCameraPos(new CameraPos(base.add(0,4.531,1.252), new Vec3(0,0.405,0.914)))
                .addCameraPos(lastCameraPos)
                ;

        CutsceneData data5 = CutsceneData.create()
                .time(40)
                .addScreenEffect(30, FDScreenEffects.SCREEN_COLOR.get(), new ScreenColorData(0,0,0,1), 10,20,10)
                .addCameraPos(lastCameraPos)
                ;

        data1.nextCutscene(data2.nextCutscene(data3.nextCutscene(data4.nextCutscene(data5))));

        return data1;
    }

    @Override
    public void onFinish() {

    }

    private ProjectileMovementPath movePath;

    @Override
    public void onTick() {

        MalkuthEntity boss = this.getBoss();



        Vec3 base = boss.spawnPosition;

        Vec3 startPos = base.add(0,23.620,59.886);

        int tick = this.getTick();

        int movePathTime = 50;

        if (movePath == null){
            movePath = new ProjectileMovementPath(movePathTime,false)
                    .addPos(startPos)
                    .addPos(base.add(0,46.091,44.675))
                    .addPos(base.add(0,49.459,18.125))
                    .addPos(base.add(0,21.895,3.019))
                    .addPos(base);
            boss.teleportTo(startPos.x,startPos.y,startPos.z);
            boss.lookAt(EntityAnchorArgument.Anchor.FEET, boss.position().add(0,0,-100));
        }

        int bossJumpStart = 160;

        int erruptionStart = bossJumpStart - 40;

        if (tick == erruptionStart){
            DefaultShakePacket.send((ServerLevel) boss.level(), boss.spawnPosition, 30, FDShakeData.builder()
                    .amplitude(0.1f)
                    .stayTime(60)
                    .build());
        }

        if (tick >= bossJumpStart){
            if (tick == bossJumpStart){
                DefaultShakePacket.send((ServerLevel) boss.level(), boss.spawnPosition, 30, FDShakeData.builder()
                                .amplitude(1f)
                                .outTime(30)
                        .build());
                boss.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.ANIMATION);
                boss.getAnimationSystem().startAnimation(MalkuthEntity.MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_SUMMON_ANIM)
                        .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                        .build());
            }else if (tick == bossJumpStart + movePathTime + 1){
                DefaultShakePacket.send((ServerLevel) boss.level(), boss.spawnPosition, 30, FDShakeData.builder()
                        .amplitude(0.5f)
                        .outTime(10)
                        .build());

                boss.doJumpStartParticles(0f);
            }
            if (!movePath.isFinished()) {
                boss.noPhysics = true;
                boss.setNoGravity(true);
                movePath.tick(boss);
            }else{

                boss.noPhysics = false;
                boss.setNoGravity(false);
                boss.teleportTo(base.x,base.y,base.z);
//                this.setFinished();
            }

        }



        this.summonCannonsTicks();

    }


    private void summonCannonsTicks(){
        int tick = this.getTick();
        MalkuthEntity boss = this.getBoss();

        HashMap<Integer, Pair<Vec3,MalkuthAttackType>> cannonSpawnMap = new HashMap<>(Map.of(
                10,new Pair<>(new Vec3(9, 10.0, 29.5),MalkuthAttackType.ICE),
                7,new Pair<>(new Vec3(13, 10.0, 27.5),MalkuthAttackType.FIRE),
                11,new Pair<>(new Vec3(11, 3.0, 29),MalkuthAttackType.FIRE),

                72 - 20,new Pair<>(new Vec3(-9, 10.0, 29.5),MalkuthAttackType.ICE),
                69 - 20,new Pair<>(new Vec3(-13, 10.0, 27.5),MalkuthAttackType.FIRE),
                68 - 20,new Pair<>(new Vec3(-11, 3.0, 29),MalkuthAttackType.ICE)
        ));

        if (cannonSpawnMap.containsKey(tick)){

            Pair<Vec3, MalkuthAttackType> sppos = cannonSpawnMap.get(tick);

            Vec3 pos = sppos.first.add(boss.spawnPosition);

            var cannons = BossTargetFinder.getEntitiesInCylinder(MalkuthCannonEntity.class, boss.level(), pos.add(0,-0.1,0), 4,2);

            if (cannons.isEmpty()) {
                MalkuthCannonEntity.summon(boss.level(), pos, pos.add(0, 0, -100), sppos.second);
            }
        }
    }

}
