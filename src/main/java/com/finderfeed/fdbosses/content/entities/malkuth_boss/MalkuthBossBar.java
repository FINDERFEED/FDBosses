package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBarInterpolated;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

import java.util.UUID;

public class MalkuthBossBar extends FDBossBarInterpolated {

    public MalkuthBossBar(UUID uuid, int entityId) {
        super(uuid, entityId, 10);
    }

    @Override
    public void renderInterpolatedBossBar(GuiGraphics graphics, float pticks, float interpolatedPercent) {

        PoseStack matrices = graphics.pose();

        FDRenderUtil.fill(matrices, -101, -1, 202, 12, 0f, 0f, 0f,1f);
        FDRenderUtil.fill(matrices, -100, 0, 200 * interpolatedPercent, 10, 1f, 0f, 0f,1f);

    }

    @Override
    public float height() {
        return 40;
    }

    @Override
    public void hanldeBarEvent(int eventId, int data) {

    }

}
