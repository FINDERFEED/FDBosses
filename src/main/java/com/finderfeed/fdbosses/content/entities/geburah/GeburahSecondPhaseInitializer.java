package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.chromatic_abberation.ChromaticAbberationData;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class GeburahSecondPhaseInitializer extends BossInitializer<GeburahEntity> {

    public GeburahSecondPhaseInitializer(GeburahEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {

        this.getBoss().mainAttackChain.reset();
        CutsceneData cutsceneData = this.createCutsceneData();

        for (var serverPlayer : this.getBoss().getArenaEntities(ServerPlayer.class)) {
            if (!serverPlayer.isDeadOrDying()) {
                FDLibCalls.startCutsceneForPlayer(serverPlayer, cutsceneData);
            }
        }

    }

    private CutsceneData createCutsceneData(){

        GeburahEntity boss = this.getBoss();

        Vec3 pos = boss.position();

        CutsceneData cutsceneData = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0f,0f,0f,1f),0,0,40)
                .time(40)
                .timeEasing(EasingType.EASE_IN_OUT)
                ;

        CameraPos lastPos = null;

        cutsceneData.addCameraPos(new CameraPos(pos.add(20, 4,0), new Vec3(-1,0.4,0)));
        cutsceneData.addCameraPos(lastPos = new CameraPos(pos.add(28, 4,0), new Vec3(-1,0.5,0)));

        CutsceneData cutsceneData1 = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.CHROMATIC_ABBERATION, new ChromaticAbberationData(0.025f),0,0,20)
                .time(40)
                .timeEasing(EasingType.EASE_OUT);

        cutsceneData1.addCameraPos(new CameraPos(lastPos.getPos(), new Vec3(-1,0.5,0)));
        cutsceneData1.addCameraPos(lastPos = new CameraPos(lastPos.getPos().add(2,-0.5,0), new Vec3(-1,0.6,0)));

        CutsceneData cutsceneData2 = CutsceneData.create()
                .addScreenEffect(10, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0f,0f,0f,1f),20,20,20)
                .time(40)
                .addCameraPos(lastPos)
                ;

        cutsceneData.nextCutscene(cutsceneData1.nextCutscene(cutsceneData2));

        return cutsceneData;
    }

    @Override
    public void onFinish() {
        this.getBoss().mainAttackChain.reset();

    }

    @Override
    public void onTick() {

        GeburahEntity geburah = this.getBoss();
        int tick = this.getTick();

        if (tick > 140){
            this.setFinished();
        }

        if (tick >= 40){
            if (tick == 40){

                geburah.level().playSound(null, geburah.getX(), geburah.getY(), geburah.getZ(), BossSounds.GEBURAH_CORE_RAY_STRIKE.get(), SoundSource.HOSTILE, 5f, 0.8f);
                geburah.level().playSound(null, geburah.getX(), geburah.getY(), geburah.getZ(), BossSounds.CHESED_RAY.get(), SoundSource.HOSTILE, 5f, 1f);

                for (var entity : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, geburah.level(), geburah.position().add(0,-1,0), GeburahEntity.ARENA_HEIGHT, GeburahEntity.ARENA_RADIUS + 10)) {
                    PacketDistributor.sendToPlayer(entity, new DefaultShakePacket(FDShakeData.builder()
                            .outTime(20)
                            .amplitude(2)
                            .build()));
                }
            }
            geburah.getEntityData().set(GeburahEntity.SECOND_PHASE, true);
        }else if (tick > 20){
            if (tick == 21){
                float angle = FDMathUtil.FPI * 2 / 3f;
                for (int i = 0; i < 3; i++){
                    StripeParticleOptions stripeParticleOptions = StripeParticleOptions.createHorizontalCircling(
                            new FDColor(1f, 0.3f, 0.2f, 1f),new FDColor(1f, 0.6f, 0.2f, 1f),
                            new Vec3(0, 1, 0), angle * i, 0.3f, 20, 100, 0, 10, 0.5f,
                            0.5f, true, true
                    );

                    FDLibCalls.sendParticles((ServerLevel) geburah.level(), stripeParticleOptions, geburah.getCorePosition(), 200);
                }
            }
            BossUtil.geburahRayChargeParticles((ServerLevel) geburah.level(), geburah.getCorePosition(), 100, geburah);


            if (tick == 35){
                FDLibCalls.sendImpactFrames((ServerLevel) this.getBoss().level(), this.getBoss().position(), 60,
                        new ImpactFrame().setDuration(2),
                        new ImpactFrame().setDuration(1).setInverted(true),
                        new ImpactFrame().setDuration(1),
                        new ImpactFrame().setDuration(1).setInverted(true),
                        new ImpactFrame().setDuration(1)
                );
            }

        }else if (tick < 10){
            geburah.propagateSins(0);
            geburah.removeAllArenaTrash();
            geburah.judgementBirdSpawnTicker = 0;

           geburah.removeCrystalsFromPlayerInventories();

        }

    }

}
