package com.finderfeed.fdbosses;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import org.joml.Vector2f;

public class HeadController<T extends Mob & IHasHead & AnimatedObject> {

    private FDModel fdModel;
    private T entity;
    private String headBone;

    private float headTransitionSpeed;

    private float currentRotationX;
    private float currentRotationY;
    private float oldRotationY;
    private float oldRotationX;

    private boolean rotatesToHeadTarget;

    private boolean reachedDestination = true;

    public HeadController(FDModel model, String headBone, T entity){
        this.entity = entity;
        this.headBone = headBone;
        this.fdModel =  model;
        this.headTransitionSpeed = entity.getHeadRotSpeed();
    }

    public void clientTick(){
        this.rotate();
    }

    public void rotateToLookTarget(boolean rotate){
        if (entity.level().isClientSide) {
            if (rotate && !rotatesToHeadTarget) {
                rotatesToHeadTarget = true;
                reachedDestination = false;
                entity.getAnimationSystem().applyAnimations(fdModel, 0.5f);
                FDModelPart modelPart = fdModel.getModelPart(headBone);
                this.setCurrentRotation(modelPart.getXRot(),modelPart.getYRot());
            }else if (!rotate && rotatesToHeadTarget){
                rotatesToHeadTarget = false;
                reachedDestination = false;
                this.setCurrentRotation(entity.getXRot(), entity.getYRot() - entity.getYHeadRot());
            }
            oldRotationX = this.getCurrentRotationX();
            oldRotationY = this.getCurrentRotationY();
        }
    }

    public boolean hasReachedDestination(){
        return reachedDestination;
    }

    protected void setCurrentRotation(float x, float y){
        this.currentRotationX = FDMathUtil.convertMCYRotationToNormal(x);
        this.currentRotationY = FDMathUtil.convertMCYRotationToNormal(y);
    }

    public Vector2f getCurrentRotation(float pticks){
        float rotx = currentRotationX;
        float roty = FDMathUtil.lerpAround(oldRotationY,currentRotationY,-180,180,pticks);

        return new Vector2f(
               rotx, roty
        );
    }

    //XY
    public Vector2f getTargetRotation(){
        if (rotatesToHeadTarget){
            return new Vector2f(
                    entity.getXRot(),
                    FDMathUtil.convertMCYRotationToNormal(entity.getYRot() - entity.getYHeadRot())
            );
        }else{
            entity.getAnimationSystem().applyAnimations(fdModel, 0.5f);
            FDModelPart modelPart = fdModel.getModelPart(headBone);
            return new Vector2f(
                    modelPart.getXRot(),
                    FDMathUtil.convertMCYRotationToNormal(modelPart.getYRot())
            );
        }
    }

    private void rotate(){
        if (this.hasReachedDestination()) return;

        Vector2f target = this.getTargetRotation();

        oldRotationX = currentRotationX;
        oldRotationY = currentRotationY;

        float targetX = target.x;
        float targetY = target.y;

        this.rotateY(targetY);
        this.rotateX(targetX);

        if (Math.abs(targetY - currentRotationY) < 0.05){
            this.reachedDestination = true;
        }
    }

    private void rotateX(float targetX){

    }

    //-180 -> 180
    private void rotateY(float targetY){

        if (currentRotationY >= 0 && targetY >= 0 || currentRotationY < 0 && targetY < 0){
            int direction = targetY - currentRotationY > 0 ? 1 : -1;

            if (currentRotationY >= 0) {
                if (direction == 1){
                    currentRotationY = Mth.clamp(currentRotationY + this.getHeadTransitionSpeed() * direction, -Integer.MAX_VALUE,targetY);
                }else{
                    currentRotationY = Mth.clamp(currentRotationY + this.getHeadTransitionSpeed() * direction, targetY, Integer.MAX_VALUE);
                }
            }else{
                if (direction == 1){
                    currentRotationY = Mth.clamp(currentRotationY + this.getHeadTransitionSpeed() * direction, -Integer.MAX_VALUE,targetY);
                }else{
                    currentRotationY = Mth.clamp(currentRotationY + this.getHeadTransitionSpeed() * direction, targetY, Integer.MAX_VALUE);
                }
            }
        }else{
            if (currentRotationY < 0){
                float dist = -currentRotationY + targetY;
                if (dist > 180){
                    currentRotationY -= this.getHeadTransitionSpeed();
                    if (currentRotationY < -180){
                        currentRotationY += 360;
                        currentRotationY = Mth.clamp(currentRotationY, -Integer.MAX_VALUE, targetY);
                    }
                }else{
                    currentRotationY = Math.clamp(currentRotationY + this.getHeadTransitionSpeed(), -Integer.MAX_VALUE, targetY);
                }
            }else{
                float dist = -targetY + currentRotationY;
                if (dist > 180){
                    currentRotationY += this.getHeadTransitionSpeed();
                    if (currentRotationY > 180){
                        currentRotationY -= 360;
                        currentRotationY = Mth.clamp(currentRotationY, targetY, Integer.MAX_VALUE);
                    }
                }else{
                    currentRotationY = Mth.clamp(currentRotationY - this.getHeadTransitionSpeed(), targetY, Integer.MAX_VALUE);
                }
            }
        }

    }

    public boolean isTransitioningToLookTarget(){
        return this.rotatesToHeadTarget;
    }

    public float getCurrentRotationX() {
        return currentRotationX;
    }

    public float getCurrentRotationY() {
        return currentRotationY;
    }

    public float getOldRotationX() {
        return oldRotationX;
    }

    public float getOldRotationY() {
        return oldRotationY;
    }

    public float getHeadTransitionSpeed() {
        return headTransitionSpeed;
    }

    public T getEntity() {
        return entity;
    }

}
