package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ClockWyvernChargingSpot extends FDOwnableEntity {

    public static ClockWyvernChargingSpot summon(ClockWyvern clockWyvern){
        ClockWyvernChargingSpot chargingSpot = new ClockWyvernChargingSpot(BossEntities.CLOCK_WYVERN_CHARGING_SPOT.get(), clockWyvern.level());
        chargingSpot.setOwner(clockWyvern);
        chargingSpot.setPos(clockWyvern.position());
        clockWyvern.level().addFreshEntity(chargingSpot);
        chargingSpot.setOnWyvernPosition();
        return chargingSpot;
    }

    private int watchdog = 2;

    public ClockWyvernChargingSpot(EntityType<? extends ClockWyvernChargingSpot> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            var owner = this.getOwner();
            if (owner instanceof ClockWyvern clockWyvern){
                if (watchdog-- == 0 || clockWyvern.isDeadOrDying() || !clockWyvern.isChargingSpotAlive()){
                    this.setRemoved(RemovalReason.DISCARDED);
                }
                this.setOnWyvernPosition();
            }else{
                this.setRemoved(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        if (!level().isClientSide){
            if (this.getOwner() instanceof ClockWyvern clockWyvern){
                clockWyvern.destroyChargingSpot();
            }
        }
        return super.hurt(src, damage);
    }

    public void setOnWyvernPosition(){
        var owner = this.getOwner();
        if (owner instanceof ClockWyvern clockWyvern){
            Matrix4f mat = clockWyvern.getModelPartTransformation(clockWyvern, "body_back", ClockWyvern.getServerAnimatedModel(), 1);
            Vector3f pos = mat.transformPosition(new Vector3f(0, 0, -0.5f));
            var worldPos = clockWyvern.position().add(
                pos.x, pos.y, pos.z
            );
            this.setPos(worldPos);
        }
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return true;
    }

    @Override
    public int getTeamColor() {
        return 0xf7363d;
    }

    public void feedWatchdog(){
        watchdog = 2;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean syncsOwner() {
        return true;
    }

}
