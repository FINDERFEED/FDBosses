package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.widget.BossAbilitesWidget;
import com.finderfeed.fdbosses.client.boss_screen.widget.SkillInfoWidget;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDImage;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.FDButtonTextures;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.WidgetTexture;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public abstract class BaseBossScreen extends SimpleFDScreen {

    private static int OPEN_TIME = 9;
    private boolean skillOpened = false;
    private int openTicker = 0;
    private SkillInfoWidget skillInfoWidget;

    public BaseBossScreen() {
        super();
    }

    @Override
    protected void init() {
        super.init();

        skillOpened = false;
        openTicker = 0;

        Window window = Minecraft.getInstance().getWindow();

        float height = window.getGuiScaledHeight();
        //12 offset 187 kind of width


        this.initSkillInfoWidget();

        this.initAbilitiesWidget();

    }

    private void initSkillInfoWidget(){
        SkillInfoWidget widget = new SkillInfoWidget(this,-200,0,200,height - 2);
        FDImage image = new FDImage(this,12 + 187 / 2f - 16,20,32,32, new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png")));
        widget.addChild("abilityImage", image);
        this.skillInfoWidget = widget;
        this.addRenderableWidget(widget);
    }

    private void initAbilitiesWidget(){

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        BossAbilitesWidget bossAbilitesWidget = new BossAbilitesWidget(this,anchorEnd.x - 237, anchorEnd.y - 120,this.getBaseStringColor());

        float offsx = 19;
        float offsy = 30;

        for (int  i = 0; i < 4;i++) {

            float x = offsx + (i % 5) * 39;
            float y = offsy + (i / 5) * 35;

            FDButton skill = new FDButton(this,x,y,32,32)
                    .setTexture(new FDButtonTextures(
                            new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                            new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                    ))
                    .setOnClickAction(((fdWidget, mx, my, button) -> {
                        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                        this.openSkillInfo(true);

                        return true;
                    }));

            bossAbilitesWidget.addChild("skill" + i, skill);

        }

        this.addRenderableWidget(bossAbilitesWidget);


    }

    public void openSkillInfo(boolean state){
        if (!skillOpened && state){
            this.skillInfoWidget.moveWidgetTo(OPEN_TIME,0,0, FDEasings::easeOutBounce);
            skillOpened = true;
        }else if (skillOpened && !state){
            this.skillInfoWidget.moveWidgetTo(OPEN_TIME,-this.skillInfoWidget.getWidth(),0, FDEasings::easeIn);
            skillOpened = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (skillOpened){
            this.openTicker = Mth.clamp(this.openTicker + 1,0,OPEN_TIME);
        }else{
            this.openTicker = Mth.clamp(this.openTicker - 1,0,OPEN_TIME);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {


        this.renderBlurredBackground(pticks);


        PoseStack matrices = graphics.pose();

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        FDRenderUtil.fill(matrices,0,0,anchorEnd.x,anchorEnd.y,0f,0f,0f,0.6f);

        this.renderBoss(graphics,mx,my,pticks);

        this.renderBack(graphics,mx,my,pticks);

        for (Renderable renderable : this.renderables) {
            if (renderable != this.skillInfoWidget) {
                renderable.render(graphics, mx, my, pticks);
            }
        }

        this.renderSkillInfo(graphics,mx,my,pticks);

    }

    private void renderSkillInfo(GuiGraphics graphics, int mx, int my, float pticks){
        Vector2f anchorEnd = this.getAnchor(1f,1f);

        PoseStack matrices = graphics.pose();
        matrices.pushPose();
        matrices.translate(0,0,100);

        float a = FDEasings.easeOut(this.openTicker / (float) OPEN_TIME);
        FDRenderUtil.fill(matrices,0,0,anchorEnd.x,anchorEnd.y,0f,0f,0f,a * 0.9f);
        this.skillInfoWidget.render(graphics,mx,my,pticks);
        matrices.popPose();
    }



    protected void renderBack(GuiGraphics graphics, float mx, float my, float pticks){

        PoseStack matrices = graphics.pose();

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        //----------------------------backgrounds------------------------------------------
        Vector2f anchor = this.getAnchor(1f,0f);

        float bossInfoWidth = 237;
        float bossInfoHeight = 125;

        float bossInfoXs = anchor.x - bossInfoWidth;
        float bossInfoYs = 10;






        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/boss_detail.png"));
        FDRenderUtil.blitWithBlend(matrices, bossInfoXs,bossInfoYs,bossInfoWidth,bossInfoHeight,0,0,1f,1f,1f,1f,0,1f);


        //----------------------------boss name-----------------------------------------------
        Component bossName = this.getBossName();

        float bossNameScale = 2f;
        float bossNameWidth = font.width(bossName) * bossNameScale;
        float bossNameHeight = font.lineHeight * bossNameScale;
        float bossNameOffsetX = bossInfoWidth/2 - bossNameWidth / 2;
        float bossNameOffsetY = 35;
        float gradientOffset = 2;
        float gradientWidthAddition = 20;
        float r = ((this.getBaseStringColor() & 0xff0000) >> 16) / 255f;
        float g = ((this.getBaseStringColor() & 0x00ff00) >> 8) / 255f;
        float b = ((this.getBaseStringColor() & 0x0000ff)) / 255f;


        FDRenderUtil.fill(matrices,bossInfoXs + bossNameOffsetX - gradientOffset + bossNameWidth/2,bossInfoYs + bossNameOffsetY - gradientOffset, bossNameWidth + gradientOffset * 2 + gradientWidthAddition - bossNameWidth/2,bossNameHeight + gradientOffset * 2,
                r,g,b,0.3f,
                r,g,b,0f,
                r,g,b,0f,
                r,g,b,0.3f
        );

        FDRenderUtil.fill(matrices,bossInfoXs + bossNameOffsetX - gradientOffset * 2 - gradientWidthAddition,bossInfoYs + bossNameOffsetY - gradientOffset, bossNameWidth / 2 + gradientWidthAddition + gradientOffset,bossNameHeight + gradientOffset * 2,
                r,g,b,0f,
                r,g,b,0.3f,
                r,g,b,0.3f,
                r,g,b,0f
        );

        FDRenderUtil.renderScaledText(graphics,bossName,bossInfoXs + bossNameOffsetX,bossInfoYs + bossNameOffsetY,bossNameScale,true,this.getBaseStringColor());



    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (this.skillOpened) {
            float xBorder = this.skillInfoWidget.getX() + this.skillInfoWidget.getWidth();
            if (mx > xBorder) {
                this.openSkillInfo(false);
                return false;
            } else {
                if (FDRenderUtil.isMouseInBounds((float)mx,(float)my,this.skillInfoWidget.getX(),this.skillInfoWidget.getY(),this.skillInfoWidget.getWidth(),this.skillInfoWidget.getY())) {
                    return this.skillInfoWidget.mouseClicked(mx, my, button);
                }
                return false;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    public abstract Component getBossName();

    public abstract int getBaseStringColor();

    protected abstract void renderBoss(GuiGraphics graphics, float mx, float my, float pticks);

}
