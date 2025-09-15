package com.finderfeed.fdbosses.client.overlay;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthWeaknessHandler;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner.MalkuthBossSpawner;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.systems.screen.screen_particles.ScreenParticlesRenderEvent;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticle;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MalkuthWeaknessOverlay implements LayeredDraw.Layer {

    public static Random random = new Random();

    public static final int MAX_IN_TIME = 10;

    private static int iceTicker = 0;
    private static int fireTicker = 0;

    private static int iceTickerO = 0;
    private static int fireTickerO = 0;

    private static int alpha = 0;
    private static int alphaO = 0;

    @SubscribeEvent
    public static void tickClient(ClientTickEvent.Pre event){
        Player player = FDClientHelpers.getClientPlayer();
        if (player == null) {
            iceTicker = 0;
            fireTicker = 0;
            return;
        }

        boolean spawnerNearby = hasMalkuthSpawnerNearby();

        alphaO = alpha;
        if (spawnerNearby){
            alpha = Mth.clamp(alpha + 1,0, MAX_IN_TIME);
        }else{
            alpha = Mth.clamp(alpha - 1,0, MAX_IN_TIME);
        }

        MalkuthAttackType weakTo = MalkuthWeaknessHandler.getWeakTo(player);

        iceTickerO = iceTicker;
        fireTickerO = fireTicker;

        if (weakTo.isIce()){
            iceTicker = Math.clamp(iceTicker + 1, 0, MAX_IN_TIME);
            fireTicker = Math.clamp(fireTicker - 1, 0, MAX_IN_TIME);
        }else{
            iceTicker = Math.clamp(iceTicker - 1, 0, MAX_IN_TIME);
            fireTicker = Math.clamp(fireTicker + 1, 0, MAX_IN_TIME);
        }


        if (spawnerNearby) {
            Window window = Minecraft.getInstance().getWindow();
            float w = window.getGuiScaledWidth();
            float h = window.getGuiScaledHeight();

            for (int i = 0; i < 5; i++) {

                Vector3f color = MalkuthEntity.getAndRandomizeColor(weakTo, player.level().random);

                float x = random.nextFloat() * w;
                float y = h;

                FDTexturedSParticle.create(FDRenderUtil.ParticleRenderTypesS.TEXTURES_BLUR_ADDITIVE, BallParticle.LOCATION)
                        .setPos(x, y, true)
                        .setMaxQuadSize(3.5f)
                        .setSpeed(0, -0.4)
                        .setFriction(1f)
                        .setColor(
                                color.x, color.y, color.z, 0.8f
                        )
                        .setLifetime(30)
                        .setDefaultScaleOut()
                        .sendToOverlay();
            }
        }

    }

    private static boolean hasMalkuthSpawnerNearby(){

        Player player = Minecraft.getInstance().player;

        if (player == null) return false;

        return !BossTargetFinder.getEntitiesInCylinder(MalkuthBossSpawner.class, player.level(), player.position().add(0,-40,0), 80, 130).isEmpty();
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {

        if (FDClientHelpers.getClientLevel() == null || Minecraft.getInstance().options.hideGui) return;

        Window window = Minecraft.getInstance().getWindow();

        float w = window.getGuiScaledWidth();
        float h = window.getGuiScaledHeight();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);


        Vector3f ice = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.ICE);
        Vector3f fire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.FIRE);

        float pticks = tracker.getGameTimeDeltaPartialTick(false);

        float basicAlpha = FDMathUtil.lerp(alphaO,alpha,pticks) / MAX_IN_TIME;

        if (basicAlpha == 0) return;

        float iceAlpha = FDMathUtil.lerp(iceTickerO,iceTicker,pticks) / MAX_IN_TIME;
        float fireAlpha = FDMathUtil.lerp(fireTickerO,fireTicker,pticks) / MAX_IN_TIME;

        float size = 5;


        PoseStack matrices = graphics.pose();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);


        for (int i = 0; i < 2;i++){
            matrices.pushPose();

            matrices.translate(w/2,h - size/2,0);

            QuadRenderer.start(builder)
                .pose(matrices)
                .direction(new Vec3(0,0,-1))
                .color1(ice.x,ice.y,ice.z,iceAlpha * basicAlpha)
                .color2(ice.x,ice.y,ice.z,iceAlpha * basicAlpha)
                .color3(ice.x,ice.y,ice.z,0)
                .color4(ice.x,ice.y,ice.z,0)
                .sizeX(w/2)
                .sizeY(size)
                .render();


            QuadRenderer.start(builder)
                    .pose(matrices)
                    .direction(new Vec3(0, 0, -1))
                    .color1(fire.x, fire.y, fire.z, fireAlpha * basicAlpha)
                    .color2(fire.x, fire.y, fire.z, fireAlpha * basicAlpha)
                    .color3(fire.x, fire.y, fire.z, 0)
                    .color4(fire.x, fire.y, fire.z, 0)
                    .sizeX(w/2)
                    .sizeY(size)
                    .render();

            matrices.popPose();
        }

        BufferUploader.drawWithShader(builder.build());

        matrices.pushPose();
        matrices.translate(w/2 - 0.5f,h/2 - 0.5f,0);

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.FIRE);
        Vector3f fire1 = new Vector3f(colFire.x,colFire.y - 0.25f,colFire.z);
        Vector3f fire2 = new Vector3f(colFire.x,colFire.y,colFire.z);

        Vector3f colIce = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.ICE);
        Vector3f ice1 = new Vector3f(colIce.x - 0.1f,colIce.y - 0.6f,colIce.z);
        Vector3f ice2 = new Vector3f(colIce.x,colIce.y,colIce.z);

        float innerRadius = 1.5f;
        float radius = 7.5f;

        float time = FDClientHelpers.getClientLevel().getGameTime() + pticks;


        matrices.mulPose(Axis.ZP.rotationDegrees(time * 10));
        float sin = (float) Math.sin(time / 3) / 2 + 0.5f;

        sin = sin * 0.5f;

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawCircle(new Vector3f(),new Vector3f(),matrices, radius,innerRadius, basicAlpha,1);

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawCircle(ice1,ice2,matrices, radius,innerRadius, iceAlpha * basicAlpha,3);
        drawCircle(ice1,ice2,matrices, radius,innerRadius, iceAlpha * basicAlpha * sin,2);

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawCircle(fire1,fire2,matrices, radius,innerRadius, fireAlpha * basicAlpha,3);
        drawCircle(fire1,fire2,matrices, radius,innerRadius, fireAlpha * basicAlpha * sin,2);


        matrices.popPose();

        RenderSystem.defaultBlendFunc();

    }

    public static void drawCircle(Vector3f c1, Vector3f c2, PoseStack matrices, float radius, float innerRadius,float a, int renderAmount){
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();

        BufferBuilder vertex = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);


        Vec3 v = new Vec3(radius,0,0);

        int count = 48;

        float angle = FDMathUtil.FPI * 2 / count;

        Matrix4f m = matrices.last().pose();


        for (int i = 0; i < count; i++){

            float p = i / (float) count;
            float sinp = (float) Math.sin(FDMathUtil.FPI * 2 * p * 3) / 2 + 0.5f;

            float p2 = (i + 1f) / count;
            float sinp2 = (float) Math.sin(FDMathUtil.FPI * 2 * p2 * 3) / 2 + 0.5f;

            Vector3f color1 = FDMathUtil.interpolateVectors( c1, c2, sinp);
            Vector3f color2 = FDMathUtil.interpolateVectors( c1, c2, sinp2);

            float currentAngle = angle * i;
            float nextAngle = angle * (i + 1);

            Vec3 offset = v.zRot(currentAngle);
            Vec3 noffset = offset.normalize().multiply(innerRadius,innerRadius,innerRadius);

            Vec3 offset2 = v.zRot(nextAngle);
            Vec3 noffset2 = offset2.normalize().multiply(innerRadius,innerRadius,innerRadius);

            for (int g = 0; g < renderAmount;g++) {
                vertex.addVertex(m, (float) (offset.x - noffset.x), (float) (offset.y - noffset.y), 0).setColor((float) color1.x, (float) color1.y, (float) color1.z, 0f);
                vertex.addVertex(m, (float) offset.x, (float) offset.y, 0).setColor((float) color1.x, (float) color1.y, (float) color1.z, a);

                vertex.addVertex(m, (float) offset2.x, (float) offset2.y, 0).setColor((float) color2.x, (float) color2.y, (float) color2.z, a);
                vertex.addVertex(m, (float) (offset2.x - noffset2.x), (float) (offset2.y - noffset2.y), 0).setColor((float) color2.x, (float) color2.y, (float) color2.z, 0f);


                vertex.addVertex(m, (float) offset.x, (float) offset.y, 0).setColor((float) color1.x, (float) color1.y, (float) color1.z, a);
                vertex.addVertex(m, (float) (offset.x + noffset.x), (float) (offset.y + noffset.y), 0).setColor((float) color1.x, (float) color1.y, (float) color1.z, 0f);

                vertex.addVertex(m, (float) (offset2.x + noffset2.x), (float) (offset2.y + noffset2.y), 0).setColor((float) color2.x, (float) color2.y, (float) color2.z, 0f);
                vertex.addVertex(m, (float) offset2.x, (float) offset2.y, 0).setColor((float) color2.x, (float) color2.y, (float) color2.z, a);
            }




        }

//        vertex.addVertex(m, (float) radius,(float) 0,0).setColor(1f,1f,1f,1f);
//        vertex.addVertex(m, (float) radius - innerRadius,(float) 0,0).setColor(1f,1f,1f,1f);


        BufferUploader.drawWithShader(vertex.build());

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();

    }


}
