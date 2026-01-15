package com.finderfeed.fdbosses.client.overlay;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GeburahSinsOverlay implements IGuiOverlay {

    public static final ResourceLocation SINS = FDBosses.location("textures/gui/geburah_sin.png");

    private static int ticker = 0;
    private static int oldSinCount = 0;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {

        if (Minecraft.getInstance().player == null || Minecraft.getInstance().options.hideGui || !Minecraft.getInstance().player.hasEffect(BossEffects.SINNER.get())) return;

        float pticks = partialTick;

        Window window = Minecraft.getInstance().getWindow();
        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        PlayerSins playerSins = PlayerSins.getPlayerSins(Minecraft.getInstance().player);

        int maxSins = BossConfigs.BOSS_CONFIG.get().geburahConfig.maxPlayerSins;

        int sinnedTimes = playerSins.getSinnedTimes();

        float h = height / 2;
        float sinHeight = 25;

        float xOffset = 2.5f;
        if (ticker < 20){
            float p = Mth.clamp(ticker - pticks,0, Integer.MAX_VALUE) / 20f;
            xOffset -= FDEasings.easeInOut(1 - p) * 32;
        }

        float startOffset;
        if (maxSins % 2 == 0){
            startOffset = sinHeight * (maxSins / 2);
        }else{
            startOffset = sinHeight / 2 + sinHeight * (maxSins / 2);
        }

        for (int i = 0; i < maxSins; i++){
            float texOffset = i < sinnedTimes ? 0.5f : 0;
            FDRenderUtil.bindTexture(SINS);
            FDRenderUtil.blitWithBlend(guiGraphics.pose(), xOffset, h - startOffset + i * sinHeight, 28, 33, 0,texOffset,1,0.5f,1,1,0,1f);
        }

    }


    @SubscribeEvent
    public static void tickEvent(TickEvent.ClientTickEvent event){

        if (event.phase != TickEvent.Phase.START) return;

        if (Minecraft.getInstance().player == null){
            ticker = 0;
            oldSinCount = 0;
            return;
        }

        PlayerSins playerSins = PlayerSins.getPlayerSins(Minecraft.getInstance().player);
        int sinnedTimes = playerSins.getSinnedTimes();
        if (sinnedTimes != oldSinCount){
            oldSinCount = sinnedTimes;
            ticker = 100;
        }

        ticker = Mth.clamp(ticker - 1,0,Integer.MAX_VALUE);

    }




}
