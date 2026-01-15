package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class DecalParticle extends Particle {

    private DecalParticleOptions decalParticleOptions;

    private ComplexEasingFunction easingFunction;

    public DecalParticle(DecalParticleOptions decalParticleOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);

        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.decalParticleOptions = decalParticleOptions;

        var alphaOptions = decalParticleOptions.getAlphaOptions();

        this.lifetime = alphaOptions.fullTime();

        easingFunction = ComplexEasingFunction.builder()
                .addArea(alphaOptions.inTime, FDEasings::linear)
                .addArea(alphaOptions.stayTime, FDEasings::one)
                .addArea(alphaOptions.outTime, FDEasings::reversedLinear)
                .build();

    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        Vec3 camPos = camera.getPosition();

        Vec3 thisPos = FDMathUtil.interpolateVectors(
                new Vec3(xo,yo,zo),
                new Vec3(x,y,z),
                pticks
        );

        Vec3 translation = thisPos.subtract(camPos);

        Matrix4f mat = new Matrix4f();

        mat.translate((float) translation.x, (float) translation.y, (float) translation.z);

        FDRenderUtil.applyMovementMatrixRotations(mat, decalParticleOptions.getDirection());

        float roll = FDMathUtil.lerp(oRoll,this.roll,pticks);

        mat.rotateY((float)Math.toRadians(roll));

        float alpha = easingFunction.apply(age + pticks);

        float size = decalParticleOptions.getSize();

        int light = this.getLightColor(pticks);

        vertex.vertex(mat, size,0,size).uv(0,0).color(1f,1f,1f,alpha).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
        vertex.vertex(mat, size,0,-size).uv(0,1).color(1f,1f,1f,alpha).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
        vertex.vertex(mat, -size,0,-size).uv(1,1).color(1f,1f,1f,alpha).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();
        vertex.vertex(mat, -size,0,size).uv(1,0).color(1f,1f,1f,alpha).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).endVertex();

    }

    public DecalParticleOptions getDecalParticleOptions() {
        return decalParticleOptions;
    }

}
