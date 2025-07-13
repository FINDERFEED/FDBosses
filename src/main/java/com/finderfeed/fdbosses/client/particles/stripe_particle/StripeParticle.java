package com.finderfeed.fdbosses.client.particles.stripe_particle;

import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.ShapeOnCurveRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class StripeParticle extends Particle {

    private StripeParticleOptions stripeParticleOptions;

    private List<Vector3f> points;

    private FD2DShape shape;

    public StripeParticle(StripeParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.stripeParticleOptions = options;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = options.getLifetime();
        shape = FD2DShape.createSimpleCircleNVertexShape(options.getScale(), 3);
        points = stripeParticleOptions.getOffsets().stream().map(v->new Vector3f((float) v.x + 0.001f,(float) v.y + 0.001f,(float) v.z + 0.001f)).toList();
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return AABB.INFINITE;
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {


        float lifetimeP = (this.age + pticks) / lifetime;

        float startP = FDMathUtil.lerp(-stripeParticleOptions.getStripePercentLength(),1,lifetimeP);

        float endP = startP + stripeParticleOptions.getStripePercentLength();

        Vec3 pos = FDMathUtil.interpolateVectors(
                new Vec3(xo,yo,zo),
                new Vec3(x,y,z),
                pticks
        ).subtract(camera.getPosition());

        PoseStack matrix = new PoseStack();

        matrix.pushPose();
        matrix.translate(pos.x,pos.y,pos.z);

        for (int i = 0; i < 2;i++)
            ShapeOnCurveRenderer.start(vertex)
                .scalingFunction(v->{

                    float distToEnd = endP - v;
                    float distToStart = v - startP;

                    float p;
                    if (distToEnd > distToStart){
                        p =  distToStart / (stripeParticleOptions.getStripePercentLength() / 2) ;
                    }else{
                        p =  distToEnd / (stripeParticleOptions.getStripePercentLength()/2);
                    }

                    float startp = Math.clamp(v / stripeParticleOptions.getStripePercentLength() * 4,0,1);
                    float endp = Math.clamp((1 - v) / stripeParticleOptions.getStripePercentLength() * 4,0,1);

                    return Math.clamp(FDEasings.easeOut(p),0,1) * startp * endp;
                })
                .startPercent(startP)
                .endPercent(endP)
                .pose(matrix)
                .shape(shape)
                .startColor(stripeParticleOptions.getStartColor())
                .endColor(stripeParticleOptions.getEndColor())
                .curvePositions(points)
                .lod(Math.clamp(stripeParticleOptions.getLOD(),0,200))
                .render();

        matrix.popPose();
    }


    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new FDParticleRenderType() {
        @Override
        public void end() {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.defaultBlendFunc();
        }

        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {


            if (Minecraft.useShaderTransparency()){
                Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
            }else{
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            RenderSystem.enableCull();

            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }
    };

    public static class Factory implements ParticleProvider<StripeParticleOptions> {

        @Nullable
        @Override
        public Particle createParticle(StripeParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new StripeParticle(options,level,x,y,z,xd,yd,zd);
        }
    }



}
