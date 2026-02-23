package com.finderfeed.fdbosses.client.particles.vanilla_like;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimatedSpriteParticle extends TextureSheetParticle {

    private SpriteParticleOptions options;
    private SpriteSet spriteSet;

    private float xr;
    private float yr;
    private float zr;

    private Vector3f particleLookDirection;

    private float xro;
    private float yro;
    private float zro;


    private float xrd;
    private float yrd;
    private float zrd;

    public AnimatedSpriteParticle(SpriteParticleOptions options, SpriteSet spriteSet, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd) {
        super(clientLevel, x, y, z, xd, yd, zd);
        this.particleLookDirection = options.getParticleLookDirection();
        this.options = options;
        this.spriteSet = spriteSet;
        this.lifetime = options.getLifetime();
        this.quadSize = options.getSize();
        this.setSpriteFromAge(spriteSet);
        this.xrd = options.getXYZRotationSpeed().x;
        this.yrd = options.getXYZRotationSpeed().y;
        this.zrd = options.getXYZRotationSpeed().z;
        this.friction = options.getFriction();
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {
        PoseStack matrices = new PoseStack();

        Vec3 camPos = camera.getPosition();

        double px = FDMathUtil.lerp(xo, x, pticks) - camPos.x;
        double py = FDMathUtil.lerp(yo, y, pticks) - camPos.y;
        double pz = FDMathUtil.lerp(zo, z, pticks) - camPos.z;

        matrices.translate(px,py,pz);

        boolean lookingAtCamera = particleLookDirection.x == 0 && particleLookDirection.y == 0 && particleLookDirection.z == 0;

        if (lookingAtCamera){
            matrices.mulPose(camera.rotation());
        }else{
            FDRenderUtil.applyMovementMatrixRotations(matrices, new Vec3(particleLookDirection));
        }

        float rx = (float) Math.toRadians(FDMathUtil.lerp(xro, this.xr, pticks) + options.getXYZRotation().x);
        float ry = (float) Math.toRadians(FDMathUtil.lerp(yro, this.yr, pticks) + options.getXYZRotation().y);
        float rz = (float) Math.toRadians(FDMathUtil.lerp(zro, this.zr, pticks) + options.getXYZRotation().z);

        if (rx != 0 || ry != 0 || rz != 0) {
            matrices.mulPose(new Quaternionf().rotationZYX(rx, ry, rz));
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        if (options.isFlipped()) {
            u0 = this.getU1();
            u1 = this.getU0();
        }

        Matrix4f mat = matrices.last().pose();

        float w = quadSize / 2;

        int light = this.getLightColor(pticks);

        if (lookingAtCamera){
            vertex.addVertex(mat, (float)- w, (float)- w, (float)0).setUv(u0,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)- w, (float)0).setUv(u1,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)+ w, (float)0).setUv(u1,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)- w, (float)+ w, (float)0).setUv(u0,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);

            vertex.addVertex(mat, (float)- w, (float)+ w, (float)0).setUv(u0,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)+ w, (float)0).setUv(u1,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)- w, (float)0).setUv(u1,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)- w, (float)- w, (float)0).setUv(u0,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);

        }else{
            vertex.addVertex(mat, (float)- w, (float)0, (float) - w).setUv(u0,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)0, (float) - w).setUv(u1,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)0, (float) + w).setUv(u1,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)- w, (float)0, (float) + w).setUv(u0,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);

            vertex.addVertex(mat, (float)- w, (float)0, (float)+ w).setUv(u0,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)0, (float)+ w).setUv(u1,v1).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)+ w, (float)0, (float)- w).setUv(u1,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
            vertex.addVertex(mat, (float)- w, (float)0, (float)- w).setUv(u0,v0).setColor(this.rCol, gCol, bCol, alpha).setLight(light);
        }



    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);

        xro = xr;
        yro = yr;
        zro = zr;

        xr += xrd;
        yr += yrd;
        zr += zrd;

        if (this.options.frictionAffectsXYZRotation()){
            xrd = xrd * options.getFriction();
            yrd = yrd * options.getFriction();
            zrd = zrd * options.getFriction();
        }


        super.tick();


    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    protected int getLightColor(float pticks) {
        return options.isLightenedUp() ? LightTexture.FULL_BRIGHT : super.getLightColor(pticks);
    }

    public static class Factory implements ParticleProvider<SpriteParticleOptions> {

        private SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet){
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SpriteParticleOptions p_107421_, ClientLevel p_107422_, double x, double y, double z, double xd, double yd, double zd) {
            return new AnimatedSpriteParticle(p_107421_, spriteSet, p_107422_, x, y, z, xd, yd, zd);
        }

    }


}
