package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.packets.PosLevelEventPacket;
import com.finderfeed.fdlib.util.FDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
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

    public static Predicate<Entity> entityInVerticalRadiusPredicate(Vec3 pos,float radius){
        return (entity)->{
            double x = pos.x - entity.getX();
            double z = pos.z - entity.getZ();
            return x * x + z * z <= radius * radius;
        };
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

}
