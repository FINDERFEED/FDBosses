package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDScreenParticle;
import com.finderfeed.fdlib.systems.trails.FDTrailDataGenerator;
import com.finderfeed.fdlib.systems.trails.FDTrailRenderer;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class SparkScreenParticle extends FDScreenParticle<SparkScreenParticle> {

    private FDTrailDataGenerator<SparkScreenParticle> trailData;
    public FDColor endColor = new FDColor(1f,1f,1f,1f);
    private float quadSize;


    public SparkScreenParticle(float quadSize, int trailPoints){
        this.quadSize = quadSize;
        this.trailData = new FDTrailDataGenerator<>(
                ((sparkScreenParticle, aFloat) -> {
                    return new Vec3(
                            sparkScreenParticle.getX(aFloat),
                            sparkScreenParticle.getY(aFloat),
                            0
                    );
                }), trailPoints, 1);
    }

    @Override
    public SparkScreenParticle setLifetime(int lifetime) {
        return super.setLifetime(lifetime + trailData.getMaxPointsAmount());
    }

    @Override
    public void tick() {
        if (this.getAge() >= this.getLifetime() - trailData.getMaxPointsAmount()){
            this.setSpeed(0,0);
            this.setAcceleration(0,0);
        }
        super.tick();
        this.trailData.tick(this);
    }

    @Override
    public void render(GuiGraphics graphics, BufferBuilder vertex, float partialTicks) {
        partialTicks = FDRenderUtil.tryGetPartialTickIgnorePause();
        PoseStack matrices = graphics.pose();
        float x = (float)this.getX(partialTicks);
        float y = (float)this.getY(partialTicks);
        float roll = this.getRoll(partialTicks);

        float halfQuadSize = this.quadSize / 2;

        matrices.pushPose();
        matrices.translate(x, y, 0);

        FDTrailRenderer.renderTrail(this, trailData, vertex, matrices, this.quadSize, 4, 20, partialTicks,
                endColor,
                new FDColor(this.getR(),this.getG(),this.getB(),this.getA())
        );

        matrices.mulPose(Axis.ZN.rotationDegrees(roll));
        Matrix4f m = matrices.last().pose();
        float a = 0;

        vertex.addVertex(m, -halfQuadSize, -halfQuadSize, 0.0F).setColor(this.getR(), this.getG(), this.getB(), this.getA() * a);
        vertex.addVertex(m, -halfQuadSize, halfQuadSize, 0.0F).setColor(this.getR(), this.getG(), this.getB(), this.getA() * a);
        vertex.addVertex(m, halfQuadSize, halfQuadSize, 0.0F).setColor(this.getR(), this.getG(), this.getB(), this.getA() * a);
        vertex.addVertex(m, halfQuadSize, -halfQuadSize, 0.0F).setColor(this.getR(), this.getG(), this.getB(), this.getA() * a);


        matrices.popPose();


    }

    public SparkScreenParticle setEndColor(float r, float g, float b, float a){
        this.endColor = new FDColor(r,g,b,a);
        return this;
    }

    @Override
    public ParticleRenderType getParticleRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new FDParticleRenderType() {
        @Override
        public void end() {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }

        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }
    };

}
