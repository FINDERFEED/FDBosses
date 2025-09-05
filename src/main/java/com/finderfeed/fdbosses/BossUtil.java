package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.config.BossConfig;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.packets.PosLevelEventPacket;
import com.finderfeed.fdlib.util.FDUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class BossUtil {


    public static final TargetingConditions ALL = TargetingConditions.forNonCombat().selector(p->true).ignoreLineOfSight().ignoreInvisibilityTesting();

    public static final int CHESED_GET_BLOCKS_FROM_EARTH_EVENT = 1;
    public static final int RADIAL_EARTHQUAKE_PARTICLES = 2;
    public static final int ROCKFALL_PARTICLES = 3;
    public static final int CHESED_RAY_EXPLOSION = 4;
    public static final int CHESED_RAY_ATTACK_SMOKE = 5;
    public static final int CHESED_BOOM_PARTICLES = 6;
    public static final int MALKUTH_CANNON_SHOOT = 7;
    public static final int MALKUTH_SWORD_CHARGE_PARTICLES = 8;
    public static final int MALKUTH_FLOAT_PARTICLES = 9;
    public static final int MALKUTH_FIREBALL_EXPLODE = 10;
    public static final int MALKUTH_VOLCANO_ERRUPTION = 11;
    public static final int MALKUTH_SWORD_INSERT_PARTICLES = 12;
    public static final int MALKUTH_PLAYER_FIREBALL_EXPLODE = 13;

    public static Vec3 matTransformDirectionVec3(Matrix4f mat, Vec3 v){
        Vector3f v1 = mat.transformDirection(
                (float)v.x,
                (float)v.y,
                (float)v.z,
                new Vector3f()
        );

        return new Vec3(v1.x,v1.y,v1.z);
    }

    /**
     * Air Friction? What?
     * We don't do that here
     * *Insert Black Panther meme*
     */
    public static Vec3 calculateMortarProjectileVelocity(Vec3 startPos, Vec3 endPos, double gravity, int tickTravelTime){

        Vec3 between = endPos.subtract(startPos);

        double horizontalDistance = Math.sqrt(between.x * between.x + between.z * between.z);

        double d = between.y;


        double horizontalSpeed = horizontalDistance / tickTravelTime;

        //d = vt + at^2 * 1/2
        //v = ((at^2 * 1/2) - d) / (-t)

        double verticalSpeed = ((gravity * tickTravelTime * tickTravelTime / 2) - d) / (-tickTravelTime);


        Vec3 result = between.multiply(1,0,1).normalize().multiply(horizontalSpeed,0,horizontalSpeed).add(0,verticalSpeed,0);

        return result;
    }

    public static float transformDamage(Level level, float damage){
        Difficulty difficulty = level.getDifficulty();
        switch (difficulty){
            case EASY -> {
                return BossConfigs.BOSS_CONFIG.get().easyDifficultyBossDamageMultiplier * damage;
            }
            case NORMAL -> {
                return BossConfigs.BOSS_CONFIG.get().normalDifficultyBossDamageMultiplier * damage;
            }
            case HARD -> {
                return BossConfigs.BOSS_CONFIG.get().hardDifficultyBossDamageMultiplier * damage;
            }
            case PEACEFUL -> {
                return BossConfigs.BOSS_CONFIG.get().peacefulDifficultyBossDamageMuliplier * damage;
            }
            default -> {
                return BossConfigs.BOSS_CONFIG.get().hardDifficultyBossDamageMultiplier * damage;
            }
        }
    }

    public static double getToolDamage(LivingEntity owner, Entity target, ItemStack itemStack){
        var attribute = owner.getAttribute(Attributes.ATTACK_DAMAGE);

        double base = attribute.getBaseValue();
        double damage;

        var map = AttributeUtil.getSortedModifiers(itemStack, EquipmentSlotGroup.MAINHAND);
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

            DamageSource damageSource = owner.level().damageSources().mobAttack(owner);

            damage = EnchantmentHelper.modifyDamage((ServerLevel) owner.level(), itemStack, target, damageSource, (float) damage);

            return damage;
        }

        return 0;
    }

    private static Map<AttributeModifier.Operation, List<AttributeModifier>> modifierCollectionToOperationMap(Collection<AttributeModifier> collection){

        Map<AttributeModifier.Operation, List<AttributeModifier>> operationListMap = new LinkedHashMap<>();
        operationListMap.put(AttributeModifier.Operation.ADD_VALUE,new ArrayList<>());
        operationListMap.put(AttributeModifier.Operation.ADD_MULTIPLIED_BASE,new ArrayList<>());
        operationListMap.put(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,new ArrayList<>());

        for (var mod : collection){
            operationListMap.get(mod.operation()).add(mod);
        }

        return operationListMap;
    }

    public static Predicate<Entity> entityInVerticalRadiusPredicate(Vec3 pos,float radius){
        return (entity)->{
            double x = pos.x - entity.getX();
            double z = pos.z - entity.getZ();
            return x * x + z * z <= radius * radius;
        };
    }

    public static void malkuthSwordsInsertParticles(ServerLevel serverLevel, Vec3 pos, double radius, int malkuthEntityId){
        posEvent(serverLevel, pos, MALKUTH_SWORD_INSERT_PARTICLES, malkuthEntityId, radius);
    }

    public static void volcanoErruptionParticles(ServerLevel serverLevel, Vec3 pos, int radius16, double sendRadius){
        posEvent(serverLevel,pos,MALKUTH_VOLCANO_ERRUPTION,radius16, sendRadius);
    }

    public static void malkuthFireballExplosionParticles(ServerLevel serverLevel, Vec3 pos, MalkuthAttackType type){
        posEvent(serverLevel, pos, MALKUTH_FIREBALL_EXPLODE, type.isFire() ? 0 : 1, 60);
    }

    public static void malkuthPlayerFireballExplosionParticles(ServerLevel serverLevel, Vec3 pos, MalkuthAttackType type){
        posEvent(serverLevel, pos, MALKUTH_PLAYER_FIREBALL_EXPLODE, type.isFire() ? 0 : 1, 60);
    }

    public static void malkuthFloatParticles(ServerLevel serverLevel, MalkuthEntity malkuthEntity){
        posEvent(serverLevel, malkuthEntity.position(), MALKUTH_FLOAT_PARTICLES, malkuthEntity.getId(), 60);
    }

    public static void malkuthCannonShoot(ServerLevel serverLevel, MalkuthAttackType malkuthAttackType, Vec3 pos, Vec3 direction, double radius){
        int data = FDUtil.encodeDirection(direction);
        data <<= 1;
        if (malkuthAttackType.isFire()){
            data += 0b1;
        }
        posEvent(serverLevel,pos,MALKUTH_CANNON_SHOOT, data, radius);
    }

    public static void malkuthSwordChargeParticles(ServerLevel serverLevel, MalkuthAttackType malkuthAttackType, MalkuthEntity malkuthEntity, double radius){
        Vec3 yesIEncodeEnumIntoVec3DontJudgeMe = malkuthAttackType.isFire() ? new Vec3(1,0,0) : new Vec3(-1,0,0);
        yesIEncodeEnumIntoVec3DontJudgeMe = yesIEncodeEnumIntoVec3DontJudgeMe.add(malkuthEntity.position());
        posEvent(serverLevel, yesIEncodeEnumIntoVec3DontJudgeMe, MALKUTH_SWORD_CHARGE_PARTICLES, malkuthEntity.getId(), radius);
    }

    public static void chesedRaySmoke(ServerLevel level,Vec3 pos,Vec3 direction,double radius){
        posEvent(level,pos,CHESED_RAY_ATTACK_SMOKE, FDUtil.encodeDirection(direction),radius);
    }

    public static void chesedBoomParticles(ServerLevel level,Vec3 pos,int radiusFromCenter,double packetRadius){
        posEvent(level,pos,CHESED_BOOM_PARTICLES, radiusFromCenter,packetRadius);
    }

    public static void chesedRayExplosion(ServerLevel level,Vec3 pos,Vec3 direction,double radius,int particlesCount,float sizeModifier){
        if (particlesCount > 0xf) throw new RuntimeException("Cannot encode more than 16 particles count");
        if (sizeModifier > 1) throw new RuntimeException("Cannot encode size modifier > 1");

        direction = direction.normalize();
        int dx = (int)Math.round((direction.x + 1) / 2 * 0xff);
        int dy = (int)Math.round((direction.y + 1) / 2 * 0xff);
        int dz = (int)Math.round((direction.z + 1) / 2 * 0xff);

        int size = Math.round(sizeModifier * 0xf);


        int data = 0x00000000;
        data |= size << 28;
        data |= particlesCount << 24;
        data |= dx << 16;
        data |= dy << 8;
        data |= dz;

        posEvent(level,pos,CHESED_RAY_EXPLOSION,data,radius);

    }

    public static void posEvent(ServerLevel level, Vec3 pos, int event,int data,double radius){
        PacketDistributor.sendToPlayersNear(level,null,pos.x,pos.y,pos.z,radius,new PosLevelEventPacket(pos,event,data));
    }

    //easings.net
    public static float easeInBack(float x){
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return c3 * x * x * x - c1 * x * x;
    }

    public static boolean itemContainsModifierForAttribute(ItemStack itemStack, Holder<Attribute> attributeHolder){
        ItemAttributeModifiers modifiers = itemStack.getAttributeModifiers();
        for (var modifier : modifiers.modifiers()){
            if (modifier.attribute().value().equals(attributeHolder.value())){
                return true;
            }
        }
        return false;
    }


    public static class StructureTags {

        public static final TagKey<Structure> EYE_OF_CHESED_LOCATED = create("eye_of_chesed_located");
        public static final TagKey<Structure> EYE_OF_MALKUTH_LOCATED = create("eye_of_malkuth_located");

        private static TagKey<Structure> create(String id) {
            return TagKey.create(Registries.STRUCTURE, FDBosses.location(id));
        }
    }

}
