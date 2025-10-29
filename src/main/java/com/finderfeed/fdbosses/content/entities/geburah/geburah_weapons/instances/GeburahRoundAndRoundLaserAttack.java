package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttack;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations.GeburahLerpingRotation;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GeburahRoundAndRoundLaserAttack extends GeburahWeaponAttack {

    private boolean forward;

    private boolean circleComplete = false;

    private int cannonId = -1;
    private Player targetPlayer;

    private int fireShotTime = -1;

    private Vec3 targetPos;
    private float rotationSnapshot;

    public GeburahRoundAndRoundLaserAttack(GeburahEntity geburah, boolean forward) {
        super(geburah);
        this.forward = forward;
    }

    @Override
    public void onAttackStart() {
        this.rotationSnapshot = this.geburah.getWeaponRotationController().getCurrentRotation();
        this.targetPlayer = geburah.pickRandomCombatant();
        this.geburah.laserAttackPreparator.launchPreparation(20);
    }

    @Override
    public void tickAttack() {

        if (fireShotTime == -1){
            float maxSpeed = 15f;
            this.rotateUntilStopAngleIsReached(maxSpeed, 50);
            this.rotateToTargetPos(maxSpeed);
        }else{
            System.out.println("Jiorno Jiovanna");
            fireShotTime = Mth.clamp(fireShotTime - 1,0,Integer.MAX_VALUE);
        }

    }

    private void rotateToTargetPos(float maxSpeed){

        if (targetPos != null){

            var controller = this.geburah.getWeaponRotationController();
            if (rotationSnapshot != -1) {
                if (!forward){
                    rotationSnapshot = -rotationSnapshot;
                }
                controller.rotateWeapons(new GeburahLerpingRotation(controller, maxSpeed, rotationSnapshot));
                rotationSnapshot = -1;
            }else{
                if (controller.finishedRotation()){
                    fireShotTime = 30;
                }
            }

        }

    }



    private void rotateUntilStopAngleIsReached(float maxSpeed, float startDecelerationAngle){

        if (targetPos == null) {
            if (this.hasFinishedCircle()) {

                if (cannonId == -1){
                    this.cannonId = this.getFarthestCannonToPlayer(targetPlayer);
                }

                circleComplete = true;

                Vec3 targetPos = this.getPlayerTargetPos(20);

                double angle = this.getCurrentAngleToTargetPos(targetPos);

                if (angle <= startDecelerationAngle) {
                    rotationSnapshot = (float) angle;
                    this.targetPos = targetPos;
                }

            }

            if (!forward){
                maxSpeed = -maxSpeed;
            }

            this.geburah.getWeaponRotationController().startConstantRotation(maxSpeed);
        }
    }

    private double getCurrentAngleToTargetPos(Vec3 targetPos){
        var cannonData = this.geburah.getCannonsPositionAndDirection().get(cannonId);


        var pos = cannonData.first;
        var cannonDirection = cannonData.second;

        ((ServerLevel)geburah.level()).sendParticles(BallParticleOptions.builder().stay(3).build(),
                pos.x + cannonDirection.x,
                pos.y,
                pos.z + cannonDirection.z,
                1,0,0,0,0
        );


        Vec3 geb = this.geburah.position().multiply(1, 0, 1);
        Vec3 between = targetPos.subtract(geb);

        Vec3 rotatedCannonDirection = cannonDirection.yRot(FDMathUtil.FPI / 2);

        double angle = Math.toDegrees(FDMathUtil.angleBetweenVectors(cannonDirection, between));
        var dot = rotatedCannonDirection.dot(between.normalize());


        if (forward && dot < 0 || dot > 0 && !forward) {
            angle = 360 - angle;
        }

        return angle;
    }


    private Vec3 getPlayerTargetPos(float angleModifier){
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
            return this.geburah.position().add(current.yRot(-(float)angle * angleModifier)).multiply(1,0,1);
        }else{
            return this.geburah.position().add(current.yRot((float)angle * angleModifier)).multiply(1,0,1);
        }
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

    private int getFarthestCannonToPlayer(Player player){

        var cannonData = this.geburah.getCannonsPositionAndDirection();

        double maxAngle = 0;

        int maxId = 0;

        for (int i = 0; i < GeburahEntity.CANNONS_AMOUNT; i++){

            var cannonDirection = cannonData.get(i).second;
            Vec3 between = player.position().subtract(geburah.position()).multiply(1,0,1);

            double angle = FDMathUtil.angleBetweenVectors(cannonDirection.normalize(),between.normalize());

            if (angle > maxAngle){
                maxAngle = angle;
                maxId = i;
            }

        }

        return maxId;
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


}
