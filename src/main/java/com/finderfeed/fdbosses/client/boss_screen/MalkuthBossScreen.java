package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class MalkuthBossScreen extends BaseBossScreen{

    public MalkuthBossScreen(int bossSpawnerId, BossScreenOptions options) {
        super(bossSpawnerId, options);
    }

    @Override
    public Component getBossName() {
        return BossEntities.MALKUTH.get().getDescription();
    }

    @Override
    public int getBaseStringColor() {
        return 0x55ccff;
    }

    @Override
    protected void renderBoss(GuiGraphics graphics, float mx, float my, float pticks) {

    }

    @Override
    public float getScreenWidth() {
        return 0;
    }

    @Override
    public float getScreenHeight() {
        return 0;
    }
}
