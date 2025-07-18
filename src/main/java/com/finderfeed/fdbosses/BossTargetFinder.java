package com.finderfeed.fdbosses;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class BossTargetFinder {

    public static <T extends Entity> List<T> getEntitiesInCylinder(Class<T> clazz, Level level, Vec3 cylinderStart, float cylinderHeight, float cylinderRadius){
        return getEntitiesInCylinder(clazz, level, cylinderStart, cylinderHeight, cylinderRadius, v -> true);
    }

    public static <T extends Entity> List<T> getEntitiesInCylinder(Class<T> clazz, Level level, Vec3 cylinderStart, float cylinderHeight, float cylinderRadius, Predicate<T> predicate){

        AABB box = new AABB(
                -cylinderRadius,0,-cylinderRadius,
                cylinderRadius,cylinderHeight,cylinderRadius
        ).move(cylinderStart);

        var entities = level.getEntitiesOfClass(clazz, box, entity->{

            Vec3 entityPos = entity.position();
            Vec3 b = entityPos.subtract(cylinderStart);
            double horizontalRadius = Math.sqrt(b.x * b.x + b.z * b.z);

            double h = b.y;

            return horizontalRadius <= cylinderRadius && (h >= 0 && h <= cylinderHeight) && predicate.test(entity);
        });

        return entities;
    }

    public static <T extends Entity> List<T> getEntitiesInSphere(Class<T> clazz, Level level, Vec3 center, float radius){
        return getEntitiesInSphere(clazz, level, center, radius, v -> true);
    }

    public static <T extends Entity> List<T> getEntitiesInSphere(Class<T> clazz, Level level, Vec3 center, float radius, Predicate<T> predicate){
        AABB box = new AABB(
                -radius,-radius,-radius,
                radius,radius,radius
        ).move(center);

        var entities = level.getEntitiesOfClass(clazz, box, entity->{

            Vec3 entityPos = entity.position();
            Vec3 b = entityPos.subtract(center);
            double d = b.length();

            return d <= radius && predicate.test(entity);
        });

        return entities;
    }

    public static <T extends Entity> List<T> getEntitiesInArc(Class<T> clazz, Level level, Vec3 start, Vec2 direction, float halfAngle, float cylinderHeight, float cylinderRadius){
        return getEntitiesInArc(clazz, level, start, direction, halfAngle, cylinderHeight, cylinderRadius);
    }

    public static <T extends Entity> List<T> getEntitiesInArc(Class<T> clazz, Level level, Vec3 start, Vec2 direction, float halfAngle, float cylinderHeight, float cylinderRadius, Predicate<T> predicate){

        var inCylinder = getEntitiesInCylinder(clazz, level, start, cylinderHeight, cylinderRadius, predicate);

        Iterator<T> entities = inCylinder.iterator();

        while (entities.hasNext()){

            var next = entities.next();

            var position = next.position();

            Vec3 horizontal = position.subtract(start).multiply(1,0,1).normalize();
            Vec3 dir = new Vec3(direction.x,0,direction.y);

            double angle = FDMathUtil.angleBetweenVectors(horizontal,dir);

            if (angle > halfAngle){
                entities.remove();
            }

        }

        return inCylinder;
    }

}
