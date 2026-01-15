package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttack;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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
        geburah.level().playSound(null, geburah.getX(),geburah.getY(),geburah.getZ(), BossSounds.GEBURAH_RAY_SHOT.get(), SoundSource.HOSTILE, 4f ,1f);
        BossUtil.geburahWeaponsStartLaser((ServerLevel) geburah.level(), geburah.position(), 120, geburah);
    }

    @Override
    public void tickAttack() {
        this.geburah.setLaserVisualsState(true);

        if (this.getCurrentTick() % 2 == 0){
            geburah.level().playSound(null, geburah.getX(),geburah.getY(),geburah.getZ(), BossSounds.GEBURAH_RAY_LOOP.get(), SoundSource.HOSTILE, 4f ,1f);
        }

        List<Pair<Vec3, Vec3>> rotationOld = this.geburah.getCannonsPositionAndDirection(0);
        List<Pair<Vec3, Vec3>> rotationNew = this.geburah.getCannonsPositionAndDirection(1);

        for (int i = 0; i < rotationOld.size(); i++){

            var oldR = rotationOld.get(i);
            var newR = rotationNew.get(i);

            Vec3 oldDirection = oldR.second;
            Vec3 newDirection = newR.second;

            Vec3 centeredDirection = oldDirection.add(newDirection).normalize();

            float angle = (float) FDMathUtil.angleBetweenVectors(oldDirection,newDirection);

            if (Float.isNaN(angle) || angle == 0){
                angle = 0.05f;
            }

            var targets = BossTargetFinder.getEntitiesInArc(LivingEntity.class, geburah.level(), geburah.position().add(0,-0.1,0),
                    new Vec2((float)centeredDirection.x,(float)centeredDirection.z),
                    angle * 2,2,GeburahEntity.ARENA_RADIUS,livingEntity -> {
                        return !(livingEntity instanceof GeburahEntity);
            });


            float damage = BossUtil.transformDamage(geburah.level(), BossConfigs.BOSS_CONFIG.get().geburahConfig.rotatingLaserAttackDamage);

            for (var target : targets){
                target.hurt(BossDamageSources.GEBURAH_LASER_STRIKE_SOURCE,damage);
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
