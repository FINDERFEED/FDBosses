package com.finderfeed.fdbosses.content.entities.geburah.scales_controller;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class GeburahScalesController implements AutoSerializable {

    public static final int MAX_DISPLACEMENT = 5;

    @SerializableField
    private int currentDisplacement = 0;
    private int oldDisplacement = 0;

    private int displacementTime = -1;
    private int displacementMaxTime = -1;

    public GeburahEntity geburah;

    public GeburahScalesController(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void tick(){
        Level level = geburah.level();

        if (!level.isClientSide) {

        }else{
            this.clientTick();
        }
    }

    private void clientTick(){
        if (displacementTime == -1 || displacementMaxTime == -1) {
            oldDisplacement = currentDisplacement;
        }else{
            displacementTime++;
            if (displacementTime >= displacementMaxTime){
                displacementTime = -1;
                displacementMaxTime = -1;
                oldDisplacement = currentDisplacement;
            }
        }
    }

    public float getClientDisplacementAngle(float pticks){
        float anglePerDisplacement = 5f;
        if (displacementTime == -1) {
            return currentDisplacement * anglePerDisplacement;
        } else {
            float p = Mth.clamp(displacementTime + pticks, 0, displacementMaxTime) / displacementMaxTime;

            if (displacementMaxTime < 40){
                p = FDEasings.easeOutBack(p);
            }else{
                p = FDEasings.easeInOut(p);
            }

            return FDMathUtil.lerp(oldDisplacement, currentDisplacement, p) * anglePerDisplacement;
        }
    }


    public void setCurrentDisplacement(int currentDisplacement, int displacementTime) {

        currentDisplacement = Mth.clamp(currentDisplacement, -MAX_DISPLACEMENT, MAX_DISPLACEMENT);

        if (!geburah.level().isClientSide) {
            if (this.currentDisplacement != currentDisplacement) {
                PacketDistributor.sendToPlayersTrackingEntity(this.geburah, new GeburahScalesControllerSetDisplacement(geburah, currentDisplacement, displacementTime));
            }
        }else {
            if (this.displacementMaxTime != -1 || this.displacementTime != -1) {
                oldDisplacement = this.currentDisplacement;
            }
            this.displacementMaxTime = displacementTime;
            this.displacementTime = 0;
        }
        this.currentDisplacement = currentDisplacement;
    }

    public int getCurrentDisplacement() {
        return currentDisplacement;
    }

    public void syncToPlayer(ServerPlayer serverPlayer){
        PacketDistributor.sendToPlayer(serverPlayer, new GeburahScalesControllerSetDisplacement(geburah, currentDisplacement, 100));
    }


}
