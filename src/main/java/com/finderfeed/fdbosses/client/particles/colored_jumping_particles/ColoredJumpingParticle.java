package com.finderfeed.fdbosses.client.particles.colored_jumping_particles;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.FDLib;
import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.systems.trails.FDTrailDataGenerator;
import com.finderfeed.fdlib.systems.trails.FDTrailPoint;
import com.finderfeed.fdlib.systems.trails.FDTrailRenderer;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ColoredJumpingParticle extends Particle {

    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private boolean stoppedByCollision;

    public ColoredJumpingParticleOptions options;
    private FDTrailDataGenerator<ColoredJumpingParticle> trail;

    public int ignoreCollisionTicks = 0;

    public boolean disappearWhenSpeedIsLow = false;

    public ColoredJumpingParticle(ColoredJumpingParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
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

        this.gravity = options.gravity;

        this.disappearWhenSpeedIsLow = options.lifetime == -1;

        this.lifetime = options.lifetime;
        if (this.lifetime == -1){
            this.lifetime = Integer.MAX_VALUE;
        }

        this.trail = new FDTrailDataGenerator<>(
                ((coloredJumpingParticle, aFloat) -> {
                    return FDMathUtil.interpolateVectors(
                            new Vec3(coloredJumpingParticle.xo,coloredJumpingParticle.yo,coloredJumpingParticle.zo),
                            new Vec3(coloredJumpingParticle.x,coloredJumpingParticle.y,coloredJumpingParticle.z),
                            aFloat
                    );
                }),options.maxPointsInTrail,0.1f);
        this.friction = 1f;
        this.setSize(0.05f,0.05f);
    }

    @Override
    public void tick() {

        if (disappearWhenSpeedIsLow && new Vec3(xd,yd,zd).length() < 0.025f){
            this.remove();
            return;
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd = this.yd - 0.04 * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1;
                this.zd *= 1.1;
            }

            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
        this.ignoreCollisionTicks = Mth.clamp(ignoreCollisionTicks - 1,0,Integer.MAX_VALUE);
        trail.tick(this);
    }


    public void move(double xd, double yd, double zd) {
        if (!this.stoppedByCollision) {
            double d1 = yd;
            if (this.hasPhysics && (xd != 0.0 || yd != 0.0 || zd != 0.0) && xd * xd + yd * yd + zd * zd < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                Vec3 vec3 = this.handleCollision(xd,yd,zd);

                xd = vec3.x;
                yd = vec3.y;
                zd = vec3.z;
            }

            if (xd != 0.0 || yd != 0.0 || zd != 0.0) {
                this.setBoundingBox(this.getBoundingBox().move(xd, yd, zd));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(d1) >= 1.0E-5F && Math.abs(yd) < 1.0E-5F) {
                this.stoppedByCollision = true;
            }

            this.onGround = d1 != yd && d1 < 0.0;

        }
    }

    private Vec3 handleCollision(double xd, double yd, double zd){

        if (ignoreCollisionTicks > 0){
            return new Vec3(xd,yd,zd);
        }

        Vec3 oldShift = new Vec3(xd,yd,zd);
        Vec3 newShift = Entity.collideBoundingBox(null, oldShift, this.getBoundingBox(), this.level, List.of());

        if (!oldShift.equals(newShift)){

            Vec3 thisPos = this.getPos();

            Vec3 endPos = thisPos.add(xd * 2,yd * 2,zd * 2);

            ClipContext clipContext = new ClipContext(thisPos,endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());

            var result = level.clip(clipContext);

            Direction direction = result.getDirection();

            Vector3d shift = new Vector3d(xd,yd,zd).reflect(direction.getStepX(),direction.getStepY(),direction.getStepZ());



            float xMod = direction.getStepX() == 0 ? 1 : options.reflectionStrength;
            float yMod = direction.getStepY() == 0 ? 1 : options.reflectionStrength;
            float zMod = direction.getStepZ() == 0 ? 1 : options.reflectionStrength;

            if (Direction.Plane.HORIZONTAL.test(direction)){
                ignoreCollisionTicks = 0;
                if (shift.y > 0){
                    shift.y = -shift.y;
                }
            }

            this.xd = shift.x * xMod;
            this.yd = shift.y * yMod;
            this.zd = shift.z * zMod;



            return new Vec3(
                    shift.x  * xMod,
                    shift.y  * yMod,
                    shift.z  * zMod
            );
        }


        return newShift;
    }



    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        Vec3 pos = FDMathUtil.interpolateVectors(
                new Vec3(this.xo,this.yo,this.zo),
                new Vec3(this.x,this.y,this.z),
                pticks);

        Vec3 camPos = camera.getPosition();

        Vec3 translatePosition = pos.subtract(camPos);

        Matrix4f mat = new Matrix4f();
        mat.translate(
                (float) translatePosition.x,
                (float) translatePosition.y,
                (float) translatePosition.z
        );

        mat.rotate(camera.rotation());

        float radius = 0.01f;

        FDColor color = options.color;

        vertex.addVertex(mat, radius,radius,0).setColor(color.r,color.g,color.b,color.a);
        vertex.addVertex(mat, -radius,radius,0).setColor(color.r,color.g,color.b,color.a);
        vertex.addVertex(mat, -radius,-radius,0).setColor(color.r,color.g,color.b,color.a);
        vertex.addVertex(mat, radius,-radius,0).setColor(color.r,color.g,color.b,color.a);

        Trails.TO_RENDER_TRAILS_ON.add(this);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new FDParticleRenderType() {

        @Override
        public void end() {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.disableBlend();
        }

        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {

            RenderSystem.enableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }
    };

    public static class Factory implements ParticleProvider<ColoredJumpingParticleOptions> {

        @Override
        public Particle createParticle(ColoredJumpingParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            ColoredJumpingParticle particle = new ColoredJumpingParticle(options, level, x, y, z, xd, yd, zd);
            return particle;
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = FDBosses.MOD_ID)
    public static class Trails {

        public static List<ColoredJumpingParticle> TO_RENDER_TRAILS_ON = new ArrayList<>();

        @SubscribeEvent
        public static void renderTrails(RenderLevelStageEvent event){
            var stage = event.getStage();

            if (stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

            if (TO_RENDER_TRAILS_ON.isEmpty()) return;

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            PoseStack matrices = event.getPoseStack();

            for (var particle : TO_RENDER_TRAILS_ON){

                matrices.pushPose();

                Vec3 pos = FDMathUtil.interpolateVectors(
                        new Vec3(particle.xo,particle.yo,particle.zo),
                        new Vec3(particle.x,particle.y,particle.z),
                        event.getPartialTick().getGameTimeDeltaPartialTick(false)
                ).subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());

                matrices.translate(pos.x,pos.y,pos.z);

                FDTrailRenderer.renderTrail(particle, particle.trail, builder, matrices, 0.01f,4,30, event.getPartialTick().getGameTimeDeltaPartialTick(false),
                        particle.options.color,
                        particle.options.color
                );

                matrices.popPose();
            }



            var meshData = builder.build();

            if (meshData != null) {
                BufferUploader.drawWithShader(meshData);
            }

            TO_RENDER_TRAILS_ON.clear();

        }

    }

}
