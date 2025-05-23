package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossInfo;
import com.finderfeed.fdbosses.client.boss_screen.widget.*;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerStartFight;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.simple_screen.FDScrollableWidget;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.TextBlockWidget;
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
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class BaseBossScreen extends SimpleFDScreen {

    public static boolean wasSkillRead = false;

    private static int OPEN_TIME = 9;
    private boolean skillOpened = false;
    private int openTicker = 0;
    private SkillInfoWidget skillInfoWidget;
    private TextBlockWidget skillInfoText;
    private BossScreenOptions options;
    private BossDetailsWidget bossDetailsWidget;
    private BossAbilitesWidget bossAbilitesWidget;
    private FDButton skillStatsButton;
    private FDButton skillInfoButton;
    private float bossMenuXStart = 0;

    private DidntReadSkillWarningWidget didntReadSkillWarningWidget;

    private int moveThings = 0;

    private int bossSpawnerId;

    public BaseBossScreen(int bossSpawnerId, BossScreenOptions options) {
        super();
        this.options = options;
        this.bossSpawnerId = bossSpawnerId;
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

        this.initDidntReadSkillWarningWidget();

        FDButton startFightButton = new FDButton(this,5,5,110,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.start_fight").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        110,1f,true,0,1)
                .setOnClickAction(((fdWidget1, v2, v11, i1) -> {

                    if (wasSkillRead) {

                        Level level = FDClientHelpers.getClientLevel();
                        if (level.getEntity(bossSpawnerId) instanceof BossSpawnerEntity bossSpawner) {
                            PacketDistributor.sendToServer(new BossSpawnerStartFight(bossSpawnerId));
                        }

                    }else{
                        this.didntReadSkillWarningWidget.setActive(true);
                    }

                    return true;
                }));
        this.addRenderableWidget(startFightButton);

    }

    private void initDidntReadSkillWarningWidget(){
        Vector2f anchor = this.getAnchor(0.5f,0.5f);

        DidntReadSkillWarningWidget didntReadSkillWarningWidget = new DidntReadSkillWarningWidget(this, anchor.x - 150,anchor.y - 40, 300, 80);

        this.didntReadSkillWarningWidget = didntReadSkillWarningWidget;

        FDButton yes = new FDButton(this,20,didntReadSkillWarningWidget.getHeight() - 25,110,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.yes").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        110,1f,true,0,1)
                .setOnClickAction(((fdWidget1, v2, v11, i1) -> {
                    Level level = FDClientHelpers.getClientLevel();
                    if (level.getEntity(bossSpawnerId) instanceof BossSpawnerEntity bossSpawner) {
                        PacketDistributor.sendToServer(new BossSpawnerStartFight(bossSpawnerId));
                    }
                    return true;
                }));


        FDButton no = new FDButton(this,didntReadSkillWarningWidget.getWidth() - 120,didntReadSkillWarningWidget.getHeight() - 25,110,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.no").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        110,1f,true,0,1)
                .setOnClickAction(((fdWidget1, v2, v11, i1) -> {
                    this.didntReadSkillWarningWidget.setActive(false);
                    return true;
                }));

        this.didntReadSkillWarningWidget.setActive(false);

        didntReadSkillWarningWidget.addChild("yes",yes);
        didntReadSkillWarningWidget.addChild("no",no);


        this.addRenderableWidget(didntReadSkillWarningWidget);
    }

    private void initBossDetails(){

        Vector2f anchor = this.getAnchor(1f,0f);

        float bossInfoWidth = 237;
        float bossInfoHeight = 130;

        float bossInfoYs = 0;

        BossDetailsWidget widget = new BossDetailsWidget(this,  anchor.x, bossInfoYs, bossInfoWidth, bossInfoHeight);
        widget.setBossInfo(this.options.getEntityType().getDescription(),this.getBaseStringColor());
        this.bossMenuXStart = anchor.x - bossInfoWidth;

        TextBlockWidget bossDescription = new TextBlockWidget(this, 18,60,195, 52);
        bossDescription.setText(options.getBossDescription(),1f,this.getBaseStringColor(),true);

        widget.addChild("bossDescription",bossDescription);

        this.addRenderableWidget(widget);
        bossDetailsWidget = widget;

    }

    private void initSkillInfoWidget(){

        Vector2f anchor = this.getAnchor(1,1);


        SkillInfoWidget widget = new SkillInfoWidget(this,-200,2,200,anchor.y - 4,Component.literal("TTT"),this.getBaseStringColor());


        FDButton fdButtonStats = new FDButton(this,108,79,73,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/small_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/small_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.stats").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        61,1f,true,0,1);

        FDButton fdButtonInfo = new FDButton(this,30,79,73,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/small_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/small_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.info").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        61,1f,true,0,1);


        skillInfoButton = fdButtonInfo;
        skillStatsButton = fdButtonStats;



        TextBlockWidget textBlockWidget = new TextBlockWidget(this,30,107,151,widget.getHeight() - 130);
        widget.addChild("text",textBlockWidget);

        widget.addChild("statsButton",fdButtonStats);
        widget.addChild("infoButton",fdButtonInfo);

        this.skillInfoWidget = widget;
        this.skillInfoText = textBlockWidget;
        this.addRenderableWidget(widget);
    }


    private void initAbilitiesWidget(){

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        BossAbilitesWidget bossAbilitesWidget = new BossAbilitesWidget(this,anchorEnd.x - 237, anchorEnd.y,this.getBaseStringColor());

        List<BossInfo> skills = options.getSkills();

        this.initAbilitiesWidgetModeChangeButtons(bossAbilitesWidget);

        this.initAbilitiesWidgetInfoButtons(bossAbilitesWidget.bossAbilitiesButtonContainer, skills);

        this.addRenderableWidget(bossAbilitesWidget);

        this.bossAbilitesWidget = bossAbilitesWidget;
    }

    private void initAbilitiesWidgetModeChangeButtons(BossAbilitesWidget bossAbilitesWidget){

        FDButton fdButtonSkills = new FDButton(this,34,1,73,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/small_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/small_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.abilities").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        73,1f,true,0,1)
                .setOnClickAction((fdWidget, v, v1, i) -> {
                    bossAbilitesWidget.bossAbilitiesButtonContainer.setCurrentScroll(0);
                    bossAbilitesWidget.bossAbilitiesButtonContainer.removeAllChildren();
                    this.initAbilitiesWidgetInfoButtons(bossAbilitesWidget.bossAbilitiesButtonContainer, this.options.getSkills());
                    return true;
                });

        FDButton fdButtonDrops = new FDButton(this,197-73,1,73,24)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/small_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/small_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.drops").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                        73,1f,true,0,1)
                .setOnClickAction(((fdWidget, v, v1, i) -> {
                    bossAbilitesWidget.bossAbilitiesButtonContainer.setCurrentScroll(0);
                    bossAbilitesWidget.bossAbilitiesButtonContainer.removeAllChildren();
                    this.initAbilitiesWidgetInfoButtons(bossAbilitesWidget.bossAbilitiesButtonContainer, this.options.getDrops());
                    return true;
                }));

        bossAbilitesWidget.addChild("openSkills",fdButtonSkills);
        bossAbilitesWidget.addChild("openDrops",fdButtonDrops);

    }



    private void initAbilitiesWidgetInfoButtons(FDScrollableWidget bossAbilitesWidget, List<BossInfo> bossInfos){
        float offsx = 5;
        float offsy = 4;
        for (int  i = 0; i < bossInfos.size();i++) {

            BossInfo bossSkill = bossInfos.get(i);

            float x = offsx + (i % 5) * 39;
            float y = offsy + (i / 5) * 35;

            FDButton skill = new FDSkillButton(this,x,y,32,32, bossSkill)
                    .setTexture(new FDButtonTextures(
                            new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                            new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                    ))
                    .setOnHoverAction(((fdWidget, guiGraphics, v, v1, v2) -> {
                        if (!this.skillOpened) {
                            PoseStack matrices = guiGraphics.pose();
                            matrices.pushPose();
                            matrices.translate(0, 0, 100);
                            BossRenderUtil.renderBossScreenTooltip(guiGraphics, Component.translatable("fdbosses.skills.skill_button"), v, v1, 200, this.getBaseStringColor(), 1f,
                                    this.bossAbilitesWidget.bossAbilitiesButtonContainer.getX(),
                                    this.bossAbilitesWidget.bossAbilitiesButtonContainer.getY(),
                                    this.bossAbilitesWidget.bossAbilitiesButtonContainer.getX() + this.bossAbilitesWidget.bossAbilitiesButtonContainer.getWidth(),
                                    this.bossAbilitesWidget.bossAbilitiesButtonContainer.getY() + this.bossAbilitesWidget.bossAbilitiesButtonContainer.getHeight()
                            );
                            matrices.popPose();
                        }
                    }))
                    .setSound(BossSounds.BUTTON_CLICK.get())
                    .setOnClickAction(((fdWidget, mx, my, button) -> {
                        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                        BaseBossScreen.wasSkillRead = true;

                        this.openSkillInfo(bossSkill,true);

                        return true;
                    }));


            bossAbilitesWidget.addChild("skill" + i, skill);

        }

    }

    public void openSkillInfo(BossInfo info, boolean state){
        if (!skillOpened && state){
            if (info.getInfoStats() == null) {
                this.skillInfoText.setText(info.getInfoDescription(), 1f, this.getBaseStringColor(), true);
                this.skillInfoButton.setOnClickAction(null);
                this.skillStatsButton.setOnClickAction(null);
                skillInfoButton.setActive(false);
                skillStatsButton.setActive(false);
            }else{
                this.skillInfoText.setText(info.getInfoDescription(), 1f, this.getBaseStringColor(), true);
                this.skillInfoButton.setOnClickAction(((fdWidget, v, v1, i) -> {
                    this.skillInfoText.setText(info.getInfoDescription(), 1f, this.getBaseStringColor(), true);
                    return true;
                }));
                this.skillStatsButton.setOnClickAction(((fdWidget, v, v1, i) -> {
                    this.skillInfoText.setText(info.getInfoStats(), 1f, this.getBaseStringColor(), true);
                    return true;
                }));
                skillInfoButton.setActive(true);
                skillStatsButton.setActive(true);

            }
            this.skillInfoWidget.setSkillName(info.getInfoName(),this.getBaseStringColor());

            this.skillInfoWidget.setSkillImage(info.getInfoIcon());

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
            this.bossAbilitesWidget.moveWidgetTo(9,this.bossAbilitesWidget.getX(),this.bossAbilitesWidget.getY() - this.bossAbilitesWidget.getHeight(),FDEasings::easeOut);
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
            if (renderable != this.skillInfoWidget && renderable != didntReadSkillWarningWidget) {
                renderable.render(graphics, mx, my, pticks);
            }
        }


        this.renderSkillInfo(graphics,mx,my,pticks);

        this.renderDidntReadSkillInfoWidget(graphics,mx,my,pticks);
    }

    private void renderDidntReadSkillInfoWidget(GuiGraphics graphics, int mx, int my, float pticks){
        if (!didntReadSkillWarningWidget.isActive()) return;

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        PoseStack poseStack = graphics.pose();



        poseStack.pushPose();
        poseStack.translate(0,0,200);
        FDRenderUtil.fill(poseStack,0,0,anchorEnd.x,anchorEnd.y,0f,0f,0f,0.925f);
        didntReadSkillWarningWidget.render(graphics,mx,my,pticks);
        poseStack.popPose();
    }

    private void renderSkillInfo(GuiGraphics graphics, int mx, int my, float pticks){
        Vector2f anchorEnd = this.getAnchor(1f,1f);

        PoseStack matrices = graphics.pose();
        matrices.pushPose();
        matrices.translate(0,0,200);
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



    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (didntReadSkillWarningWidget.isActive()){
            if (FDRenderUtil.isMouseInBounds((float) mx,(float) my,didntReadSkillWarningWidget.getX(),didntReadSkillWarningWidget.getY(),didntReadSkillWarningWidget.getWidth(),didntReadSkillWarningWidget.getHeight())){
                return didntReadSkillWarningWidget.mouseClicked(mx,my, button);
            }
            return false;
        } else if (this.skillOpened) {
            float xBorder = this.skillInfoWidget.getX() + this.skillInfoWidget.getWidth();
            if (mx > xBorder) {
                this.openSkillInfo(null,false);
                return false;
            } else {
                if (FDRenderUtil.isMouseInBounds((float)mx,(float)my,this.skillInfoWidget.getX(),this.skillInfoWidget.getY(),this.skillInfoWidget.getWidth(),this.skillInfoWidget.getHeight())) {
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

    public float getBossMenuXStart() {
        return bossMenuXStart;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
