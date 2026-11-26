package com.finderfeed.fdbosses.content.entities.netzach.netzach_cane_attack;

import com.finderfeed.fdbosses.content.entities.BossSimpleProjectile;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class NetzachCaneVisualStrike extends BossSimpleProjectile implements IEntityWithComplexSpawn {

    public int fadeIn;
    public int fadeOut;
    public int stay;

    public static NetzachCaneVisualStrike summon(Level level, Vec3 position, Vec3 movement, int fadeIn, int stay, int fadeOut){
        var strike = new NetzachCaneVisualStrike(BossEntities.NETZACH_CANE_VISUAL_STRIKE.get(), level);
        strike.setPos(position);
        strike.fadeIn = fadeIn;
        strike.fadeOut = fadeOut;
        strike.stay = stay;
        strike.setDeltaMovement(movement);
        level.addFreshEntity(strike);
        return strike;
    }

    public NetzachCaneVisualStrike(EntityType<?> type, Level level){
        super(type, level);
        this.noCulling = true;
    }

    @Override
    public void tick(){
        if (this.hasReachedDestination()){
            this.setDeltaMovement(Vec3.ZERO);
        }
        super.tick();

        if (!this.level().isClientSide){
            if (this.hasReachedDestination() && this.reachedDestinationTicks > stay + fadeOut){
                this.discard();
            }
        }
    }

    @Override
    public void onBlockHit(BlockHitResult result){
        Vec3 position = result.getLocation();
        this.teleportTo(position.x, position.y, position.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.setReachedDestination(true);
    }

    @Override
    public void onEntityHit(Entity entity){
    }


    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.fadeIn);
        buffer.writeInt(this.fadeOut);
        buffer.writeInt(this.stay);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.fadeIn = additionalData.readInt();
        this.fadeOut = additionalData.readInt();
        this.stay = additionalData.readInt();
    }

}
