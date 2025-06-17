package com.finderfeed.fdbosses;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.BoneTransformationController;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;

public class HeadBoneTransformation<T extends Mob & AnimatedObject & IHasHead<T>> implements BoneTransformationController<T> {

    @Override
    public void transformBone(T t, FDModelPart fdModelPart, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay, float v) {

        float yBodyRot = FDMathUtil.lerp(t.yRotO,t.getYRot(), v);
        float yHeadRot = t.getViewYRot(v);
        float xHeadRot = t.getViewXRot(v);

        HeadController<T> headController = t.getHeadController();

        if (!headController.hasReachedDestination()){
            var rot = headController.getCurrentRotation(v);
            fdModelPart.setXRot(yBodyRot - rot.x);
            fdModelPart.setYRot(rot.y);
        }else {
            if (headController.isTransitioningToLookTarget()) {
                fdModelPart.setYRot(yBodyRot - yHeadRot);
                fdModelPart.setXRot(-xHeadRot);
            }
        }

        fdModelPart.setXRot(0);

    }

}
