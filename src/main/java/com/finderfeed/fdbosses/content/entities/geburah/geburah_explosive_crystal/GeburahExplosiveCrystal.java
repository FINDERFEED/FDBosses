package com.finderfeed.fdbosses.content.entities.geburah.geburah_explosive_crystal;

import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.trails.FDTrailDataGenerator;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.List;
import java.util.UUID;

public class GeburahExplosiveCrystal extends FDEntity implements AutoSerializable {

    @SerializableField
    private UUID playerToFlyTo;

    public FDTrailDataGenerator<GeburahExplosiveCrystal> trail;

    public static GeburahExplosiveCrystal summon(Vec3 pos, Player player){
        GeburahExplosiveCrystal crystal = new GeburahExplosiveCrystal(BossEntities.GEBURAH_EXPLOSIVE_CRYSTAL.get(), player.level());

        crystal.playerToFlyTo = player.getUUID();

        Vec3 targetPos = crystal.getTargetPos(player);

        Vec3 between = pos.subtract(targetPos).normalize();

        Vec3 initialSpeed = between.add(
                player.getRandom().nextFloat() * 0.1 - 0.05,
                player.getRandom().nextFloat() * 0.1 - 0.05,
                player.getRandom().nextFloat() * 0.1 - 0.05
        ).normalize();

        crystal.setDeltaMovement(initialSpeed.scale(0.25f));

        crystal.setPos(pos);

        player.level().addFreshEntity(crystal);
        return crystal;
    }

    public GeburahExplosiveCrystal(EntityType<?> type, Level level) {
        super(type, level);
        if (level.isClientSide){
            trail = new FDTrailDataGenerator<>(GeburahExplosiveCrystal::getPosition, 10, 0.01f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.flyToTargetPlayer();
            this.setPos(this.position().add(this.getDeltaMovement()));
        }else{
            this.setPos(this.position().add(this.getDeltaMovement()));
            trail.tick(this);
        }
    }

    public void flyToTargetPlayer(){

        var player = this.getTargetPlayer();
        if (player == null){
            this.setRemoved(RemovalReason.DISCARDED);
        }

        Vec3 targetPos = this.getTargetPos(player);

        if (this.position().distanceTo(targetPos) < 0.5){
            this.putCrystalInPlayersInventory(player);
            this.remove(RemovalReason.DISCARDED);
        }

        float p = FDEasings.easeOut(Mth.clamp(this.tickCount / 100f, 0, 1));

        Vec3 currentSpeed = this.getDeltaMovement();

        Vec3 targetSpeed = targetPos.subtract(this.position());


        Vector3d currentSpeedv = new Vector3d(
                currentSpeed.x,currentSpeed.y,currentSpeed.z
        ).normalize();

        Vector3d targetSpeedv = new Vector3d(
                targetSpeed.x,targetSpeed.y,targetSpeed.z
        ).normalize();

        Vector3d axis = currentSpeedv.cross(targetSpeedv, new Vector3d());


        double angleBetween = currentSpeedv.angle(targetSpeedv);

        currentSpeedv.rotateAxis(angleBetween * p,axis.x,axis.y,axis.z);

        float spMod = 1f;

        this.setDeltaMovement(currentSpeedv.x * spMod,currentSpeedv.y * spMod,currentSpeedv.z * spMod);

    }

    public Player getTargetPlayer(){
        if (this.playerToFlyTo == null){
            return null;
        }else{
            return ((ServerLevel)level()).getPlayerByUUID(playerToFlyTo);
        }
    }

    public void putCrystalInPlayersInventory(Player player){

        Inventory inventory = player.getInventory();

        List<ItemStack> items = inventory.items;

        int randomSlot = random.nextInt(items.size());

        ItemStack cr = items.get(randomSlot);

        int tries = 100;

        while (cr.is(BossItems.GEBURAH_EXPLOSIVE_CRYSTAL.get()) || tries > 0){
            randomSlot = random.nextInt(items.size());
            cr = items.get(randomSlot);
            tries--;
        }

        var itemInSlot = items.get(randomSlot);

        items.set(randomSlot, BossItems.GEBURAH_EXPLOSIVE_CRYSTAL.get().getDefaultInstance());

        if (!itemInSlot.isEmpty()){

            int freeSlot = inventory.getFreeSlot();

            if (freeSlot != -1){
                items.set(freeSlot, itemInSlot.copy());
            }else{
                ItemEntity item = new ItemEntity(level(), player.getX(),player.getY() + player.getBbHeight() / 2,player.getZ(), itemInSlot.copy());
                Vec3 speed = player.getLookAngle().scale(0.5f);
                item.setDefaultPickUpDelay();
                item.setDeltaMovement(speed);
                level().addFreshEntity(item);
            }

        }

        inventory.setChanged();

    }






    public Vec3 getTargetPos(Player player){
        return player.position().add(0,player.getBbHeight() / 2,0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

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
