package com.finderfeed.fdbosses.content.entities.geburah.scales_controller;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.BoneTransformationController;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public class GeburahScalesBoneController implements BoneTransformationController<GeburahEntity> {

    @Override
    public void transformBone(GeburahEntity geburah, FDModel fdModel, FDModelPart scalesPlates, PoseStack poseStack, MultiBufferSource multiBufferSource, String s, int i, int i1, float v) {

        
        FDModelPart scalesPlate1 = fdModel.getModelPart("scales_plate_south");
        FDModelPart scalesPlate2 = fdModel.getModelPart("scales_plate_north");

        float rotation = geburah.getScalesController().getClientDisplacementAngle(v);

        scalesPlates.setXRot(rotation);
        scalesPlate1.setXRot(-rotation);
        scalesPlate2.setXRot(-rotation);
        
    }

}
