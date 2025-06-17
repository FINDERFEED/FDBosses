package com.finderfeed.fdbosses;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.BoneTransformationController;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.joml.*;

import java.lang.Math;

public class HeadBoneTransformation<T extends Mob & AnimatedObject & IHasHead<T>> implements BoneTransformationController<T> {

    @Override
    public void transformBone(T t, FDModel model, FDModelPart fdModelPart, PoseStack poseStack, MultiBufferSource multiBufferSource,String boneName, int light, int overlay, float v) {

//        float yBodyRot = FDMathUtil.lerp(t.yRotO,t.getYRot(), v);
//
//
//        HeadController<T> headController = t.getHeadController();
//
//        if (!headController.hasReachedDestination()){
//            var rot = headController.getCurrentRotation(v);
//            fdModelPart.setXRot(rot.x);
//            fdModelPart.setYRot(rot.y);
//        }else {
//            if (headController.isTransitioningToLookTarget()) {
//                float yHeadRot = t.getViewYRot(v);
//                float xHeadRot = t.getViewXRot(v);
//                fdModelPart.setYRot(yBodyRot - yHeadRot + fdModelPart.initRotation.y);
//                fdModelPart.setXRot(-xHeadRot + fdModelPart.initRotation.x);
//            }
//        }

        Player player = t.level().getNearestPlayer(t, 30);

        if (player == null) return;



        Matrix4f transform = t.getModelPartTransformation(t, boneName, model, v);

        Vector3f position = transform.transformPosition(new Vector3f());
        Vector3f wposition = position.add(
                (float) t.getX(),
                (float) t.getY(),
                (float) t.getZ(),
                new Vector3f()
        );

        Vector3d playerPosition = new Vector3d(
                player.getPosition(v).x,
                player.getPosition(v).y + player.getEyeHeight(),
                player.getPosition(v).z
        );

        Vector3d between = playerPosition.sub(wposition,new Vector3d());

        Vector3d axisX = this.floatToDouble(transform.transformDirection(new Vector3f(1,0,0))).normalize();
        Vector3d axisY = this.floatToDouble(transform.transformDirection(new Vector3f(0,1,0))).normalize();
        Vector3d axisZ = this.floatToDouble(transform.transformDirection(new Vector3f(0,0,1))).normalize();


        Vector3d vec = this.fromOtherBasisToEuclidian(axisX,axisY,axisZ,between);

        float verticalAngle = 180 + (float)Math.toDegrees(Math.atan2(vec.x,vec.z));

        double a = Math.sqrt(vec.z * vec.z + vec.x * vec.x);
        float horizontalAngle =  (float) Math.toDegrees(Math.atan2(vec.y,a));


//        fdModelPart.setXRot(-30);
//        fdModelPart.setYRot(-10);

        fdModelPart.setYRot(verticalAngle);
        fdModelPart.setXRot(horizontalAngle);

    }

    private Vector3d floatToDouble(Vector3f v){
        return new Vector3d(
                v.x,v.y,v.z
        );
    }

    private Vector3d fromEuclidianToOtherBasis(
            Vector3d nbx,
            Vector3d nby,
            Vector3d nbz,
            Vector3d point
    ){
        Matrix3d matrix3d = new Matrix3d(
                nbx.x,nbx.y,nbx.z,
                nby.x,nby.y,nby.z,
                nbz.x,nbz.y,nbz.z
        );
        return matrix3d.transform(point, new Vector3d());
    }

    private Vector3d fromOtherBasisToEuclidian(
            Vector3d nbx,
            Vector3d nby,
            Vector3d nbz,
            Vector3d point
    ){
        Matrix3d matrix3d = new Matrix3d(
                nbx.x,nbx.y,nbx.z,
                nby.x,nby.y,nby.z,
                nbz.x,nbz.y,nbz.z
        ).invert();
        return matrix3d.transform(point, new Vector3d());
    }

}
