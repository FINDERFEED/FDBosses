package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner.MalkuthBossSpawner;
import com.finderfeed.fdbosses.content.util.GainLoseValue;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;


@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class BossClientEvents {

    public static Double cachedGamma = null;

    public static int chesedGazeEffectTick = 0;
    public static int chesedGazeEffectTickO = 0;
    public static int chesedGazeEffectTickMax = 20;

    public static int chesedDarkenEffectTick = 0;
    public static int chesedDarkenEffectTickO = 0;
    public static int chesedDarkenEffectTickMax = 10;

    private static GainLoseValue hellscapeSkyValue = new GainLoseValue(0,100);


    @SubscribeEvent
    public static void collectTooltips(ItemTooltipEvent event){
        var componentList = event.getToolTip();
        ItemStack itemStack = event.getItemStack();
        if (itemStack.has(BossDataComponents.ITEM_CORE.get())){
            ItemCoreDataComponent itemCoreDataComponent = itemStack.get(BossDataComponents.ITEM_CORE);
            ItemCoreDataComponent.CoreType coreType = itemCoreDataComponent.getCoreType();
            Item item = coreType.getItem();
            componentList.add(item.getDefaultInstance().getHoverName().copy().withStyle(Style.EMPTY.withColor(coreType.getTextColor())));
        }
    }

    @SubscribeEvent
    public static void tickEvent(ClientTickEvent.Pre event){
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            tickChesedGaze(player);
            tickChesedDarken(player);
            lowerGammaWhenAnyDarkenEffect(player);
        }
        tickHellscapeSky();
    }

    private static void lowerGammaWhenAnyDarkenEffect(Player player){
        Options options = Minecraft.getInstance().options;
        if (player.hasEffect(MobEffects.NIGHT_VISION) && player.hasEffect(BossEffects.CHESED_DARKEN)){
            if (cachedGamma == null){
                cachedGamma = options.gamma().get();
            }
            options.gamma().set(0d);
        }else{
            if (cachedGamma != null){
                options.gamma().set(cachedGamma);
                cachedGamma = null;
            }
        }
    }

    private static void tickChesedGaze(Player player){
        if (player != null){
            chesedGazeEffectTickO = chesedGazeEffectTick;
            if (player.hasEffect(BossEffects.CHESED_GAZE)){
                chesedGazeEffectTick = Mth.clamp(chesedGazeEffectTick + 1,0,chesedGazeEffectTickMax);
            }else{
                chesedGazeEffectTick = Mth.clamp(chesedGazeEffectTick - 1,0,chesedGazeEffectTickMax);
            }
        }else{
            chesedGazeEffectTick = 0;
        }
    }

    private static void tickChesedDarken(Player player){
        if (player != null){
            chesedDarkenEffectTickO = chesedDarkenEffectTick;
            if (player.hasEffect(BossEffects.CHESED_DARKEN)){
                chesedDarkenEffectTick = Mth.clamp(chesedDarkenEffectTick + 1,0,chesedDarkenEffectTickMax);
            }else{
                chesedDarkenEffectTick = Mth.clamp(chesedDarkenEffectTick - chesedDarkenEffectTickMax / 4,0,chesedDarkenEffectTickMax);
            }
        }else{
            chesedDarkenEffectTick = 0;
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event){

        if (chesedGazeEffectTick != 0) {
            chesedGazeEffectTickMax = 20;
            float p = getChesedGazePercent(event.getPartialTick());


            float r = event.getRed();
            float g = event.getGreen();
            float b = event.getBlue();

            event.setRed(r * (1 - p));
            event.setGreen(g * (1 - p));
            event.setBlue(b * (1 - p));

        }

    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event){


        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        if (chesedGazeEffectTick != 0) {
            event.setCanceled(true);

            float levelTime = (float) ((level.getGameTime() + event.getPartialTick()) % 120);
            levelTime /= 120;


            float p = getChesedGazePercent(event.getPartialTick());




            float farPlane = event.getFarPlaneDistance();
            float nearPlane = event.getNearPlaneDistance();
            event.setFogShape(FogShape.SPHERE);

            float farPlaneDistance = farPlane * (1 - p) + p * (2  * ((float)Math.sin(levelTime * FDMathUtil.FPI * 2) + 1) / 2 + 3);


            float nearPlaneDistance = nearPlane * (1 - p);

            event.setFarPlaneDistance(farPlaneDistance);
            event.setNearPlaneDistance(nearPlaneDistance);

        }
    }

    public static float getChesedGazePercent(double pticks){
        float time = (float) Mth.lerp(pticks,chesedGazeEffectTickO,chesedGazeEffectTick);
        float p = FDEasings.easeOut(time / chesedGazeEffectTickMax);
        return p;
    }

    public static float getChesedDarkenPercent(double pticks){
        float time = (float) Mth.lerp(pticks,chesedDarkenEffectTickO,chesedDarkenEffectTick);
        float p = FDEasings.easeOut(time / chesedDarkenEffectTickMax);
        return p;
    }





    @SubscribeEvent
    public static void renderLevelStageEvent(RenderLevelStageEvent event){
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL){
            renderHellscapeSkybox(event);
        }else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS){
            renderMalkuthCowardExecution(event);
        }
    }

    private static FDModel SWORD_MODEL;
    public static final ResourceLocation MALKUTH_FIRE_SWORD = FDBosses.location("textures/item/malkuth_sword_fire.png");
    private static final ResourceLocation MALKUTH_COWARD_EXECUTION_SQUARE = FDBosses.location("textures/util/malkuth_cowardice_prepare.png");
    private static void renderMalkuthCowardExecution(RenderLevelStageEvent event){

        float pticks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        Player player = Minecraft.getInstance().player;

        if (player == null || player.isCreative() || player.isSpectator()) return;

        Vec3 forward = player.getForward().multiply(1,0,1).normalize();

        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 offsetPos = player.getPosition(pticks);

        PoseStack matrices = event.getPoseStack();

        Vec3 offset = offsetPos.subtract(camPos);

        if (!player.hasEffect(BossEffects.MARK_OF_A_COWARD)) return;

        var instance = player.getEffect(BossEffects.MARK_OF_A_COWARD);

        int duration = instance.getDuration();


        int maxtime = MalkuthEntity.Events.MARK_OF_A_COWARD_DURATION;
        int maxtime2 = 7;

        float time = Mth.clamp(duration - pticks, 0,maxtime);
        float time2 = Mth.clamp(duration - pticks, 0,maxtime2);


        float p = time / maxtime;
        float p2 = time2 / maxtime2;

        if (p == 0) return;


        float easeOut = FDEasings.easeIn(1 - p);
        float easeInP2 = FDEasings.easeOutBack(p2);

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


        FDRenderUtil.bindTexture(MALKUTH_COWARD_EXECUTION_SQUARE);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        matrices.pushPose();

        matrices.translate(offset.x,offset.y + 0.001,offset.z);

        matrices.mulPose(Axis.YP.rotationDegrees(easeOut * 360 * 6));


        QuadRenderer.start(builder)
                .pose(matrices)
                .size((1f - p) * 2)
                .color(1,1,1,1 - p)
                .direction(new Vec3(0,1,0))
                .renderBack()
                .render();
        ;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        BufferUploader.drawWithShader(builder.build());
        matrices.popPose();

        RenderSystem.disableDepthTest();

        if (SWORD_MODEL == null){
            SWORD_MODEL = new FDModel(BossModels.MALKUTH_SWORD.get());
        }

        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        FDRenderUtil.bindTexture(MALKUTH_FIRE_SWORD);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        matrices.pushPose();

        float offsetP = -3f * (1 - easeInP2);

        matrices.translate(offset.x + forward.x * 0.1,offset.y + 5 + offsetP,offset.z + forward.z * 0.1);
        matrices.mulPose(Axis.ZP.rotationDegrees(180));

        matrices.mulPose(Axis.YP.rotationDegrees(easeOut * 360 * 6));
        SWORD_MODEL.render(matrices, builder, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1 - p);

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        BufferUploader.drawWithShader(builder.build());
        matrices.popPose();

        RenderSystem.disableDepthTest();

    }

    private static void tickHellscapeSky(){

        Player player = Minecraft.getInstance().player;

        if (player  == null) {
            hellscapeSkyValue.reset();
            return;
        }

        float rad = 30;

        AABB box = new AABB(-rad,-rad,-rad,rad,rad,rad).move(player.position());

        Level level = player.level();

        long time = level.getDayTime() % 24000;
        if (time < 13000 || time > 23000){
            hellscapeSkyValue.backward();
            return;
        }

        var spawners = level.getEntitiesOfClass(MalkuthBossSpawner.class, box, e->{
            return e.position().distanceTo(player.position()) <= rad;
        });

        if (spawners.isEmpty()) {
            hellscapeSkyValue.backward();
        }else{
            hellscapeSkyValue.forward();
        }

    }

    public static final ResourceLocation HELL_FROST_LIGHT = FDBosses.location("textures/skyboxes/hellscape/hell_frost_light.png");
    public static final ResourceLocation HELL_FIRE_LIGHT = FDBosses.location("textures/skyboxes/hellscape/hell_light.png");

    private static void renderHellscapeSkybox(RenderLevelStageEvent event){
        float alpha;
        if ((alpha = hellscapeSkyValue.getPercent(event.getPartialTick().getGameTimeDeltaPartialTick(false))) == 0) return;



        PoseStack matrices = event.getPoseStack();

        Level level = Minecraft.getInstance().level;

        //back    *empty*
        //front   left
        //right   top
        //bottom  *empty*

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


        float time = (level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false));

        float flash = (float) (Math.sin(time * 0.025f) + 1) / 2;
        flash = flash * 0.8f + 0.2f;

        float flashCounter = (float) (Math.sin(time * 0.025f + FDMathUtil.FPI) + 1) / 2;
        flashCounter = flashCounter * 0.8f + 0.2f;


        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        FDRenderUtil.bindTexture(HELL_FIRE_LIGHT);

        matrices.pushPose();

        Matrix4f mat = event.getModelViewMatrix();
        matrices.mulPose(mat);
        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(-90));
        matrices.mulPose(Axis.XP.rotationDegrees( time * 0.015f));
        matrices.mulPose(Axis.YP.rotationDegrees( 20));


        renderSkybox(matrices, builder, 300, 1f, 1f ,1f, 0.5f * alpha);
        renderSkybox(matrices, builder, 300, 1f, 1f ,1f, flash * alpha);
        BufferUploader.drawWithShader(builder.build());
        matrices.popPose();


        matrices.pushPose();
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        FDRenderUtil.bindTexture(HELL_FROST_LIGHT);
        matrices.mulPose(Axis.XP.rotationDegrees( 180));
        matrices.mulPose(Axis.YP.rotationDegrees( time * 0.015f + 90));
        matrices.mulPose(Axis.ZP.rotationDegrees(20));



        renderSkybox(matrices, builder, 299f, 1f, 1f ,1f, 0.5f * alpha);
        renderSkybox(matrices, builder, 299f, 1f, 1f ,1f, flashCounter * 0.8f * alpha);
        BufferUploader.drawWithShader(builder.build());
        matrices.popPose();


        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.popPose();
    }


    private static void renderSkybox(PoseStack matrices, VertexConsumer vertexConsumer, float boxRadius, float r, float g, float b, float a){

        Matrix4f m = matrices.last().pose();


        //back (z is back)
        vertexConsumer.vertex(m, -boxRadius,-boxRadius,-boxRadius).color(r,g,b,a).uv(0,0).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,-boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,0).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,0.25f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,boxRadius,-boxRadius).color(r,g,b,a).uv(0,0.25f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);


        //front
        vertexConsumer.vertex(m, -boxRadius,boxRadius,boxRadius).color(r,g,b,a).uv(0.5f,0.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,boxRadius,boxRadius).color(r,g,b,a).uv(0.0f,0.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,-boxRadius,boxRadius).color(r,g,b,a).uv(0.0f,0.25f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,-boxRadius,boxRadius).color(r,g,b,a).uv(0.5f,0.25f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);

        //left
        vertexConsumer.vertex(m, boxRadius,-boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,0.25f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,-boxRadius,boxRadius).color(r,g,b,a).uv(1f,0.25f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,boxRadius,boxRadius).color(r,g,b,a).uv(1f,0.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,0.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);


        //right
        vertexConsumer.vertex(m, -boxRadius,boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,0.75f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,boxRadius,boxRadius).color(r,g,b,a).uv(0f,0.75f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,-boxRadius,boxRadius).color(r,g,b,a).uv(0f,0.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,-boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,0.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);


        //down (but for some reason its up)
        vertexConsumer.vertex(m, boxRadius,boxRadius,-boxRadius).color(r,g,b,a).uv(0f,1f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,boxRadius,boxRadius).color(r,g,b,a).uv(0f,.75f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,boxRadius,boxRadius).color(r,g,b,a).uv(0.5f,.75f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,boxRadius,-boxRadius).color(r,g,b,a).uv(0.5f,1f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);

        //up (but for some reason its down
        vertexConsumer.vertex(m, -boxRadius,-boxRadius,-boxRadius).color(r,g,b,a).uv(1f,.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, -boxRadius,-boxRadius,boxRadius).color(r,g,b,a).uv(1f,.75f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,-boxRadius,boxRadius).color(r,g,b,a).uv(0.5f,.75f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);
        vertexConsumer.vertex(m, boxRadius,-boxRadius,-boxRadius).color(r,g,b,a).uv(.5f,.5f).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY);


    }

}
