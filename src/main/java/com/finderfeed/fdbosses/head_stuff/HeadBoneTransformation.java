package com.finderfeed.fdbosses.head_stuff;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.BoneTransformationController;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;

public class HeadBoneTransformation<T extends Mob & AnimatedObject & IHasHead<T>> implements BoneTransformationController<T> {

    @Override
    public void transformBone(T t, FDModel model, FDModelPart fdModelPart, PoseStack poseStack, MultiBufferSource multiBufferSource,String boneName, int light, int overlay, float v) {

        var container = t.getHeadControllerContainer();
        var controller = container.getController(boneName);
        var vec = controller.getCurrentHeadAngles(v);

        HeadControllerContainer.Mode mode = container.getControllersMode();

        float rotX = vec.x;
        float rotY = vec.y;

        if (mode == HeadControllerContainer.Mode.ANIMATION_AND_LOOK || mode == HeadControllerContainer.Mode.ANIMATION) {
            rotX += fdModelPart.getXRot();
            rotY += fdModelPart.getYRot();
        }


        fdModelPart.setXRot(rotX);
        fdModelPart.setYRot(rotY);


    }



}
