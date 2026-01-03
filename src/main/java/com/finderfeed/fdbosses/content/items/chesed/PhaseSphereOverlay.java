package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossCoreShaders;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDShaderRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class PhaseSphereOverlay implements LayeredDraw.Layer {

    private static Boolean smartCull;

    public static final int MAX_PROGRESS = 20;
    private static int progress;
    private static int progressO;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {

        if (Minecraft.getInstance().level == null || progress == 0) return;

        var shader = BossCoreShaders.CHESED_ITEM_OVERLAY;
        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        float pticks = tracker.getGameTimeDeltaPartialTick(false);
        float gametime = Minecraft.getInstance().level.getGameTime() % 2000000;
        float time = gametime + pticks;

        float progress = FDMathUtil.lerp(progressO, PhaseSphereOverlay.progress, pticks) / MAX_PROGRESS;

        FDShaderRenderer.start(graphics, shader)
                .setShaderUniform("progress", FDEasings.easeOut(progress))
                .setShaderUniform("size", width, height)
                .setShaderUniform("time",time / 200)
                .setResolution(width,height)
                .end();
        RenderSystem.defaultBlendFunc();

    }

    @SubscribeEvent
    public static void tickClient(ClientTickEvent.Post event){

        Player player = Minecraft.getInstance().player;

        if (progress == 0){
            if (smartCull != null){
                Minecraft.getInstance().smartCull = smartCull;
                smartCull = null;
            }
        }else{
            if (PhaseSphereOverlay.smartCull == null){
                PhaseSphereOverlay.smartCull = Minecraft.getInstance().smartCull;
            }
            Minecraft.getInstance().smartCull = false;
        }

        if (player == null) {
            progress = 0;
            return;
        }

        progressO = progress;

        if (player.getUseItem().is(BossItems.PHASE_SPHERE.get())){
            progress = Mth.clamp(progress + 1, 0, MAX_PROGRESS);
        }else{
            progress = Mth.clamp(progress - 1, 0, MAX_PROGRESS);
        }

    }



}
