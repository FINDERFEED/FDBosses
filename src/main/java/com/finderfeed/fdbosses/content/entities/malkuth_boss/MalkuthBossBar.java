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
    public static final ResourceLocation MAIN_GLOW = FDBosses.location("textures/boss_bars/malkuth_bossbar_glow.png");
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

        FDRenderUtil.bindTexture(MAIN_GLOW);
        FDRenderUtil.blitWithBlend(matrices, -width / 2, -verticalOffset, width, height, 0,0,1,1,1,1,0,1f);

        float t = (time + FDRenderUtil.tryGetPartialTickIgnorePause()) / 100f;

        float xHPPos = -width/2 + 14.1f;
        float yHPPos = -verticalOffset + 33;

        FDRenderUtil.Scissor.pushScissors(matrices,xHPPos, yHPPos, 189 * interpolatedPercent, 5);

        FDShaderRenderer.start(graphics, BossCoreShaders.MALKUTH_BOSS_BAR)
                .position(xHPPos,yHPPos,0.5f)
                .setResolution(189,5)
                .setUVSpan(12f,0.3f)
                .setShaderUniform("xyOffset",0f,0f)
                .setShaderUniform("time",t)
                .setShaderUniform("uvSpan",12f,0.3f)
                .end();

        FDRenderUtil.Scissor.popScissors();


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
