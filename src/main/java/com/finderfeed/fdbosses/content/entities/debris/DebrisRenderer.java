package com.finderfeed.fdbosses.content.entities.debris;

import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class DebrisRenderer extends EntityRenderer<DebrisEntity> {

    private static final Vec3 UP = new Vec3(0, 1, 0);

    public DebrisRenderer(EntityRendererProvider.Context context){
        super(context);
    }

    @Override
    public void render(DebrisEntity debris, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light){
        float spin = Mth.lerp(partialTick, debris.rotationOld, debris.rotation);

        if (debris.groundTicks > 0 && debris.groundTicks <= DebrisEntity.SETTLE_TICKS){
            float age = debris.groundTicks - 1 + partialTick;
            float decay = 1 - age / DebrisEntity.SETTLE_TICKS;
            spin += 32 * Mth.sin(age * 4) * decay * decay;
        }

        Vec3 axis = debris.lastMovement.cross(UP);

        if (axis.x == 0 && axis.z == 0){
            axis = new Vec3(1, 0, 0);
        }

        var initial = debris.initialRotation;
        Quaternionf rotation = new Quaternionf()
                .rotationAxis((float) Math.toRadians(spin), (float) axis.x, (float) axis.y, (float) axis.z)
                .rotateXYZ((float) Math.toRadians(initial.x), (float) Math.toRadians(initial.y), (float) Math.toRadians(initial.z));
        var type = debris.getDebrisType();
        float halfSize = type.quadSize * 0.5f;

        pose.pushPose();
        pose.mulPose(rotation);
        QuadRenderer.start(buffers.getBuffer(RenderType.entityCutoutNoCull(type.texture)))
                .pose(pose)
                .size(halfSize)
                .light(light)
                .render();
        pose.popPose();

        super.render(debris, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(DebrisEntity debris){
        return debris.getDebrisType().texture;
    }
}
