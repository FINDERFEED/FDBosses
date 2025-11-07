package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttack;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile.JudgementBallProjectile;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class GeburahAttackFireDefaultProjectiles extends GeburahWeaponAttack {

    private int projectileFlyTime;
    private float radius;
    private float speed;

    public GeburahAttackFireDefaultProjectiles(GeburahEntity geburah, float radius, int projectileFlyTime, float speed) {
        super(geburah);
        this.radius = radius;
        this.projectileFlyTime = projectileFlyTime;
        this.speed = speed;
    }

    @Override
    public void onAttackStart() {
        geburah.getAnimationSystem().startAnimation(GeburahEntity.GEBURAH_CANNONS_LAYER, AnimationTicker.builder(BossAnims.GEBURAH_FIRE_CANNONS)
                        .setSpeed(speed)
                .build());
    }

    @Override
    public void tickAttack() {

        int attackTick = Math.round(BossAnims.GEBURAH_FIRE_CANNONS.get().getAnimTime() / speed * 0.6f);

        if (this.getCurrentTick() == attackTick){
            geburah.level().playSound(null, geburah.getX(),geburah.getY(), geburah.getZ(), BossSounds.GEBURAH_CANNON_SHOOT.get(), SoundSource.HOSTILE, 4f ,1f);
            for (var cannon : this.geburah.getCannonsPositionAndDirection()){

                Vec3 position = cannon.first.add(0,-0.25f,0);
                Vec3 direction = cannon.second;
                JudgementBallProjectile judgementBallProjectile = JudgementBallProjectile.summon(geburah.level(), new ProjectileMovementPath(projectileFlyTime, false)
                        .addPos(position)
                        .addPos(position.add(direction.scale(radius)))
                );

            }
        }
    }

    @Override
    public boolean hasEnded() {
        return this.getCurrentTick() > BossAnims.GEBURAH_FIRE_CANNONS.get().getAnimTime() / speed;
    }

    @Override
    public void onAttackEnd() {

    }

    @Override
    public boolean canBeChanged() {
        return false;
    }

}
