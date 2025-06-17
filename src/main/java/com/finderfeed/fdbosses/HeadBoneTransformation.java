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

        var vec = t.getHeadController().getCurrentHeadAngles(v);

        fdModelPart.setXRot(vec.x);
        fdModelPart.setYRot(vec.y);



    }



}
