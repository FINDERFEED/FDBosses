package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_opening_floor.GeburahOpeningFloor;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import net.minecraft.server.level.ServerLevel;

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
        }

        if (this.getTick() > BossAnims.GEBURAH_APPEAR.get().getAnimTime()){
            this.setFinished();
        }

    }

}
