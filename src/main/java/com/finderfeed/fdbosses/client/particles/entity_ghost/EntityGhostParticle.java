package com.finderfeed.fdbosses.client.particles.entity_ghost;

import com.finderfeed.fdlib.util.client.ColoredVertexConsumer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EntityGhostParticle extends Particle {

    private int entityId;

    public EntityGhostParticle(EntityGhostParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.entityId = options.entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = options.lifetime;
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        var entity = level.getEntity(entityId);

        if (entity != null) {

            PoseStack matrices = new PoseStack();

            Vec3 ppos = FDMathUtil.interpolateVectors(
                    new Vec3(xo,yo,zo),
                    new Vec3(x,y,z),
                    pticks
            );
            Vec3 camPos = camera.getPosition();
            Vec3 offset = ppos.subtract(camPos);

            matrices.translate(offset.x, offset.y, offset.z);

            float yRot = FDMathUtil.lerp(entity.yRotO, entity.getYRot(), pticks);

            var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            MultiBufferSource multiBufferSource = ColoredVertexConsumer.wrapBufferSource(bufferSource, 255, 255, 255, 50);

            renderer.render(entity, yRot, pticks, matrices, multiBufferSource, this.getLightColor(pticks));

        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Factory implements ParticleProvider<EntityGhostParticleOptions>{

        @Nullable
        @Override
        public Particle createParticle(EntityGhostParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new EntityGhostParticle(options, level, x, y, z, xd, yd, zd);
        }
    }

}
