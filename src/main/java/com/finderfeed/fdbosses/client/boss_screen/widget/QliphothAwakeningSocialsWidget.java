package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.FDButtonTextures;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.WidgetTexture;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.URISyntaxException;

public class QliphothAwakeningSocialsWidget extends FDWidget {

    public QliphothAwakeningSocialsWidget(Screen screen, float x, float y, float width, float height) {
        super(screen, x,y, width, height);
        this.initSocials();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, float mx, float my, float pticks) {
        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        PoseStack matrices = guiGraphics.pose();

        matrices.pushPose();
        matrices.translate(0,0,0);

        BossRenderUtil.renderBossScreenTooltip(guiGraphics.pose(), this.getX(), this.getY(),this.getWidth() - 14, this.getHeight() - 14);
        FDRenderUtil.renderCenteredText(guiGraphics, this.getX() + this.getWidth() / 2, this.getY() + 10, 1.5f, true, Component.translatable("fdbosses.word.socials_word").getString(), BaseBossScreen.DEFAULT_TEXT_COLOR);
        matrices.popPose();

    }

    @Override
    public boolean onMouseClick(float v, float v1, int i) {
        return false;
    }

    @Override
    public boolean onMouseRelease(float v, float v1, int i) {
        return false;
    }

    @Override
    public boolean onMouseScroll(float v, float v1, float v2, float v3) {
        return false;
    }

    @Override
    public boolean onCharTyped(char c, int i) {
        return false;
    }

    @Override
    public boolean onKeyPress(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean onKeyRelease(int i, int i1, int i2) {
        return false;
    }

    private void initSocials(){

        float offset = 0;

        float ypos = 29;

        FDButton discord = new FDButtonWithTexture(this.widgetOwner, 9 + offset,ypos, 32, 32, FDBosses.location("textures/gui/discord.png"))
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setOnHoverAction(((fdWidget, guiGraphics, v, v1, v2) -> {
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.translate(0,0,100);
                    BossRenderUtil.renderBossScreenTooltip(guiGraphics, Component.translatable("fdbosses.word.mod_discord"),v,v1, 200, BaseBossScreen.DEFAULT_TEXT_COLOR, 1f);
                    poseStack.popPose();
                }))
                .setOnClickAction(((fdWidget, v, v1, i) -> {
                    URI uri = null;
                    try {
                        uri = Util.parseAndValidateUntrustedUri("https://discord.com/invite/tW6zFRHDxC");
                        Util.getPlatform().openUri(uri);
                    } catch (URISyntaxException e) {

                    }
                    return true;
                }));

        FDButton patreon = new FDButtonWithTexture(this.widgetOwner, 9 + 35 + offset,ypos, 32, 32, FDBosses.location("textures/gui/patreon.png"))
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setOnHoverAction(((fdWidget, guiGraphics, v, v1, v2) -> {
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.translate(0,0,100);
                    BossRenderUtil.renderBossScreenTooltip(guiGraphics, Component.translatable("fdbosses.word.patreon"),v,v1, 200, BaseBossScreen.DEFAULT_TEXT_COLOR, 1f);
                    poseStack.popPose();
                }))
                .setOnClickAction(((fdWidget, v, v1, i) -> {
                    URI uri = null;
                    try {
                        uri = Util.parseAndValidateUntrustedUri("https://www.patreon.com/c/FDMods");
                        Util.getPlatform().openUri(uri);
                    } catch (URISyntaxException e) {

                    }
                    return true;
                }));

        FDButton boosty = new FDButtonWithTexture(this.widgetOwner, 9 + 35 * 2 + offset,ypos, 32, 32, FDBosses.location("textures/gui/boosty.png"))
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setOnHoverAction(((fdWidget, guiGraphics, v, v1, v2) -> {
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.translate(0,0,100);
                    BossRenderUtil.renderBossScreenTooltip(guiGraphics, Component.translatable("fdbosses.word.boosty"),v,v1, 200, BaseBossScreen.DEFAULT_TEXT_COLOR, 1f);
                    poseStack.popPose();
                }))
                .setOnClickAction(((fdWidget, v, v1, i) -> {
                    URI uri = null;
                    try {
                        uri = Util.parseAndValidateUntrustedUri("https://www.patreon.com/c/FDMods");
                        Util.getPlatform().openUri(uri);
                    } catch (URISyntaxException e) {

                    }
                    return true;
                }));

        this.addChild("discord", discord);
        this.addChild("patreon", patreon);
        this.addChild("boosty", boosty);

    }

}
