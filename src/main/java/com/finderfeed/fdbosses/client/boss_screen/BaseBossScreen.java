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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseBossScreen extends SimpleFDScreen {

    public static final int DEFAULT_TEXT_COLOR = 0x55ccff;

    public static boolean wasSkillRead = false;

    private static int OPEN_TIME = 9;
    private boolean skillOpened = false;
    private int openTicker = 0;
    protected SkillInfoWidget skillInfoWidget;
    protected TextBlockWidget skillInfoText;
    protected BossScreenOptions options;
    protected BossDetailsWidget bossDetailsWidget;
    protected BossAbilitesWidget bossAbilitesWidget;
    protected FDButton skillStatsButton;
    protected FDButton skillInfoButton;
    protected float bossMenuXStart = 0;

    protected QliphothAwakeningSocialsWidget socialsWidget;

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

        FDButton startFightButton = new FDButton(this,6,6,110,24)
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

        this.initTLDRButton();

        this.initSocialsButton();

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

        this.initAbilitiesWidgetInfoButtons(bossAbilitesWidget.bossAbilitiesButtonContainer, skills, false);

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
                    this.resetAbilities(this.options.getSkills(), false);
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
                    this.resetAbilities(this.options.getDrops(), false);
                    return true;
                }));

        bossAbilitesWidget.addChild("openSkills",fdButtonSkills);
        bossAbilitesWidget.addChild("openDrops",fdButtonDrops);

    }

    protected void resetAbilities(List<BossInfo> bossInfos, boolean malkuthShuffle){
        bossAbilitesWidget.bossAbilitiesButtonContainer.setCurrentScroll(0);
        bossAbilitesWidget.bossAbilitiesButtonContainer.removeAllChildren();
        this.initAbilitiesWidgetInfoButtons(bossAbilitesWidget.bossAbilitiesButtonContainer,bossInfos, malkuthShuffle);
    }

    private void initSocialsButton(){
        Vector2f anchor = this.getAnchor(0,1);
        Vector2f anchor2 = this.getAnchor(0.5f,0.5f);
        float x = anchor2.x;
        float y = anchor2.y;
        socialsWidget = new QliphothAwakeningSocialsWidget(this, x - 60, y - 35, 120, 70);


        FDButton button = new FDButtonWithTexture(this, anchor.x + 4,anchor.y - 36 * 2, 32, 32, FDBosses.location("textures/gui/link.png"))
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setOnHoverAction(((fdWidget, guiGraphics, v, v1, v2) -> {
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.translate(0,0,100);
                    BossRenderUtil.renderBossScreenTooltip(guiGraphics, Component.translatable("fdbosses.word.socials"),v,v1, 200, this.getBaseStringColor(), 1f);
                    poseStack.popPose();
                }))
                .setOnClickAction(((fdWidget, v, v1, i) -> {
                    this.socialsWidget.setActive(true);
                    return true;
                }));

        this.addRenderableWidget(button);
        this.socialsWidget.setActive(false);
        this.addWidget(socialsWidget);
    }

    private void initTLDRButton(){


        Vector2f anchor = this.getAnchor(0,1);


        BossInfo bossInfo = new BossInfo(ResourceLocation.parse("minecraft:textures/item/book.png"),Component.translatable("fdbosses.word.short_description"),null,this.options.getTLDRComponent());
        FDButton skill = new FDSkillButton(this,anchor.x + 4,anchor.y - 36,32,32, bossInfo)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_unselected.png"),0,0),
                        new WidgetTexture(FDBosses.location("textures/gui/ability_button_selected.png"),1,1)
                ))
                .setOnHoverAction(((fdWidget, guiGraphics, v, v1, v2) -> {
                    if (!this.skillOpened) {
                        PoseStack matrices = guiGraphics.pose();
                        matrices.pushPose();
                        matrices.translate(0, 0, 100);
                        BossRenderUtil.renderBossScreenTooltip(guiGraphics, Component.translatable("fdbosses.word.tldr"), v, v1, 200, this.getBaseStringColor(), 1f);
                        matrices.popPose();
                    }
                }))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setOnClickAction(((fdWidget, mx, my, button) -> {
                    if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                    BaseBossScreen.wasSkillRead = true;

                    this.openSkillInfo(bossInfo,true);

                    return true;
                }));

        this.addRenderableWidget(skill);
    }



    private void initAbilitiesWidgetInfoButtons(FDScrollableWidget bossAbilitesWidget, List<BossInfo> bossInfos, boolean malkuthShuffle){
        float offsx = 5;
        float offsy = 4;

        List<BossInfo> bossInfos2 = new ArrayList<>(bossInfos);

        Random random = new Random();

        if (malkuthShuffle && bossInfos2.size() > 1){
            for (int i = 0; i < bossInfos2.size() * 2; i++){

                int id1 = random.nextInt(bossInfos2.size());
                BossInfo info1 = bossInfos2.get(id1);

                int id2;
                while ((id2 = random.nextInt(bossInfos2.size())) == id1);

                BossInfo info2 = bossInfos2.get(id2);

                bossInfos2.set(id1, info2);
                bossInfos2.set(id2, info1);

            }
        }

        for (int  i = 0; i < bossInfos.size();i++) {

            BossInfo bossSkill = bossInfos.get(i);
            BossInfo skillToOpen = bossInfos2.get(i);

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

                        this.openSkillInfo(skillToOpen,true);

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
        this.renderSocialsWidget(graphics,mx,my,pticks);
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

    private void renderSocialsWidget(GuiGraphics graphics, int mx, int my, float pticks){
        if (!socialsWidget.isActive()) return;

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        PoseStack poseStack = graphics.pose();



        poseStack.pushPose();
        poseStack.translate(0,0,200);
        FDRenderUtil.fill(poseStack,0,0,anchorEnd.x,anchorEnd.y,0f,0f,0f,0.9f);
        socialsWidget.render(graphics,mx,my,pticks);
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
        } else if (socialsWidget.isActive()){
            if (FDRenderUtil.isMouseInBounds((float) mx,(float) my,socialsWidget.getX(),socialsWidget.getY(),socialsWidget.getWidth(),socialsWidget.getHeight())){
                return socialsWidget.mouseClicked(mx,my, button);
            }else{
                this.socialsWidget.setActive(false);
            }
            return false;
        }else if (this.skillOpened) {
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
