package com.finderfeed.fdbosses;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import org.joml.Vector2f;

public class HeadController<T extends Mob & IHasHead<T> & AnimatedObject> {

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
            entity.getAnimationSystem().applyAnimations(fdModel, 0f);
            FDModelPart modelPart = fdModel.getModelPart(headBone);
            if (rotate && !rotatesToHeadTarget) {
                rotatesToHeadTarget = true;
                reachedDestination = false;
                this.setCurrentRotation(modelPart.getXRot(),modelPart.getYRot());
            }else if (!rotate && rotatesToHeadTarget){
                rotatesToHeadTarget = false;
                reachedDestination = false;
                this.setCurrentRotation(-entity.getXRot() + modelPart.initRotation.x, entity.getYRot() - entity.getYHeadRot() + modelPart.initRotation.y);
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
        float rotx = FDMathUtil.lerpAround(oldRotationX,currentRotationX,-180,180,pticks);
        float roty = FDMathUtil.lerpAround(oldRotationY,currentRotationY,-180,180,pticks);

        return new Vector2f(
               rotx, roty
        );
    }

    //XY
    public Vector2f getTargetRotation(){
        entity.getAnimationSystem().applyAnimations(fdModel, 0f);
        FDModelPart modelPart = fdModel.getModelPart(headBone);
        if (rotatesToHeadTarget){
            return new Vector2f(
                    -entity.getXRot() + modelPart.initRotation.x,
                    FDMathUtil.convertMCYRotationToNormal(entity.getYRot() - entity.getYHeadRot() + modelPart.initRotation.y)
            );
        }else{
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

        if (Math.abs(targetY - currentRotationY) < 0.05 && Math.abs(targetX - currentRotationX) < 0.05){
            this.reachedDestination = true;
        }
    }

    private void rotateX(float targetX){
        float newRotation = this.getRotation(currentRotationX, targetX);
        currentRotationX = newRotation;
    }

    //-180 -> 180
    private void rotateY(float targetY){
        float newRotation = this.getRotation(currentRotationY, targetY);
        currentRotationY = newRotation;
    }

    private float getRotation(float currentRotation, float targetY){
        if (currentRotation >= 0 && targetY >= 0 || currentRotation < 0 && targetY < 0){
            int direction = targetY - currentRotationY > 0 ? 1 : -1;

            if (currentRotation >= 0) {
                if (direction == 1){
                    return Mth.clamp(currentRotation + this.getHeadTransitionSpeed() * direction, -Integer.MAX_VALUE,targetY);
                }else{
                    return Mth.clamp(currentRotation + this.getHeadTransitionSpeed() * direction, targetY, Integer.MAX_VALUE);
                }
            }else{
                if (direction == 1){
                    return Mth.clamp(currentRotation + this.getHeadTransitionSpeed() * direction, -Integer.MAX_VALUE,targetY);
                }else{
                    return Mth.clamp(currentRotation + this.getHeadTransitionSpeed() * direction, targetY, Integer.MAX_VALUE);
                }
            }
        }else{
            if (currentRotation < 0){
                float dist = -currentRotation + targetY;
                if (dist > 180){
                    currentRotation -= this.getHeadTransitionSpeed();
                    if (currentRotation < -180){
                        currentRotation += 360;
                        return Mth.clamp(currentRotation, -Integer.MAX_VALUE, targetY);
                    }
                }else{
                    return Mth.clamp(currentRotation + this.getHeadTransitionSpeed(), -Integer.MAX_VALUE, targetY);
                }
            }else{
                float dist = -targetY + currentRotation;
                if (dist > 180){
                    currentRotation += this.getHeadTransitionSpeed();
                    if (currentRotation > 180){
                        currentRotation -= 360;
                        return Mth.clamp(currentRotation, targetY, Integer.MAX_VALUE);
                    }
                }else{
                    return Mth.clamp(currentRotation - this.getHeadTransitionSpeed(), targetY, Integer.MAX_VALUE);
                }
            }
        }
        return 0;
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
        if (true) return 2;
        return headTransitionSpeed;
    }

    public T getEntity() {
        return entity;
    }

}
