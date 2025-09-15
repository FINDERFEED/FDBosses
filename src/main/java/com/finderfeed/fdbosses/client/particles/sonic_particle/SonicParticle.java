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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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





    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        float q = FDMathUtil.lerp(oldQuadSize,currentQuadSize,pticks);
        this.quadSize = q;

        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)pticks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pticks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pticks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = this.facing;
        } else {
            quaternionf = new Quaternionf(this.facing);
            quaternionf.rotateZ(Mth.lerp(pticks, this.oRoll, this.roll));
        }

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(pticks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(pticks);
        vertex.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertex.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertex.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertex.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
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
