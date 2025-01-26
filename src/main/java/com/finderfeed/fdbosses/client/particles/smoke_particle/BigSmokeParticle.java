package com.finderfeed.fdbosses.client.particles.smoke_particle;

import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import org.jetbrains.annotations.Nullable;

public class BigSmokeParticle extends TextureSheetParticle {

    private BigSmokeParticleOptions options;
    private ComplexEasingFunction easingFunction;
    private float maxQuadSize;
    private float oQuadSize;
    private float cQuadSize;


    public BigSmokeParticle(BigSmokeParticleOptions options,ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.options = options;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.rCol = options.color.r;
        this.gCol = options.color.g;
        this.bCol = options.color.b;
        this.alpha = options.color.a;
        this.easingFunction = ComplexEasingFunction.builder()
                .addArea(options.intOut.inTime, FDEasings::linear)
                .addArea(options.intOut.stayTime,FDEasings::one)
                .addArea(options.intOut.outTime,FDEasings::reversedLinear)
                .build();
        quadSize = this.easingFunction.apply(0) * options.size;
        cQuadSize = quadSize;
        oQuadSize = quadSize;
        this.lifetime = options.intOut.fullTime();
        this.maxQuadSize = options.size;
        this.oQuadSize = quadSize;
        this.friction = options.friction;
        this.hasPhysics = false;
    }


    @Override
    public void render(VertexConsumer consumer, Camera camera, float pticks) {
        this.quadSize = FDMathUtil.lerp(oQuadSize,cQuadSize,pticks);
        super.render(consumer, camera, pticks);
    }

    @Override
    public void tick() {

        double l = Math.sqrt(xd * xd + yd * yd + zd * zd);

        if (l < options.minSpeed){
            this.xd = xd / l * options.minSpeed;
            this.yd = yd / l * options.minSpeed;
            this.zd = zd / l * options.minSpeed;
        }

        this.oQuadSize = cQuadSize;
        this.cQuadSize = maxQuadSize * this.easingFunction.apply(age);

        super.tick();

    }

    @Override
    public ParticleRenderType getRenderType() {
        return FDRenderUtil.ParticleRenderTypes.NORMAL_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<BigSmokeParticleOptions>{

        private SpriteSet set;

        public Factory(SpriteSet set){
            this.set = set;
        }

        @Nullable
        @Override
        public Particle createParticle(BigSmokeParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            BigSmokeParticle particle = new BigSmokeParticle(options,level,x,y,z,xd,yd,zd);
            particle.pickSprite(set);
            return particle;
        }
    }
}
