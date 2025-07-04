package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.config.BossConfig;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.packets.PosLevelEventPacket;
import com.finderfeed.fdlib.util.FDUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

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

    public static Predicate<Entity> entityInVerticalRadiusPredicate(Vec3 pos,float radius){
        return (entity)->{
            double x = pos.x - entity.getX();
            double z = pos.z - entity.getZ();
            return x * x + z * z <= radius * radius;
        };
    }

    public static void malkuthCannonShoot(ServerLevel serverLevel, MalkuthAttackType malkuthAttackType, Vec3 pos, Vec3 direction, double radius){
        int data = FDUtil.encodeDirection(direction);
        data <<= 1;
        if (malkuthAttackType.isFire()){
            data += 0b1;
        }
        posEvent(serverLevel,pos,MALKUTH_CANNON_SHOOT, data, radius);
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

        private static TagKey<Structure> create(String id) {
            return TagKey.create(Registries.STRUCTURE, FDBosses.location(id));
        }
    }

}
