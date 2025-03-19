package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDLib;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CurveType;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ChesedBossInitializer extends BossInitializer<ChesedEntity> {

    public ChesedBossInitializer(ChesedEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {
        ChesedEntity chesedEntity = this.getBoss();

        Level level = chesedEntity.level();

        Vec3 position = chesedEntity.position();

        Vec3 forward = chesedEntity.getForward().multiply(1,0,1).normalize();


        List<Player> players = chesedEntity.getCombatants(true);

        Vec3 left = forward.yRot(FDMathUtil.FPI / 2);

        double distBetweenPlayers = 2;

        double d = (players.size() - 1) * distBetweenPlayers;

        double hd = d / 2;

        Vec3 baseTPPos = position.add(forward.multiply(20,0,20));

        Vec3 basePlayerTPPos = baseTPPos.add(left.reverse().multiply(hd,0,hd));

        int i = 0;

        CutsceneData cutsceneData = this.startCutsceneData(position,forward);

        for (Player player : players){
            ServerPlayer serverPlayer = (ServerPlayer) player;

            FDLibCalls.sendScreenEffect(serverPlayer, FDScreenEffects.SCREEN_COLOR.get(),new ScreenColorData(0f,0f,0f,1f),0,10,20);

            double dist = i * distBetweenPlayers;

            Vec3 tppos = basePlayerTPPos.add(left.multiply(dist,0,dist));

            Vec3 between = basePlayerTPPos.subtract(position).multiply(1,0,1);

            float angle = 180 + (float)Math.toDegrees(Math.atan2(between.x,between.z));

            serverPlayer.teleportTo((ServerLevel) serverPlayer.level(),tppos.x,tppos.y,tppos.z,angle,0);

            i++;

            FDLibCalls.startCutsceneForPlayer(serverPlayer,cutsceneData);
        }
        chesedEntity.getSystem().startAnimation("APPEAR", AnimationTicker.builder(BossAnims.CHESED_APPEAR)
                .setLoopMode(Animation.LoopMode.ONCE)
                .setToNullTransitionTime(0)
                .build());

    }


    private CutsceneData startCutsceneData(Vec3 bossPosition, Vec3 forwardVector){

        CutsceneData data = new CutsceneData()
                .moveCurveType(CurveType.CATMULLROM)
                .timeEasing(EasingType.EASE_OUT)
                .time(100)
                .stopMode(CutsceneData.StopMode.UNSTOPPABLE);

        Vec3 firstPos = bossPosition.add(0,4,0).add(forwardVector.multiply(40,40,40));
        Vec3 secondPos = bossPosition.add(0,1.5,0).add(forwardVector.multiply(3,3,3));

        data.addCameraPos(new CameraPos(firstPos,forwardVector.reverse()));
        data.addCameraPos(new CameraPos(secondPos,forwardVector.reverse()));

        return data;
    }

    @Override
    public void onFinish() {
        ChesedEntity chesedEntity = this.getBoss();

        List<Player> players = chesedEntity.getCombatants(true);

        for (Player player : players){
            ServerPlayer serverPlayer = (ServerPlayer) player;
            FDLibCalls.stopCutsceneForPlayer(serverPlayer);
        }

    }

    @Override
    public void onTick() {
        int endTick = 120;
        ChesedEntity entity = this.getBoss();
        if (this.getTick() >= endTick){
            this.setFinished();
        }else if (this.getTick() == endTick - 1){
            List<Player> players = entity.getCombatants(true);
            for (Player player : players){
                ServerPlayer serverPlayer = (ServerPlayer) player;
                FDLibCalls.sendScreenEffect(serverPlayer, FDScreenEffects.SCREEN_COLOR.get(),new ScreenColorData(0f,0f,0f,1f),0,20,20);
            }
        }else if (this.getTick() == 48){
            SlamParticlesPacket packet = new SlamParticlesPacket(
                    new SlamParticlesPacket.SlamData(entity.getOnPos(),entity.position().add(0,0.5,0),new Vec3(1,0,0))
                            .maxAngle(FDMathUtil.FPI * 2)
                            .maxSpeed(0.3f)
                            .collectRadius(2)
                            .maxParticleLifetime(30)
                            .count(200)
                            .maxVerticalSpeedEdges(0.15f)
                            .maxVerticalSpeedCenter(0.15f)
            );
            PacketDistributor.sendToPlayersTrackingEntity(entity,packet);

            DefaultShakePacket defaultShakePacket = new DefaultShakePacket(FDShakeData.builder()
                    .inTime(2)
                    .stayTime(5)
                    .outTime(5)

                    .amplitude(0.2f)
                    .frequency(50f)
                    .build());
            PacketDistributor.sendToPlayersTrackingEntity(entity,defaultShakePacket);
        }
    }

}
