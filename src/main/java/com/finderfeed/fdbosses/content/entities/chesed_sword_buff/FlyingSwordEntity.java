package com.finderfeed.fdbosses.content.entities.chesed_sword_buff;

import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.AttributeUtil;

import java.util.*;

public class FlyingSwordEntity extends FDProjectile {

    public static int DELAY_UNTIL_LAUNCH = 15;

    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(FlyingSwordEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(FlyingSwordEntity.class, EntityDataSerializers.INT);

    private UUID target = null;

    public FlyingSwordEntity(EntityType<? extends FDProjectile> type, Level level) {
        super(type, level);
    }

    public static FlyingSwordEntity summonAtTarget(LivingEntity owner, LivingEntity target, ItemStack weapon){

        Level level = owner.level();

        FlyingSwordEntity flyingSwordEntity = new FlyingSwordEntity(BossEntities.FLYING_SWORD.get(), level);

        flyingSwordEntity.setItem(weapon);

        flyingSwordEntity.target = target.getUUID();

        Vec3 targetPos = target.position();

        Vec3 between = targetPos.subtract(owner.position()).multiply(1,0,1).normalize();

        float rndDistMod = 2 + level.random.nextFloat() * 2;

        float rndOffsNum = level.random.nextBoolean() ? 1 : -1;

        Vec3 randomOffset = between
                .multiply(rndDistMod,rndDistMod,rndDistMod)
                .yRot(
                        rndOffsNum * FDMathUtil.FPI / 2 + (-rndOffsNum) * FDMathUtil.FPI / 4
                )
                .add(0,1.5 + level.random.nextFloat() * 2,0);

        Vec3 resultPos = targetPos.add(randomOffset);

        flyingSwordEntity.setPos(resultPos);

        flyingSwordEntity.setOwner(owner);

        flyingSwordEntity.setTargetIdForClient(target);

        level.addFreshEntity(flyingSwordEntity);

        return flyingSwordEntity;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (this.tickCount >= 400) {
                this.remove(RemovalReason.DISCARDED);
            }
            this.handleTarget();
        }
    }

    private void handleTarget(){
        if (level() instanceof ServerLevel serverLevel){
            if (serverLevel.getEntity(target) instanceof LivingEntity livingTarget && livingTarget.isAlive() && this.getOwner() instanceof LivingEntity owner){
                this.setTargetIdForClient(livingTarget);

                if (tickCount > DELAY_UNTIL_LAUNCH * 0.75f) {
                    Vec3 thisPos = this.position();

                    Vec3 targetPos = this.getTargetPos(livingTarget, 0);

                    Vec3 between = targetPos.subtract(thisPos);

                    float speed = 0.75f;
                    Vec3 deltaMovement = between.normalize().multiply(speed,speed,speed);

                    this.setDeltaMovement(deltaMovement);

                    if (this.position().distanceTo(targetPos) <= 0.01f){
                        this.hurtTarget(owner, livingTarget);
                    }

                }


            }else{
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    public Vec3 getTargetPos(LivingEntity target, float pticks){
        return target.getPosition(pticks).add(0,target.getBbHeight()/2,0);
    }

    public void setTargetIdForClient(LivingEntity target){
        this.entityData.set(TARGET_ID, target.getId());
    }

    public int getTargetIdForClient(){
        return this.entityData.get(TARGET_ID);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!level().isClientSide && this.tickCount > DELAY_UNTIL_LAUNCH * 0.75f) {
            if (this.getOwner() instanceof LivingEntity owner && owner.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) && hitResult.getEntity() instanceof LivingEntity target){
                this.hurtTarget(owner,target);
            }
            this.remove(RemovalReason.DISCARDED);
        }

    }

    private void hurtTarget(LivingEntity owner, LivingEntity target){
        var attribute = owner.getAttribute(Attributes.ATTACK_DAMAGE);

        double base = attribute.getBaseValue();
        double damage;

        var map = AttributeUtil.getSortedModifiers(this.getItem(), EquipmentSlotGroup.MAINHAND);
        if (map.containsKey(Attributes.ATTACK_DAMAGE)){
            var attributes = map.get(Attributes.ATTACK_DAMAGE);

            var operationAttributes = modifierCollectionToOperationMap(attributes);

            for (var mod : operationAttributes.get(AttributeModifier.Operation.ADD_VALUE)){
                base += mod.amount();
            }

            damage = base;

            for (var mod : operationAttributes.get(AttributeModifier.Operation.ADD_MULTIPLIED_BASE)){
                damage += base * mod.amount();
            }

            for (var mod : operationAttributes.get(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)){
                damage *= 1 + mod.amount();
            }

            target.hurt(level().damageSources().mobAttack(owner),(float) damage);

        }
    }

    private Map<AttributeModifier.Operation, List<AttributeModifier>> modifierCollectionToOperationMap(Collection<AttributeModifier> collection){

        Map<AttributeModifier.Operation, List<AttributeModifier>> operationListMap = new LinkedHashMap<>();
        operationListMap.put(AttributeModifier.Operation.ADD_VALUE,new ArrayList<>());
        operationListMap.put(AttributeModifier.Operation.ADD_MULTIPLIED_BASE,new ArrayList<>());
        operationListMap.put(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,new ArrayList<>());

        for (var mod : collection){
            operationListMap.get(mod.operation()).add(mod);
        }

        return operationListMap;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {

    }

    public ItemStack getItem(){
        return this.entityData.get(ITEM);
    }

    public void setItem(ItemStack itemStack){
        this.entityData.set(ITEM,itemStack);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder b) {
        b.define(ITEM, Items.WOODEN_SWORD.getDefaultInstance());
        b.define(TARGET_ID, 0);
    }

    @Override
    protected void checkInsideBlocks() {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {

        if (tag.contains("item")) {
            Tag t = tag.get("item");
            var opt = ItemStack.parse(level().registryAccess(),t);
            if (opt.isPresent()) {
                this.setItem(opt.get());
            }
        }

        if (tag.contains("target")){
            this.target = tag.getUUID("target");
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        Tag t = this.getItem().save(level().registryAccess());
        tag.put("item",t);

        if (target != null){
            tag.putUUID("target",target);
        }
    }
}
