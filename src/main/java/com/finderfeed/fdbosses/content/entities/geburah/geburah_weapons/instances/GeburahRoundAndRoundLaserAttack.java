package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttack;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GeburahRoundAndRoundLaserAttack extends GeburahWeaponAttack {

    private boolean forward;

    private boolean circleComplete = false;

    private float rotationSnapshot;
    private int cannonId;
    private Player targetPlayer;

    private int fireShotTime = -1;
    private int endingRotation = -1;

    private Vec3 cachedTargetPos;

    public GeburahRoundAndRoundLaserAttack(GeburahEntity geburah, boolean forward) {
        super(geburah);
        this.forward = forward;
    }

    @Override
    public void onAttackStart() {
        this.rotationSnapshot = this.geburah.getWeaponRotationController().getCurrentRotation();
        this.targetPlayer = geburah.pickRandomCombatant();
        this.cannonId = this.getNearestCannonToPlayer(targetPlayer);
        this.geburah.laserAttackPreparator.launchPreparation(20);
    }

    @Override
    public void tickAttack() {



        if (fireShotTime == -1){

            float speed = 10f;

            if (this.hasFinishedCircle()){
                circleComplete = true;


                Vec3 targetPos = this.getPlayerTargetPos();
                if (cachedTargetPos != null){
                    targetPos = cachedTargetPos;
                }

                var cannonData = this.geburah.getCannonsPositionAndDirection().get(cannonId);

                var cannonPos = cannonData.first;
                var cannonDirection = cannonData.second;


                ((ServerLevel)geburah.level()).sendParticles(BallParticleOptions.builder().stay(1).build(),cannonPos.x + cannonDirection.x,cannonPos.y,cannonPos.z + cannonDirection.z,1,0,0,0,0);

                Vec3 geb = this.geburah.position().multiply(1,0,1);
                Vec3 between = targetPos.subtract(geb);

                Vec3 rotatedCannonDirection = cannonDirection.yRot(FDMathUtil.FPI / 2);

                double angle = Math.toDegrees(FDMathUtil.angleBetweenVectors(cannonDirection,between));
                var dot = rotatedCannonDirection.dot(between.normalize());
                if (dot < 0){
                    angle = 360 - angle;
                }

                float stopAngle = 80;
                if (angle <= stopAngle){
                    speed = (float) FDMathUtil.lerp(0,speed,FDEasings.linear((float) (angle / stopAngle)));
                }


            }

            this.geburah.getWeaponRotationController().startConstantRotation(speed);

        }else{
            fireShotTime = Mth.clamp(fireShotTime - 1,0,Integer.MAX_VALUE);
        }

    }

    private Vec3 getPlayerTargetPos(){
        Pair<Vec3, Vec3> playerData = this.geburah.getPlayerPositionsCollector().getOldAndCurrentPlayerPosition(targetPlayer);
        var old = playerData.first.subtract(this.geburah.position()).multiply(1,0,1);
        var current = playerData.second.subtract(this.geburah.position()).multiply(1,0,1);
        var angle = FDMathUtil.angleBetweenVectors(old.normalize(),current.normalize());

        if (Double.isNaN(angle)){
            angle = 0;
        }

        Vec3 rotated = current.yRot(FDMathUtil.FPI / 2);


        double dot = old.normalize().dot(rotated.normalize());

        if (dot > 0){
            return this.geburah.position().add(current.yRot(-(float)angle * 20)).multiply(1,0,1);
        }else{
            return this.geburah.position().add(current.yRot((float)angle * 20)).multiply(1,0,1);
        }
    }





    @Override
    public boolean hasEnded() {
        return fireShotTime == 0;
    }

    @Override
    public void onAttackEnd() {

    }

    @Override
    public boolean canBeChanged() {
        return false;
    }

    private boolean hasFinishedCircle(){
        if (circleComplete){
            return true;
        }

        var rotationController = this.geburah.getWeaponRotationController();

        float currentRotation = rotationController.getCurrentRotation();

        if (forward){
            return currentRotation - rotationSnapshot > 180;
        }else{
            return currentRotation - rotationSnapshot > -180;
        }

    }

    private int getNearestCannonToPlayer(Player player){

        var cannonData = this.geburah.getCannonsPositionAndDirection();

        double minAngle = 180;

        int minId = 0;

        for (int i = 0; i < GeburahEntity.CANNONS_AMOUNT; i++){

            var cannonDirection = cannonData.get(i).second;
            Vec3 between = player.position().subtract(geburah.position()).multiply(1,0,1);

            double angle = FDMathUtil.angleBetweenVectors(cannonDirection.normalize(),between.normalize());

            if (angle < minAngle){
                minAngle = angle;
                minId = i;
            }

        }

        return minId;
    }


}
