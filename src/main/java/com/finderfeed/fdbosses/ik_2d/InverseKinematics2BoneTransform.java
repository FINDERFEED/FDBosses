package com.finderfeed.fdbosses.ik_2d;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.BoneTransformationController;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import org.joml.*;

import java.lang.Math;

public class InverseKinematics2BoneTransform<T extends AnimatedObject> implements BoneTransformationController<T> {

    private String controllerBoneStart;
    private String controllerBoneEnd;

    private String first;
    private String second;
    private String end;

    private Direction.Axis legRotateAroundAxis;
    private Direction.Axis legForwardAxis;

    private boolean negateAngles;

    /**
     * Attach this controller to any bone, it doesn't matter
     *
     * "Controller" bones should be children of the same bone as first second and end bones!
     *
     * End bone should have the same parent as controller bones
     *
     * Controller bones should only be changing in the same plane as their parent dictates!
     *
     * Rotation of the end controller bone will be given to end bone (for example to control how feet rotates)
     *
     * Pivot position of start controller bone should be in the same position as first bone's pivot
     *
     * Pivot position of end controller bone should be in the same position as end bone's pivot
     *
     * Animations SHOULD NOT INTERFERE WITH POSITIONS OF FIRST SECOND AND END BONES.
     *
     * Otherwise everything will blow up!
     *
     * Basically do not animate first second and end parts, animate only start controller bone parent and controller bones
     */
    public InverseKinematics2BoneTransform(Direction.Axis legForwardAxis, Direction.Axis legRotateAroundAxis,String controllerBoneStart, String controllerBoneEnd, String first, String second, String end, boolean negateAngles){
        this.controllerBoneEnd = controllerBoneEnd;
        this.controllerBoneStart = controllerBoneStart;
        this.legRotateAroundAxis = legRotateAroundAxis;
        this.first = first;
        this.second = second;
        this.end = end;
        this.negateAngles = negateAngles;
        this.legForwardAxis = legForwardAxis;
    }

    @Override
    public void transformBone(T animatedObject, FDModel fdModel, FDModelPart fdModelPart, PoseStack poseStack, MultiBufferSource multiBufferSource, String s, int i, int i1, float v) {

        FDModelPart controllerBoneStart = fdModel.getModelPart(this.controllerBoneStart);
        FDModelPart controllerBoneEnd = fdModel.getModelPart(this.controllerBoneEnd);

        FDModelPart firstPart = fdModel.getModelPart(this.first);
        FDModelPart secondPart = fdModel.getModelPart(this.second);
        FDModelPart endPart = fdModel.getModelPart(this.end);

        double d = this.distanceBetweenModelParts(fdModel, controllerBoneStart, controllerBoneEnd);
        double d1 = this.distanceBetweenModelParts(fdModel, firstPart, secondPart);
        double d2 = this.distanceBetweenModelParts(fdModel, secondPart, endPart);

        firstPart.x = controllerBoneStart.x;
        firstPart.y = controllerBoneStart.y;
        firstPart.z = controllerBoneStart.z;

        double distBetweenControllers = 0;

        switch (legForwardAxis){
            case X -> {
                distBetweenControllers = controllerBoneStart.x - controllerBoneEnd.x;
            }
            case Y -> {
                distBetweenControllers = controllerBoneStart.y - controllerBoneEnd.y;
            }
            case Z -> {
                distBetweenControllers = controllerBoneStart.z - controllerBoneEnd.z;
            }
        }
        distBetweenControllers /= 16;

        double cos = Math.toDegrees(Math.sin(distBetweenControllers  /  d));



        double angle1 = Math.toDegrees(Math.acos(
                (d1 * d1 + d * d - d2 * d2) / (2 * d1 * d)
        )) + cos;
        double angle2 = Math.toDegrees(Math.acos(
                (d1 * d1 + d2 * d2 - d * d) / (2 * d1 * d2)
        )) - 180;
        if (d > d1 + d2){
            angle1 = cos;
            angle2 = 0;
        }
        if (negateAngles){
            angle1 = -angle1;
            angle2 = -angle2;
        }
        switch (legRotateAroundAxis){
            case X -> {
                firstPart.setXRot((float)angle1);
                firstPart.setYRot((float)0);
                firstPart.setZRot((float)0);
                secondPart.setXRot((float)angle2);
                secondPart.setYRot((float)0);
                secondPart.setZRot((float)0);
            }
            case Y -> {
                firstPart.setXRot((float)0);
                firstPart.setYRot((float)angle1);
                firstPart.setZRot((float)0);
                secondPart.setXRot((float)0);
                secondPart.setYRot((float)angle2);
                secondPart.setZRot((float)0);
            }
            case Z -> {
                firstPart.setXRot((float)0);
                firstPart.setYRot((float)0);
                firstPart.setZRot((float)angle1);
                secondPart.setXRot((float)0);
                secondPart.setYRot((float)0);
                secondPart.setZRot((float)angle2);
            }
        }



        endPart.x = controllerBoneEnd.x;
        endPart.y = controllerBoneEnd.y;
        endPart.z = controllerBoneEnd.z;

        endPart.setXRot(controllerBoneEnd.getXRot());
        endPart.setYRot(controllerBoneEnd.getYRot());
        endPart.setZRot(controllerBoneEnd.getZRot());

        endPart.xScale = controllerBoneEnd.xScale;
        endPart.yScale = controllerBoneEnd.yScale;
        endPart.zScale = controllerBoneEnd.zScale;


    }

    private double distanceBetweenModelParts(FDModel model, FDModelPart part1, FDModelPart part2){
        Matrix4f t1 = model.getModelPartTransformation(part1);
        Matrix4f t2 = model.getModelPartTransformation(part2);


        Vector3f p1 = t1.transformPosition(new Vector3f());
        Vector3f p2 = t2.transformPosition(new Vector3f());

        return p1.distance(p2);
    }

}
