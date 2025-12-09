package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBar;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class GeburahBossBar extends FDBossBar {

    public static final ResourceLocation GEBURAH_BOSSBAR = FDBosses.location("textures/boss_bars/geburah_bossbar.png");

    public GeburahBossBar(UUID uuid, int entityId) {
        super(uuid, entityId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float v) {

        int hp = (int) Math.ceil(this.getPercentage() * 10);

        FDRenderUtil.bindTexture(GEBURAH_BOSSBAR);

        float scale = 1.25f;
        float width = 287/scale;
        float height = 70/scale;

        FDRenderUtil.blitWithBlend(guiGraphics.pose(), -width/2,0,width,height,0,0,1,1,1,1,0,1f);

        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/geburah_sin_scales.png"));
        float scalesWidth = 18;
        float scalesOffset = 24;

        for (int i = 0; i < Math.min(hp,5); i++){
            FDRenderUtil.blitWithBlend(guiGraphics.pose(), 9/scale + -width/2 + i * scalesOffset / scale, 30 / scale, scalesWidth / scale, 15 / scale, 0,0,1,1,1,1,0,1);
        }

        RenderSystem.disableCull();
        for (int i = 0; i < Math.min(hp - 5,5); i++){
            FDRenderUtil.blitWithBlend(guiGraphics.pose(), -8/scale + width/2 - i * scalesOffset / scale, 30 / scale, -scalesWidth / scale, 15 / scale, 0,0,1,1,1,1,0,1);
        }
        RenderSystem.enableCull();


    }

    @Override
    public void tick(float v) {

    }

    @Override
    public float height() {
        return 70;
    }

    @Override
    public void hanldeBarEvent(int i, int i1) {

    }

}
