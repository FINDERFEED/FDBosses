package com.finderfeed.fdbosses.content.entities.netzach;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.util.BossVignetteHandler;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix4f;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class NetzachEffectOverlay implements LayeredDraw.Layer {

    public static final ResourceLocation TAPE = FDBosses.location("textures/entities/netzach/tape.png");
    public static final ResourceLocation BIG_GEAR = FDBosses.location("textures/gui/netzach_minigame/netzach_gear_big.png");
    public static final ResourceLocation MEDIUM_GEAR = FDBosses.location("textures/gui/netzach_minigame/netzach_gear_medium.png");
    public static final ResourceLocation SMALL_GEAR = FDBosses.location("textures/gui/netzach_minigame/netzach_gear_small.png");

    public static PostChain EFFECT;

    private static int effectActiveTime = -1;
    private static int maxFlashTime;
    private static int flashTime;
    private static int flashTimeO;
    private static int maxEffectTime;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {
        if (effectActiveTime > 0) {

            var screen = Minecraft.getInstance().getWindow();
            var width = screen.getGuiScaledWidth();
            var height = screen.getGuiScaledHeight();

            Tesselator tesselator = Tesselator.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            FDRenderUtil.bindTexture(TAPE);
            var vertex = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


            Matrix4f mat = graphics.pose().last().pose();

            float pticks = tracker.getGameTimeDeltaPartialTick(false);

            float ptime = Mth.clamp(1 - (effectActiveTime - pticks) / maxEffectTime,0,1);

            float effectTimeP;
            if (ptime < 0.5){
                effectTimeP = FDEasings.easeOut(ptime / 0.5f);
            }else{
                effectTimeP = FDEasings.easeOut((1 - ptime) / 0.5f);
            }

            var guiScale = (float) screen.getGuiScale();

            float guiScaleMultiplier = 5 / guiScale;

            float uMove = effectTimeP * 20;
            float tapeDimensions = 16 * guiScaleMultiplier;
            float u = width / tapeDimensions / 2;


            float a = 0.8f;
            vertex.addVertex(mat, 0, tapeDimensions, 0).setUv(0 + uMove, 1).setColor(1f, 1f, 1f, a);
            vertex.addVertex(mat, width, tapeDimensions, 0).setUv(u + uMove, 1).setColor(1f, 1f, 1f, a);
            vertex.addVertex(mat, width, -tapeDimensions, 0).setUv(u + uMove, 0).setColor(1f, 1f, 1f, a);
            vertex.addVertex(mat, 0, -tapeDimensions, 0).setUv(0 + uMove, 0).setColor(1f, 1f, 1f, a);

            vertex.addVertex(mat, 0, tapeDimensions + height, 0).setUv(0 - uMove, 1).setColor(1f, 1f, 1f, a);
            vertex.addVertex(mat, width, tapeDimensions + height, 0).setUv(u - uMove, 1).setColor(1f, 1f, 1f, a);
            vertex.addVertex(mat, width, -tapeDimensions + height, 0).setUv(u - uMove, 0).setColor(1f, 1f, 1f, a);
            vertex.addVertex(mat, 0, -tapeDimensions + height, 0).setUv(0 - uMove, 0).setColor(1f, 1f, 1f, a);

            BufferUploader.drawWithShader(vertex.build());

            float bigGearSize = 128 * guiScaleMultiplier;
            float mediumGearSize = 64 * guiScaleMultiplier;
            float smallGearSize = 32 * guiScaleMultiplier;

            addGear(MEDIUM_GEAR, graphics, 80 * guiScaleMultiplier, height, mediumGearSize, mediumGearSize, effectTimeP, -2400);
            addGear(MEDIUM_GEAR, graphics, width - 80 * guiScaleMultiplier, height, mediumGearSize, mediumGearSize, effectTimeP, 2400);

            addGear(BIG_GEAR, graphics, 0, height, bigGearSize, bigGearSize, effectTimeP, 1200);
            addGear(BIG_GEAR, graphics, width, height, bigGearSize, bigGearSize, effectTimeP, -1200);


            addGear(MEDIUM_GEAR, graphics, 0, 0, mediumGearSize, mediumGearSize, effectTimeP, 1200);
            addGear(MEDIUM_GEAR, graphics, width, 0, mediumGearSize, mediumGearSize, effectTimeP, -1200);

            addGear(SMALL_GEAR, graphics, 40 * guiScaleMultiplier, 0, smallGearSize, smallGearSize, effectTimeP, -1200);
            addGear(SMALL_GEAR, graphics, width - 40 * guiScaleMultiplier, 0, smallGearSize, smallGearSize, effectTimeP, 1200);


            RenderSystem.disableBlend();
        }
    }

    private void addGear(ResourceLocation texture, GuiGraphics graphics, float x, float y, float w, float h, float effectTimeP, float angle){
        PoseStack matrices = graphics.pose();

        matrices.pushPose();
        FDRenderUtil.bindTexture(texture);
        matrices.translate(x, y, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(effectTimeP * angle));
        FDRenderUtil.blitWithBlendCentered(matrices, 0, 0, w, h, 0, 0, 1, 1, 1, 1, 0, 1);

        matrices.popPose();
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Pre event) {
        if (!Minecraft.getInstance().isPaused()) {
            if (effectActiveTime > 0) {
                flashTimeO = flashTime;
                flashTime = Mth.clamp(flashTime - 1, 0, Integer.MAX_VALUE);
                effectActiveTime = Mth.clamp(effectActiveTime - 1, 0, Integer.MAX_VALUE);
            }else if (effectActiveTime == 0){
                BossVignetteHandler.setCurrentVignette(0,2,20,1f,1f,1f,1f);
                effectActiveTime = -1;
            }
        }
    }

    @SubscribeEvent
    public static void registerShader(FDPostShaderInitializeEvent event){
        event.registerPostChain(((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChain(textureManager, resourceProvider, renderTarget, FDBosses.location("shaders/post/netzach_mexico.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), chain -> {
            EFFECT = chain;
        });
    }

    @SubscribeEvent
    public static void processEffect(FDRenderPostShaderEvent.Level event) {
        if (effectActiveTime > 0) {
            event.doDefaultShaderBeforeShaderStuff();
            float percentageTime = FDMathUtil.lerp(flashTimeO, flashTime, event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
            float p = FDEasings.easeIn(percentageTime / maxFlashTime);

            float time = Minecraft.getInstance().level.getGameTime() + event.getDeltaTracker().getGameTimeDeltaPartialTick(false);

            EFFECT.setUniform("percent", p);
            EFFECT.setUniform("time", time);

            EFFECT.process(event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        }
    }

    public static void flash(int effectTime, int flashTime){
        maxFlashTime = flashTime;
        effectActiveTime = effectTime;
        maxEffectTime = effectTime;
        NetzachEffectOverlay.flashTime = flashTime;
        NetzachEffectOverlay.flashTimeO = flashTime;
    }


}
