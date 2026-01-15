package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossCoreShaders;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDShaderRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhaseSphereOverlay implements IGuiOverlay {

    private static Boolean smartCull;

    public static final int MAX_PROGRESS = 20;
    private static int progress;
    private static int progressO;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {

        if (Minecraft.getInstance().level == null || progress == 0) return;

        var shader = BossCoreShaders.CHESED_ITEM_OVERLAY;
        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        float pticks = partialTick;
        float gametime = Minecraft.getInstance().level.getGameTime() % 2000000;
        float time = gametime + pticks;

        float progress = FDMathUtil.lerp(progressO, PhaseSphereOverlay.progress, pticks) / MAX_PROGRESS;

        FDShaderRenderer.start(guiGraphics, shader)
                .setShaderUniform("progress", FDEasings.easeOut(progress))
                .setShaderUniform("size", width, height)
                .setShaderUniform("time",time / 200)
                .setResolution(width,height)
                .end();
        RenderSystem.defaultBlendFunc();

    }

    @SubscribeEvent
    public static void tickClient(TickEvent.ClientTickEvent event){

        if (event.phase != TickEvent.Phase.END) return;

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
