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

    public static <T extends Entity> List<T> getEntitiesInHorizontalBox(Class<T> clazz, Level level, Vec3 start, Vec2 direction, float length, float width, float height, Predicate<T> predicate){

        direction = direction.normalized();

        Vec3 sideOffset = new Vec3(direction.x * width / 2,0,direction.y * width / 2).yRot(FDMathUtil.FPI / 2);

        Vec3 lengthOffset = new Vec3(direction.x * length, 0, direction.y * length);

        Vec3 p1 = start.add(sideOffset);
        Vec3 p2 = start.add(sideOffset.reverse());

        Vec3 p3 = start.add(lengthOffset);
        Vec3 p4 = start.add(lengthOffset);

        Vec3 p5 = start.add(sideOffset).add(0, height, 0);
        Vec3 p6 = start.add(sideOffset.reverse()).add(0, height, 0);

        Vec3 p7 = start.add(lengthOffset).add(0, height, 0);
        Vec3 p8 = start.add(lengthOffset).add(0, height, 0);

        double minX = Math.min(p1.x, Math.min(p2.x, Math.min(p3.x, Math.min(p4.x, Math.min(p5.x, Math.min(p6.x, Math.min(p7.x,p8.x)))))));
        double minY = Math.min(p1.y, Math.min(p2.y, Math.min(p3.y, Math.min(p4.y, Math.min(p5.y, Math.min(p6.y, Math.min(p7.y,p8.y)))))));
        double minZ = Math.min(p1.z, Math.min(p2.z, Math.min(p3.z, Math.min(p4.z, Math.min(p5.z, Math.min(p6.z, Math.min(p7.z,p8.z)))))));


        double maxX = Math.max(p1.x, Math.max(p2.x, Math.max(p3.x, Math.max(p4.x, Math.max(p5.x, Math.max(p6.x, Math.max(p7.x,p8.x)))))));
        double maxY = Math.max(p1.y, Math.max(p2.y, Math.max(p3.y, Math.max(p4.y, Math.max(p5.y, Math.max(p6.y, Math.max(p7.y,p8.y)))))));
        double maxZ = Math.max(p1.z, Math.max(p2.z, Math.max(p3.z, Math.max(p4.z, Math.max(p5.z, Math.max(p6.z, Math.max(p7.z,p8.z)))))));

        AABB searchBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        var list = level.getEntitiesOfClass(clazz, searchBox, predicate);

        Iterator<T> iterator = list.iterator();

        double angle = Math.atan2(direction.y, direction.x);

        while (iterator.hasNext()){

            T entity = iterator.next();

            Vec3 pos = entity.position();

            Vec3 horizontal = pos.multiply(1,0,1)
                    .subtract(start.multiply(1,0,1));

            Vec3 rotated = horizontal.yRot((float)angle);

            if ((rotated.x > length || rotated.x < 0) || (pos.y > start.y + height || pos.y < start.y) || (Math.abs(rotated.z) > width / 2)){
                iterator.remove();
            }

        }

        return list;
    }


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
//            Vec3 b = entityPos.subtract(cylinderStart);
//            double horizontalRadius = Math.sqrt(b.x * b.x + b.z * b.z);
//
//            double h = b.y;

            return isPointInCylinder(entityPos, cylinderStart, cylinderHeight, cylinderRadius) && predicate.test(entity);
        });

        return entities;
    }


    public static boolean isPointInCylinder(Vec3 point, Vec3 cylinderStart, float cylinderHeight, float cylinderRadius){
        Vec3 entityPos = point;
        Vec3 b = entityPos.subtract(cylinderStart);
        double horizontalRadius = Math.sqrt(b.x * b.x + b.z * b.z);

        double h = b.y;

        return horizontalRadius <= cylinderRadius && (h >= 0 && h <= cylinderHeight);
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
        return getEntitiesInArc(clazz, level, start, direction, halfAngle, cylinderHeight, cylinderRadius,v->true);
    }

    public static <T extends Entity> List<T> getEntitiesInArc(Class<T> clazz, Level level, Vec3 start, Vec2 direction, float angle, float cylinderHeight, float cylinderRadius, Predicate<T> predicate){

        var inCylinder = getEntitiesInCylinder(clazz, level, start, cylinderHeight, cylinderRadius, predicate);

        Iterator<T> entities = inCylinder.iterator();

        while (entities.hasNext()){

            var next = entities.next();

            var position = next.position();

            Vec3 horizontal = position.subtract(start).multiply(1,0,1).normalize();
            Vec3 dir = new Vec3(direction.x,0,direction.y);

            double angleBetween = FDMathUtil.angleBetweenVectors(horizontal,dir);

            if (angleBetween > angle/2){
                entities.remove();
            }

        }

        return inCylinder;
    }

}
