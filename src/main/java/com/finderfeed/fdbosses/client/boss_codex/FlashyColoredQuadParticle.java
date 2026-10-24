package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDScreenParticle;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class FlashyColoredQuadParticle extends FDScreenParticle<FlashyColoredQuadParticle> {

    private float flashFrequency;

    private float flashOffset;

    private float quadSize;

    @Override
    public void render(GuiGraphics graphics, BufferBuilder bufferBuilder, float partialTicks) {
        PoseStack matrices = graphics.pose();
        float x = (float)this.getX(partialTicks);
        float y = (float)this.getY(partialTicks);
        float roll = this.getRoll(partialTicks);
        float halfQuadSize = this.quadSize;
        matrices.pushPose();
        matrices.translate(x, y, 0.0F);
        matrices.mulPose(Axis.ZN.rotationDegrees(roll));
        Matrix4f m = matrices.last().pose();

        float a = this.getA();

        bufferBuilder.vertex(m, -halfQuadSize, -halfQuadSize, 0.0F).color(this.getR(), this.getG(), this.getB(), a).endVertex();
        bufferBuilder.vertex(m, -halfQuadSize, halfQuadSize, 0.0F).color(this.getR(), this.getG(), this.getB(), a).endVertex();
        bufferBuilder.vertex(m, halfQuadSize, halfQuadSize, 0.0F).color(this.getR(), this.getG(), this.getB(), a).endVertex();
        bufferBuilder.vertex(m, halfQuadSize, -halfQuadSize, 0.0F).color(this.getR(), this.getG(), this.getB(), a).endVertex();
        matrices.popPose();
    }

    @Override
    public ParticleRenderType getParticleRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager p_107437_) {
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
        }

        @Override
        public void end(Tesselator p_107438_) {
            p_107438_.end();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }

    };

    public FlashyColoredQuadParticle setFlashFrequency(float flashFrequency){
        this.flashFrequency = flashFrequency;
        return this;
    }

    public FlashyColoredQuadParticle setFlashOffset(float flashOffset){
        this.flashOffset = flashOffset;
        return this;
    }

    public FlashyColoredQuadParticle setQuadSize(float quadSize){
        this.quadSize = quadSize;
        return this;
    }

    @Override
    public float getA() {
        var pticks = FDRenderUtil.tryGetPartialTickIgnorePause();

        float time = this.getAge() + pticks;
        float p = Mth.clamp((time) / this.getLifetime(), 0, 1);

        float flash = (float) (Math.sin(flashFrequency * (flashOffset + time)) * 0.5f + 0.5f) * 0.5f + 0.5f;

        return FDMathUtil.lerp(0,super.getA(),1 - p) * flash;
    }


}
