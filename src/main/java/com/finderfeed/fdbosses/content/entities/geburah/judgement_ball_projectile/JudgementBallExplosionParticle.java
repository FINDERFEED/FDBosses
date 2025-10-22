package com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossCoreShaders;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class JudgementBallExplosionParticle extends Particle {

    private static FDModel model;

    private JudgementBallExplosionParticleOptions options;

    public JudgementBallExplosionParticle(ClientLevel p_107239_,  double x, double y, double z, double xd, double yd, double zd, JudgementBallExplosionParticleOptions options) {
        super(p_107239_, x,y,z,xd,yd,zd);
        if (model == null){
            model = new FDModel(BossModels.JUDGEMENT_BALL.get());
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = options.lifetime;
        this.options = options;

    }

    @Override
    public void render(VertexConsumer v, Camera camera, float pticks) {

        Vec3 ppos = FDMathUtil.interpolateVectors(
                new Vec3(xo,yo,zo),
                new Vec3(x,y,z),
                pticks
        );

        Vec3 realPos = ppos.subtract(camera.getPosition());

        PoseStack poseStack = new PoseStack();
        poseStack.translate(realPos.x,realPos.y,realPos.z);
        FDRenderUtil.applyMovementMatrixRotations(poseStack, options.movement);

        float time = (this.age + pticks) / lifetime;

        float scale = FDEasings.easeOut(time) * options.size;

        float rotation = this.age + pticks;

        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation * 20));

        poseStack.scale(scale,scale,scale);

        float alpha = 1 - FDEasings.easeOut(time);

        model.render(poseStack,v, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,1f,1f,1f,alpha * 0.9f);

    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator p_351047_, TextureManager p_107463_) {
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, FDBosses.location("textures/entities/geburah/judgement_ball_3.png"));
            RenderSystem.setShader(()->GameRenderer.getParticleShader());
            return p_351047_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public boolean isTranslucent() {
            return true;
        }

        @Override
        public String toString() {
            return "GEBURAH_JUDGEMENT_BALL";
        }
    };

    public static class Factory implements ParticleProvider<JudgementBallExplosionParticleOptions>{


        @Nullable
        @Override
        public Particle createParticle(JudgementBallExplosionParticleOptions p_107421_, ClientLevel p_107422_, double x, double y, double z, double xd, double yd, double zd) {
            JudgementBallExplosionParticle particle = new JudgementBallExplosionParticle(p_107422_,x,y,z,xd,yd,zd,p_107421_);
            return particle;
        }
    }

}
