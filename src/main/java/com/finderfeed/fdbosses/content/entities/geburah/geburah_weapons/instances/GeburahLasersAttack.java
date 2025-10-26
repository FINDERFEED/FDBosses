package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttack;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GeburahLasersAttack extends GeburahWeaponAttack {

    public GeburahLasersAttack(GeburahEntity geburah) {
        super(geburah);
    }

    @Override
    public void onAttackStart() {
        BossUtil.geburahWeaponsStartLaser((ServerLevel) geburah.level(), geburah.position(), 120, geburah);
    }

    @Override
    public void tickAttack() {
        this.geburah.setLaserVisualsState(true);

        var rotationController = geburah.getWeaponRotationController();
        List<Pair<Vec3, Vec3>> rotationOld = this.geburah.getCannonsPositionAndDirection(0);
        List<Pair<Vec3, Vec3>> rotationNew = this.geburah.getCannonsPositionAndDirection(1);

        for (int i = 0; i < rotationOld.size(); i++){

            var oldR = rotationOld.get(i);
            var newR = rotationNew.get(i);

            Vec3 oldDirection = oldR.second;
            Vec3 newDirection = newR.second;

            Vec3 centeredDirection = oldDirection.add(newDirection).normalize();

            float angle = (float) FDMathUtil.angleBetweenVectors(oldDirection,newDirection);

            var targets = BossTargetFinder.getEntitiesInArc(LivingEntity.class, geburah.level(), geburah.position().add(0,-0.1,0),
                    new Vec2((float)centeredDirection.x,(float)centeredDirection.z),
                    angle * 2,3,GeburahEntity.ARENA_RADIUS,livingEntity -> {
                        return !(livingEntity instanceof GeburahEntity);
            });

            for (var target : targets){
                target.hurt(geburah.level().damageSources().generic(),1);
            }


        }

    }

    @Override
    public boolean hasEnded() {
        return false;
    }

    @Override
    public void onAttackEnd() {
        this.geburah.setLaserVisualsState(false);
    }

    @Override
    public boolean canBeChanged() {
        return true;
    }

}
