package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadControllerContainer;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MalkuthSecondPhaseInitializer extends BossInitializer<MalkuthEntity> {

    public MalkuthSecondPhaseInitializer(MalkuthEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {

        MalkuthEntity boss = this.getBoss();

        boss.getAnimationSystem().stopAnimation(MalkuthEntity.MAIN_LAYER);
        boss.getAnimationSystem().startAnimation(MalkuthEntity.MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
        boss.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.ANIMATION);

        Vec3 tppos = boss.spawnPosition.add(MalkuthEntity.WALL_OFFSET);
        boss.teleportTo(tppos.x,tppos.y,tppos.z);
        boss.lookAt(EntityAnchorArgument.Anchor.EYES, tppos.add(0,0,-100));


        CutsceneData data = this.createCutscene();

        for (var player : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, boss.level(), boss.spawnPosition.add(0,-2,0),30,40)){
            FDLibCalls.startCutsceneForPlayer(player, data);
        }

        boss.removeThingsForSecondPhase();

        MalkuthBossInitializer.teleportCombatants(boss);

    }

    private CutsceneData createCutscene(){

        Vec3 base = this.getBoss().spawnPosition;

        CameraPos start = new CameraPos(base.add(0,16.148,22.391), new Vec3(0,-0.053,0.999));
        CameraPos end = new CameraPos(base.add(0,20.653,38.780), new Vec3(0.001,-0.185,-0.983));

        CutsceneData data1 = CutsceneData.create()
                .time(22)
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 0,0, 20)
                .addCameraPos(start)
                ;

        CutsceneData data2 = CutsceneData.create()
                .time(15)
                .timeEasing(EasingType.EASE_IN_OUT)
                .addCameraPos(start)
                .addCameraPos(new CameraPos(base.add(4.695,17,29.002), new Vec3(-0.909,-0.417,-0.009)))
                .addCameraPos(end)
                ;

        CutsceneData data3 = CutsceneData.create()
                .time(80)
                .addScreenEffect(60, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1), 20,20, 40)
                .addCameraPos(end)
                ;

        data1.nextCutscene(data2.nextCutscene(data3));

        return data1;
    }

    @Override
    public void onFinish() {

        var boss = this.getBoss();
        Vec3 tppos = boss.spawnPosition;
        boss.teleportTo(tppos.x,tppos.y,tppos.z);
        boss.lookAt(EntityAnchorArgument.Anchor.EYES, tppos.add(0,0,-100));
        boss.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.LOOK);

    }

    @Override
    public void onTick() {

        MalkuthEntity boss = this.getBoss();

        int tick = this.getTick();

        int start = 10;
        int cannonShootTick = start + 20;
        int cannonDestroyTick = cannonShootTick + 42;

        int endTick = cannonDestroyTick + 40;

        if (tick == start){

            boss.getAnimationSystem().startAnimation(MalkuthEntity.MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_SWORD_FORWARD)
                            .setSpeed(0.75f)
                            .setToNullTransitionTime(0)
                    .build());

        }else if (tick == cannonShootTick){

            var cannons = boss.getMalkuthCannons();

            for (int i = 0; i < cannons.size();i++){
                MalkuthCannonEntity cannon = cannons.get(i);
                Vec3 pos;
                if (i < cannons.size()/2){
                    pos = boss.spawnPosition.add(MalkuthEntity.FIRE_PLAYER_CANNON_OFFSET);
                }else{
                    pos = boss.spawnPosition.add(MalkuthEntity.ICE_PLAYER_CANNON_OFFSET);
                }
                cannon.shoot(List.of(pos),0);
            }

        }else if (tick == cannonDestroyTick){
            var playerCannons = boss.getPlayerCannons(false);
            for (var cannon : playerCannons){
                cannon.setBrokenRequiresMaterials(true);
            }
        }else if (tick == endTick){
            Vec3 tppos = boss.spawnPosition;
            boss.teleportTo(tppos.x,tppos.y,tppos.z);
            boss.lookAt(EntityAnchorArgument.Anchor.EYES, tppos.add(0,0,-100));
            boss.getAnimationSystem().stopAnimation(MalkuthEntity.MAIN_LAYER);
            boss.getAnimationSystem().startAnimation(MalkuthEntity.MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
        }else if (tick >= endTick + 40){
            this.setFinished();
        }

    }

}
