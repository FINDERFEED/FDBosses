package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_mini_ray;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedRayOptions;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;


public class ChesedMiniRay extends Entity implements AutoSerializable {

    public static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(ChesedMiniRay.class, EntityDataSerializers.INT);

    public static final int TIME_UNTIL_ATTACK = 25;
    public static final int ATTACK_TIME = 8;

    private ItemStack item;

    @SerializableField
    private UUID target;

    @SerializableField
    private UUID owner;

    public static ChesedMiniRay summon(Level level, Vec3 pos, LivingEntity target, ItemStack item, LivingEntity owner){
        ChesedMiniRay chesedMiniRay = new ChesedMiniRay(BossEntities.CHESED_MINI_RAY.get(), level);
        chesedMiniRay.item = item;
        chesedMiniRay.target = target.getUUID();
        chesedMiniRay.owner = owner.getUUID();
        chesedMiniRay.entityData.set(TARGET, target.getId());
        chesedMiniRay.setPos(pos);
        level.addFreshEntity(chesedMiniRay);
        return chesedMiniRay;
    }

    public ChesedMiniRay(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.checkRemoved();
            if (tickCount == TIME_UNTIL_ATTACK){
                this.attack();
            }
        }else{

            if (tickCount == TIME_UNTIL_ATTACK){
                LivingEntity target = this.getTarget();
                if (target != null){
                    this.attackParticles(this.getTargetPos(target,0));
                }
            }

        }
    }

    private void attackParticles(Vec3 basePos){

        LivingEntity target = this.getTarget();
        if (target == null) return;

        Vec3 pos = this.position();
        Vec3 targetPos = this.getTargetPos(target,0);

        Vec3 dir = pos.subtract(targetPos);

        Matrix4f mat = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mat, dir);

        int countOnCircle = 16;
        float angle = FDMathUtil.FPI * 2 / countOnCircle;

        int count = 16;

        for (int i = 0; i < countOnCircle; i++){

            for (int k = 0; k < count; k++){

                float currentAngle = i * angle + random.nextFloat() * angle;

                Vec3 particleDirection = new Vec3(1,0,0).yRot(currentAngle);

                float p = k / (count - 1f);

                float size = 0.25f + p * 0.25f;

                float speedMod = (1 - p) * (2f + random.nextFloat() * 0.5f);

                BallParticleOptions options = BallParticleOptions.builder()
                        .friction(0.5f)
                        .size(size)
                        .color(100 + random.nextInt(50), 255, 255)
                        .scalingOptions(3,0,10)
                        .build();

                Vector3f v = new Vector3f(
                        (float) particleDirection.x,
                        (float) 3,
                        (float) particleDirection.z
                ).normalize();

                float offsetRandom = random.nextFloat() * 0.5f;

                Vector3f v2 = new Vector3f(
                        (float) particleDirection.x * offsetRandom,
                        (float) 0,
                        (float) particleDirection.z * offsetRandom
                );

                Vector3f finalSpeed = mat.transformDirection(v);
                Vector3f finalPosOffset = mat.transformDirection(v2);

                Vec3 ppos = basePos.add(finalPosOffset.x,finalPosOffset.y,finalPosOffset.z);


                level().addParticle(options, true,
                        ppos.x,ppos.y,ppos.z,finalSpeed.x * speedMod,finalSpeed.y * speedMod,finalSpeed.z * speedMod
                );

            }
        }

    }

    public void attack(){
        if (level() instanceof ServerLevel serverLevel && serverLevel.getEntity(owner) instanceof LivingEntity owner) {
            LivingEntity target = this.getTarget();
            if (target == null) return;

            Vec3 end = this.getTargetPos(target, 0);

            ChesedRayOptions options = ChesedRayOptions.builder()
                    .time(2,2,7)
                    .lightningColor(90, 180, 255)
                    .color(100, 255, 255)
                    .end(end)
                    .width(0.6f)
                    .build();
            FDLibCalls.sendParticles((ServerLevel) level(),options,this.position(),120);

            FDLibCalls.sendParticles((ServerLevel) level(),BallParticleOptions.builder()
                    .size(5f)
                    .scalingOptions(1,0,2)
                    .color(150,230,255)
                    .build(),end,120);

            PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                    .frequency(5)
                    .amplitude(5f)
                    .inTime(0)
                    .stayTime(0)
                    .outTime(5)
                    .build(),end,30);

            this.hurtTarget(owner, target);
        }
    }

    public Vec3 getTargetPos(LivingEntity target, float pticks){
        return target.getPosition(pticks).add(0,target.getBbHeight() / 2,0);
    }

    public void checkRemoved(){
        if (level() instanceof ServerLevel serverLevel) {

            if (tickCount > TIME_UNTIL_ATTACK + ATTACK_TIME){
                this.setRemoved(RemovalReason.DISCARDED);
            }

            if (target == null || owner == null) this.setRemoved(RemovalReason.DISCARDED);

            if (serverLevel.getEntity(owner) == null) {
                this.setRemoved(RemovalReason.DISCARDED);
            }else if (!(serverLevel.getEntity(target) instanceof LivingEntity living)){
                this.setRemoved(RemovalReason.DISCARDED);
            }else{
                this.entityData.set(TARGET, living.getId());
            }

        }
    }

    public LivingEntity getTarget(){
        if (level().isClientSide){
            if (level().getEntity(this.entityData.get(TARGET)) instanceof LivingEntity living){
                return living;
            }
        }else{
            if (((ServerLevel)level()).getEntity(target) instanceof LivingEntity living){
                return living;
            }
        }
        return null;
    }


    private void hurtTarget(LivingEntity owner, LivingEntity target){
        var attribute = owner.getAttribute(Attributes.ATTACK_DAMAGE);

        double base = attribute.getBaseValue();
        double damage;

        var map = AttributeUtil.getSortedModifiers(item, EquipmentSlotGroup.MAINHAND);
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

            float dmg = BossConfigs.BOSS_CONFIG.get().itemConfig.flyingSwordDamagePercent / 100f;

            DamageSource damageSource = level().damageSources().mobAttack(owner);

            damage = EnchantmentHelper.modifyDamage((ServerLevel) level(), item, target, damageSource, (float) damage);

            damage *= dmg;

            target.setRemainingFireTicks(0);

            target.invulnerableTime = 0;
            if (target.hurt(damageSource,(float) damage)){
                int duration = BossConfigs.BOSS_CONFIG.get().itemConfig.flyingSwordShockDuration;
                target.addEffect(new MobEffectInstance(BossEffects.SHOCKED,duration,0));
                EnchantmentHelper.doPostAttackEffects((ServerLevel) level(), target, damageSource);
                target.invulnerableTime = 0;
            }

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
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        data.define(TARGET, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.item = ItemStack.parse(this.level().registryAccess(), tag.get("item")).get();
        this.autoLoad(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Tag item = this.item.save(this.level().registryAccess());
        tag.put("item", item);
        this.autoSave(tag);
    }

}
