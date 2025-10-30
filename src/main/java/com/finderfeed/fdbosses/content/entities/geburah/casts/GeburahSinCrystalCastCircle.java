package com.finderfeed.fdbosses.content.entities.geburah.casts;

import com.finderfeed.fdbosses.content.entities.geburah.geburah_explosive_crystal.GeburahSinCrystal;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class GeburahSinCrystalCastCircle extends GeburahCastingCircle implements AutoSerializable {

    @SerializableField
    private UUID uuid;

    public GeburahSinCrystalCastCircle(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public static void summon(Level level, Vec3 pos, Vec3 direction, Player player){
        GeburahSinCrystalCastCircle castCircle = new GeburahSinCrystalCastCircle(BossEntities.GEBURAH_CASTING_CIRCLE_SIN_CRYSTAL.get(), level);

        castCircle.setPos(pos);
        castCircle.setDirection(direction);
        castCircle.setCastDuration(20);
        castCircle.getEntityData().set(COLOR, 0xffaaddff);
        castCircle.uuid = player.getUUID();

        level.addFreshEntity(castCircle);
    }

    @Override
    public void cast() {
        ServerLevel serverLevel = (ServerLevel) level();
        if (uuid != null && serverLevel.getPlayerByUUID(uuid) instanceof ServerPlayer serverPlayer) {
            GeburahSinCrystal.summon(this.position(), serverPlayer, this.getCastDirection());
        }
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
