package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossCoreShaders;
import com.finderfeed.fdlib.init.FDCoreShaders;
import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBarInterpolated;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.finderfeed.fdlib.util.rendering.FDShaderRenderer;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class MalkuthBossBar extends FDBossBarInterpolated {

    //217 67
    public static final ResourceLocation MAIN = FDBosses.location("textures/boss_bars/malkuth_bossbar.png");
    public static final ResourceLocation ABOVE = FDBosses.location("textures/boss_bars/malkuth_bossbar_above.png");

    private int time;

    public MalkuthBossBar(UUID uuid, int entityId) {
        super(uuid, entityId, 10);
    }

    @Override
    public void renderInterpolatedBossBar(GuiGraphics graphics, float pticks, float interpolatedPercent) {

        PoseStack matrices = graphics.pose();



        float width = 217;
        float height = 67;

        float verticalOffset = 2;

        FDRenderUtil.bindTexture(MAIN);
        FDRenderUtil.blitWithBlend(matrices, -width / 2, -verticalOffset, width, height, 0,0,1,1,1,1,0,1f);

        float t = (time + pticks) / 20f;
//        FDShaderRenderer.start(graphics, BossCoreShaders.MALKUTH_BOSS_BAR)
//                .position(-width/2 + 14.1f,-verticalOffset + 33,0.5f)
//                .setResolution(189,5)
//                .setUVSpan(6f,2)
//                .setShaderUniform("time",t)
//                .end();
        FDShaderRenderer.start(graphics, BossCoreShaders.MALKUTH_BOSS_BAR)
                .position(0,0,0)
                .setResolution(200,200)
                .setUVSpan(10,10)
                .setShaderUniform("time",0f)
                .end();



        FDRenderUtil.bindTexture(ABOVE);
        FDRenderUtil.blitWithBlend(matrices, -width / 2, -verticalOffset, width, height, 0,0,1,1,1,1,1,1f);

    }

    @Override
    public void tick(float topOffset) {
        super.tick(topOffset);
        time++;
    }

    @Override
    public float height() {
        return 70;
    }

    @Override
    public void hanldeBarEvent(int eventId, int data) {

    }

}
