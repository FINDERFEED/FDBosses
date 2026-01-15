package com.finderfeed.fdbosses.content.entities.geburah.casts;

import com.finderfeed.fdbosses.content.entities.geburah.chain_trap.ChainTrapSummonProjectile;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GeburahChainTrapCastCircle extends GeburahCastingCircle implements AutoSerializable {

    @SerializableField
    private Vec3 projectileEndPos;

    public static void summon(Level level, Vec3 pos, Vec3 direction, Vec3 projectileEndPos){

        GeburahChainTrapCastCircle castCircle = new GeburahChainTrapCastCircle(BossEntities.GEBURAH_CASTING_CIRCLE_CHAIN_TRAP.get(), level);

        castCircle.setPos(pos);
        castCircle.projectileEndPos = projectileEndPos;
        castCircle.setDirection(direction);
        castCircle.setCastDuration(10);
        castCircle.getEntityData().set(COLOR, 0xffaaddff);

        level.addFreshEntity(castCircle);

    }

    public GeburahChainTrapCastCircle(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void cast() {
        if (projectileEndPos == null){
            this.discard();
        }else {
            ChainTrapSummonProjectile.summon(level(), this.position(), projectileEndPos);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

}
