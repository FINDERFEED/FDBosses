package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_opening_floor.GeburahOpeningFloor;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.chromatic_abberation.ChromaticAbberationData;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class GeburahBossInitializer extends BossInitializer<GeburahEntity> {

    public GeburahBossInitializer(GeburahEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {
        GeburahEntity geburah = this.getBoss();
        geburah.getAnimationSystem().startAnimation(GeburahEntity.MAIN_LAYER, AnimationTicker.builder(BossAnims.GEBURAH_APPEAR)
                .build());
        var level = this.getBoss().level();
        GeburahOpeningFloor openingFloor = new GeburahOpeningFloor(BossEntities.GEBURAH_OPENING_FLOOR.get(), level);
        openingFloor.setPos(this.getBoss().position());
        level.addFreshEntity(openingFloor);

        var cutscene = this.cutsceneData();

        List<ServerPlayer> players = BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level, geburah.position(), 80, GeburahEntity.ARENA_RADIUS);

        for (var serverPlayer : players){
            FDLibCalls.startCutsceneForPlayer(serverPlayer,cutscene);
        }


        int count = players.size();

        float angle = FDMathUtil.FPI * 2 / count;

        for (int i = 0; i < count; i++){

            float a = angle * i;

            Vec3 v = new Vec3(20,0,0).yRot(a);

            Vec3 pos = v.add(geburah.position());

            ServerPlayer player = players.get(i);
            player.teleportTo((ServerLevel) geburah.level(), pos.x,pos.y,pos.z, (float) -Math.toDegrees(a) + 90,0);
        }


    }

    @Override
    public void onFinish() {
        this.getBoss().getAnimationSystem().stopAnimation(GeburahEntity.MAIN_LAYER);

    }

    @Override
    public void onTick() {


        if (this.getTick() == BossAnims.GEBURAH_APPEAR.get().getAnimTime() + 1){
            FDLibCalls.sendImpactFrames((ServerLevel) this.getBoss().level(), this.getBoss().position(), 60,
                    new ImpactFrame().setDuration(2),
                    new ImpactFrame().setDuration(1).setInverted(true),
                    new ImpactFrame().setDuration(1),
                    new ImpactFrame().setDuration(1).setInverted(true),
                    new ImpactFrame().setDuration(1)
            );
        }else if (this.getTick() == BossAnims.GEBURAH_APPEAR.get().getAnimTime() - 3){
            DefaultShakePacket.send((ServerLevel) this.getBoss().level(), this.getBoss().position(), 60, FDShakeData.builder()
                    .inTime(5)
                    .outTime(10)
                    .amplitude(2f)
                    .build());
        }else if (this.getTick() == BossAnims.GEBURAH_APPEAR.get().getAnimTime()){
            this.getBoss().getEntityData().set(GeburahEntity.OPERATING, true);
        }else if (this.getTick() == BossAnims.GEBURAH_APPEAR.get().getAnimTime() + 2){
            Level level = this.getBoss().level();
            GeburahEntity geburah = this.getBoss();
            level.playSound(null, geburah.getX(), geburah.getY(), geburah.getZ(), BossSounds.GEBURAH_CORE_RAY_STRIKE.get(), SoundSource.HOSTILE, 5f, 0.8f);
            level.playSound(null, geburah.getX(), geburah.getY(), geburah.getZ(), BossSounds.CHESED_RAY.get(), SoundSource.HOSTILE, 5f, 1f);
        }

        if (this.getTick() > BossAnims.GEBURAH_APPEAR.get().getAnimTime() + 100){
            this.setFinished();
        }

    }

    private CutsceneData cutsceneData(){

        GeburahEntity geburah = this.getBoss();
        Vec3 bossPos = geburah.position();

        CutsceneData cutsceneData = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR.get(), new ScreenColorData(0f,0f,0f,1f),0,0,20)
                .time(90)
                .timeEasing(EasingType.EASE_IN_OUT)
                ;

        Vec3 p1 = new Vec3(0,0,8);
        Vec3 p2 = p1.yRot(FDMathUtil.FPI / 8);
        Vec3 p3 = p1.yRot(FDMathUtil.FPI / 4);
        Vec3 p4 = p1.yRot(FDMathUtil.FPI / 4 + FDMathUtil.FPI / 8);
        Vec3 p5 = p1.yRot(FDMathUtil.FPI / 2);

        p1 = p1.add(bossPos.add(0,2,0));
        p2 = p2.add(bossPos.add(0,2,0));
        p3 = p3.add(bossPos.add(0,2,0));
        p4 = p4.add(bossPos.add(0,2,0));
        p5 = p5.add(bossPos.add(0,2,0));

        CameraPos last;

        cutsceneData.addCameraPos(new CameraPos(p1, bossPos.subtract(p1)));
        cutsceneData.addCameraPos(new CameraPos(p2, bossPos.subtract(p2)));
        cutsceneData.addCameraPos(new CameraPos(p3, bossPos.subtract(p3)));
        cutsceneData.addCameraPos(new CameraPos(p4, bossPos.subtract(p4)));
        cutsceneData.addCameraPos(last = new CameraPos(p5, bossPos.subtract(p5)));

        CutsceneData cutsceneData1 = CutsceneData.create()
                .time(10)
                .addCameraPos(last);

        CutsceneData cutsceneData2 = CutsceneData.create()
                .time(50)
                .timeEasing(EasingType.EASE_IN_OUT)
                .addCameraPos(last)
                .addCameraPos(last = new CameraPos(last.getPos().add(17,2,0),new Vec3(-2,0.75,0)))
                ;

        CutsceneData cutsceneData3 = CutsceneData.create()
                .time(20)
                .addCameraPos(last);


        CutsceneData cutsceneData4 = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.CHROMATIC_ABBERATION, new ChromaticAbberationData(0.025f),0,0,20)
                .time(30)
                .addCameraPos(last)
                .timeEasing(EasingType.EASE_OUT)
                .addCameraPos(last = new CameraPos(last.getPos().add(2,0,0),new Vec3(-2,0.75,0)));

        CutsceneData cutsceneData5 = CutsceneData.create()
                .addScreenEffect(10,FDScreenEffects.SCREEN_COLOR.get(), new ScreenColorData(0f,0f,0f,1f),20,10,20)
                .time(30)
                .addCameraPos(last);

        cutsceneData.nextCutscene(cutsceneData1.nextCutscene(cutsceneData2.nextCutscene(cutsceneData3.nextCutscene(cutsceneData4.nextCutscene(cutsceneData5)))));

        return cutsceneData;
    }

}
