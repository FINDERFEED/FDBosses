package com.finderfeed.fdbosses.content.entities.geburah;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public class GeburahLaserAttackPreparator {

    public static final int FADE_OUT = 10;

    public GeburahEntity geburah;

    public int time = -1;
    public int currentTime = -1;

    public GeburahLaserAttackPreparator(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void launchPreparation(int timeUntilAttack){
        if (geburah.level().isClientSide) {
            this.time = timeUntilAttack + FADE_OUT;
            this.currentTime = time;
        }else{
            PacketDistributor.sendToPlayersTrackingEntity(geburah, new GeburahPrepareAttackPacket(geburah, timeUntilAttack));
        }
    }

    public void tick(){
        currentTime = Mth.clamp(currentTime - 1,0,time);
        if (currentTime == 0){
            time = -1;
            currentTime = -1;
        }
    }

}
