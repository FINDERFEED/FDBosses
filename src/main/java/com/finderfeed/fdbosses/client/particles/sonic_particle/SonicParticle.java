package com.finderfeed.fdbosses.client.particles.sonic_particle;

import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class SonicParticle extends TextureSheetParticle {

    private SonicParticleOptions options;
    private float currentResizeSpeed;

    private ComplexEasingFunction alphaEasingFunction;

    private float currentQuadSize;
    private float oldQuadSize;

    private Quaternionf facing;

    public SonicParticle(SonicParticleOptions options,ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.options = options;
        this.lifetime = options.lifetime;
        this.currentResizeSpeed = options.resizeSpeed;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = options.startSize;
        this.rCol = options.color.r;
        this.gCol = options.color.g;
        this.bCol = options.color.b;

        double finalResize = options.endSize - options.startSize;
        if (options.resizeAcceleration != 0) {
            double d = options.resizeSpeed * options.resizeSpeed + 2 * options.resizeAcceleration * finalResize;

            double t1 = (-options.resizeSpeed + Math.sqrt(d)) / options.resizeAcceleration;
            double t2 = (-options.resizeSpeed - Math.sqrt(d)) / options.resizeAcceleration;

            this.lifetime = (int) Math.ceil(Math.max(Math.abs(t2), Math.abs(t1)));
        }else{
            double t = finalResize / options.resizeSpeed;
            this.lifetime = (int) Math.ceil(Math.abs(t));
        }
        double l = options.facingDirection.x * options.facingDirection.x + options.facingDirection.z * options.facingDirection.z; l = Math.sqrt(l);
        float angle1 = (float) Math.atan2(options.facingDirection.z,options.facingDirection.x);
        float angle2 = (float) Math.atan2(options.facingDirection.y,l);



        Quaternionf q = new Quaternionf(new AxisAngle4f(angle1,0,1,0)).rotateX(-angle2);
        this.facing = q;


        if (!options.alphaOptions.isEmpty()) {
            alphaEasingFunction = ComplexEasingFunction.builder()
                    .addArea(options.alphaOptions.inTime, FDEasings::linear)
                    .addArea(options.alphaOptions.stayTime, (f)->1f)
                    .addArea(options.alphaOptions.outTime, FDEasings::reversedLinear)
                    .build();
            this.alpha = 0;
        }else{
            this.alpha = options.alphaOptions.maxAlpha;
        }
        this.hasPhysics = false;
        this.friction = 0;
        this.currentQuadSize = quadSize;
        this.oldQuadSize = quadSize;
    }


    @Override
    public void render(VertexConsumer p_107678_, Camera p_107679_, float p_107680_) {
        float pticks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(Minecraft.getInstance().isPaused());
        float q = FDMathUtil.lerp(oldQuadSize,currentQuadSize,pticks);
        this.quadSize = q;
        super.render(p_107678_, p_107679_, p_107680_);
    }

    @Override
    public void tick() {
        super.tick();

        if (alphaEasingFunction != null){
            this.alpha = alphaEasingFunction.apply(this.age) * options.alphaOptions.maxAlpha;
        }

        float max = Math.max(options.startSize, options.endSize);
        float min = Math.min(options.startSize, options.endSize);
        oldQuadSize = currentQuadSize;
        this.currentQuadSize = Mth.clamp(this.currentQuadSize + this.currentResizeSpeed,min,max);
        this.currentResizeSpeed += options.resizeAcceleration;

    }


    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }


    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quaternion,camera,pticks)->{
            quaternion.set(facing);
        };
    }

    public static class Factory implements ParticleProvider<SonicParticleOptions>{

        private SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet){
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SonicParticleOptions particle, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            SonicParticle p = new SonicParticle(particle,level,x,y,z,xd,yd,zd);
            p.pickSprite(spriteSet);
            return p;
        }
    }

}
