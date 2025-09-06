package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;

import java.util.List;

public class SubscribersScreen extends SimpleFDScreen {

    private List<String> subscribers;
    private Component subscribersName;

    //TODO: Once enough money for server - get subscribers directly from patreon and boosty
    public SubscribersScreen(Component subscribersName, List<String> subscribers){
        this.subscribers = subscribers;
        this.subscribersName = subscribersName;
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {
        super.render(graphics, mx, my, pticks);

        this.renderBackground(graphics, mx, my, pticks);

        BossRenderUtil.renderBossScreenTooltip(graphics.pose(), this.relX - BossRenderUtil.EDGE_SIZE - 1, this.relY - BossRenderUtil.EDGE_SIZE - 1, this.getScreenWidth(), this.getScreenHeight());


        float xt = this.relX + this.getScreenWidth() / 2;
        float yt = this.relY + 5;

        FDRenderUtil.renderCenteredText(graphics, xt, yt, 2f, true,subscribersName.getString(), BaseBossScreen.DEFAULT_TEXT_COLOR);

        for (int i = 0; i < subscribers.size(); i++){

            String subscriber = subscribers.get(i);

            float x = this.relX + this.getScreenWidth() / 2;
            float y = this.relY + 30 + i * (font.lineHeight + 1);

            FDRenderUtil.renderCenteredText(graphics, x, y, 1f, false, subscriber, BaseBossScreen.DEFAULT_TEXT_COLOR);


        }

        Vector2f v = this.getAnchor(0.5f,0f);

        FDRenderUtil.renderFullyCenteredText(graphics, v.x, v.y + 15, 250, 1.25f, true, BaseBossScreen.DEFAULT_TEXT_COLOR, Component.translatable("fdbosses.word.not_updated"));

    }

    @Override
    public float getScreenWidth() {
        return 150;
    }

    @Override
    public float getScreenHeight() {
        return 200;
    }
}
