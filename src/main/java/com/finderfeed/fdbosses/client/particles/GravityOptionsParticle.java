package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class GravityOptionsParticle extends TextureSheetParticle {

    private GravityParticleOptions options;

    private float particleRollO = 0;
    private float particleRoll = 0;

    private float particleQuadSize;

    public GravityOptionsParticle(GravityParticleOptions gravityParticleOptions, ClientLevel leve, double x, double y, double z, double xd, double yd, double zd) {
        super(leve, x, y, z, xd, yd, zd);
        this.options = gravityParticleOptions;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.roll = leve.random.nextFloat() * FDMathUtil.FPI * 2;
        this.particleRoll = this.roll;
        this.particleRollO = this.roll;
        this.gravity = gravityParticleOptions.getGravity();
        this.quadSize = options.getQuadSize();
        this.particleQuadSize = gravityParticleOptions.getQuadSize();
        this.lifetime = options.getLifetime();
        this.hasPhysics = true;
    }

    @Override
    public void tick() {

        super.tick();

        Vec3 v = new Vec3(
                this.xo - x,
                this.yo - y,
                this.zo - z
        );

        float length = (float) v.length();

        this.particleRollO = this.particleRoll;
        this.particleRoll += length * options.getRotationModifier();

    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float pticks) {
        this.roll = particleRoll;
        this.oRoll = particleRollO;
        if (options.isFadeOut()) {
            this.quadSize = this.particleQuadSize * (1 - (this.age + pticks) / this.lifetime);
        }
        super.render(consumer, camera, pticks);
    }

}
