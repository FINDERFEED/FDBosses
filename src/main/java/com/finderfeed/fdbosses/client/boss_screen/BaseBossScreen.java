package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossSkill;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockWidget;
import com.finderfeed.fdbosses.client.boss_screen.widget.BossAbilitesWidget;
import com.finderfeed.fdbosses.client.boss_screen.widget.BossDetailsWidget;
import com.finderfeed.fdbosses.client.boss_screen.widget.FDSkillButton;
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
    private TextBlockWidget skillInfoText;
    private BossScreenOptions options;
    private BossDetailsWidget bossDetailsWidget;
    private BossAbilitesWidget bossAbilitesWidget;

    private int moveThings = 0;

    public BaseBossScreen(BossScreenOptions options) {
        super();
        this.options = options;
    }

    @Override
    protected void init() {
        super.init();

        moveThings = 0;
        skillOpened = false;
        OPEN_TIME = 11;
        openTicker = 0;

        Window window = Minecraft.getInstance().getWindow();

        this.initBossDetails();

        this.initSkillInfoWidget();

        this.initAbilitiesWidget();

    }

    private void initBossDetails(){

        Vector2f anchor = this.getAnchor(1f,0f);

        float bossInfoWidth = 237;
        float bossInfoHeight = 125;

        float bossInfoYs = 5;

        BossDetailsWidget widget = new BossDetailsWidget(this,  anchor.x, bossInfoYs, bossInfoWidth, bossInfoHeight);
        widget.setBossName(this.options.getEntityType().getDescription(),this.getBaseStringColor());

        TextBlockWidget bossDescription = new TextBlockWidget(this, 18,60,195, 40);
        bossDescription.setText(options.getBossDescription(),1f,this.getBaseStringColor(),true);

        widget.addChild("bossDescription",bossDescription);

        this.addRenderableWidget(widget);
        bossDetailsWidget = widget;

    }

    private void initSkillInfoWidget(){

        Vector2f anchor = this.getAnchor(1,1);


        SkillInfoWidget widget = new SkillInfoWidget(this,-200,2,200,anchor.y - 4,Component.literal("TTT"),this.getBaseStringColor());

        TextBlockWidget textBlockWidget = new TextBlockWidget(this,30,75,151,widget.getHeight() - 99);
        widget.addChild("text",textBlockWidget);

        this.skillInfoWidget = widget;
        this.skillInfoText = textBlockWidget;
        this.addRenderableWidget(widget);

    }

    private void initAbilitiesWidget(){

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        BossAbilitesWidget bossAbilitesWidget = new BossAbilitesWidget(this,anchorEnd.x - 237, anchorEnd.y,this.getBaseStringColor());

        float offsx = 19;
        float offsy = 30;

        var skills = options.getSkills();

        for (int  i = 0; i < skills.size();i++) {

            BossSkill bossSkill = skills.get(i);

            float x = offsx + (i % 5) * 39;
            float y = offsy + (i / 5) * 35;

            FDButton skill = new FDSkillButton(this,x,y,32,32, bossSkill)
                    .setTexture(new FDButtonTextures(
                            new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                            new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                    ))
                    .setOnClickAction(((fdWidget, mx, my, button) -> {
                        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                        this.openSkillInfo(bossSkill,true);

                        return true;
                    }));


            bossAbilitesWidget.addChild("skill" + i, skill);

        }

        this.addRenderableWidget(bossAbilitesWidget);

        this.bossAbilitesWidget = bossAbilitesWidget;
    }

    public void openSkillInfo(BossSkill skill, boolean state){
        if (!skillOpened && state){

            this.skillInfoText.setText(skill.getSkillDescription(),1f,this.getBaseStringColor(),true);

            this.skillInfoWidget.setSkillName(skill.getSkillName(),this.getBaseStringColor());

            this.skillInfoWidget.setSkillImage(skill.getSkillIcon());

            this.skillInfoWidget.moveWidgetTo(OPEN_TIME,0,2, FDEasings::easeOutBounce);
            skillOpened = true;
        }else if (skillOpened && !state){
            this.skillInfoWidget.moveWidgetTo(OPEN_TIME / 2,-this.skillInfoWidget.getWidth(),2, FDEasings::easeIn);
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

        if (moveThings++ == 1){
            this.bossDetailsWidget.moveWidgetTo(9,this.bossDetailsWidget.getX() - this.bossDetailsWidget.getWidth(), this.bossDetailsWidget.getY(),FDEasings::easeOut);
            this.bossAbilitesWidget.moveWidgetTo(9,this.bossAbilitesWidget.getX(),this.bossAbilitesWidget.getY() - this.bossAbilitesWidget.getHeight() - 5,FDEasings::easeOut);
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
        float a = this.openTicker / (float) OPEN_TIME;
        if (skillOpened) {
            a = FDEasings.easeOut(FDEasings.easeOut(a));
        }else{
            a = FDEasings.easeOut(a);
        }
        FDRenderUtil.fill(matrices,0,0,anchorEnd.x,anchorEnd.y,0f,0f,0f,a * 0.925f);
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




        //----------------------------boss name-----------------------------------------------



    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (this.skillOpened) {
            float xBorder = this.skillInfoWidget.getX() + this.skillInfoWidget.getWidth();
            if (mx > xBorder) {
                this.openSkillInfo(null,false);
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
