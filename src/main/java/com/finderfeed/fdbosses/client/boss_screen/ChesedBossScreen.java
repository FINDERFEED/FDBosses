package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreens;
import com.finderfeed.fdbosses.client.boss_screen.util.TimedText;
import com.finderfeed.fdbosses.client.boss_screen.util.KilledASefirotException;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDClientPacketExecutables;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.FDButtonTextures;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.WidgetTexture;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;


@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChesedBossScreen extends BaseBossScreen {

    private static FDModel chesed;

    private AnimationSystem animationSystem;

    private int hurtTime = 0;

    private static final Component[] hurtTexts = {
            Component.translatable("fdbosses.word.ouch"),
            Component.translatable("fdbosses.word.that_hurts"),
            Component.translatable("fdbosses.word.stop_it"),
            Component.translatable("fdbosses.word.you_are_getting_on_my_nerves"),
            Component.translatable("fdbosses.word.i_said_stop"),
            Component.translatable("fdbosses.word.consequences"),
            Component.translatable("fdbosses.word.does_that_not_scare_you"),
            Component.translatable("fdbosses.word.ok_listen"),
            Component.translatable("fdbosses.word.no_wait"),
            Component.translatable("fdbosses.word.killed_chesed_in_screen"),
    };

    private int maxHealth = 10;

    private int remainingHealth = maxHealth;

    private boolean renderBoss = false;

    private float bossX;
    private float bossY;

    private TimedText hurtText;

    public ChesedBossScreen(BossScreenOptions options) {
        super(options);
    }

    @Override
    protected void init() {
        super.init();
        if (chesed == null){
            chesed = new FDModel(BossModels.CHESED.get());
        }

        this.hurtText = new TimedText();

        this.hurtTime = 0;
        animationSystem = new AnimationSystem() {
            @Override
            public void onAnimationStart(String s, AnimationTicker animationTicker) {

            }

            @Override
            public void onAnimationStop(String s) {

            }

            @Override
            public void onFreeze(boolean b) {

            }

            @Override
            public void onVariableAdded(String s, float v) {

            }
        };
        animationSystem.startAnimation("APPEAR", AnimationTicker.builder(BossAnims.CHESED_SCREEN_APPEAR)
                        .setLoopMode(Animation.LoopMode.ONCE)
                        .setToNullTransitionTime(0)
                .build());

        var anchor = this.getAnchor(0, 0.5f);
        float offsetX = this.getBossMenuXStart() / 2;
        float offsetY = 100;
        this.bossX = anchor.x + offsetX;
        this.bossY = anchor.y + offsetY;

        FDButton button = new FDButton(this, bossX - 23, bossY - 90, 50,50)
                .setOnClickAction(((fdWidget, v, v1, i) -> {
                    if (hurtTime == 0 && remainingHealth > 0) {
                        FDClientPacketExecutables.playsoundInEars(SoundEvents.GENERIC_HURT,1f,1f);
                        animationSystem.startAnimation("HURT", AnimationTicker.builder(BossAnims.CHESED_IDLE)
                                .setLoopMode(Animation.LoopMode.ONCE)
                                        .startTime(30)
                                .setSpeed(20f)
                                .build());
                        this.hurtTime = 10;
                        int textId = maxHealth - remainingHealth;
                        int time = 60;
                        if (textId == hurtTexts.length - 1){
                            time = Integer.MAX_VALUE;
                        }
                        this.hurtText.setText(hurtTexts[textId],time);
                        remainingHealth = Mth.clamp(remainingHealth - 1, 0, Integer.MAX_VALUE);
                        if (remainingHealth == 0){
                            this.removeWidget(fdWidget);
                            this.animationSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_DEATH).build());
                            FDButton crashButton = new FDButton(this,bossX - 55,bossY - 40,110,24)
                                    .setTexture(new FDButtonTextures(
                                            new WidgetTexture(FDBosses.location("textures/gui/medium_button.png")),
                                            new WidgetTexture(FDBosses.location("textures/gui/medium_button_selected.png"),1,1)
                                    ))
                                    .setText(Component.translatable("fdbosses.word.crash_game").withStyle(Style.EMPTY.withColor(this.getBaseStringColor())),
                                            110,1f,true,0,1)
                                    .setOnClickAction(((fdWidget1, v2, v11, i1) -> {
                                        if (true) {
                                            throw new KilledASefirotException("That's what you get for messing with Chesed! I warned you!");
                                        }
                                        return true;
                                    }));
                            this.addRenderableWidget(crashButton);
                        }
                    }
                    return true;
                }));

        this.addWidget(button);
    }

    @Override
    public Component getBossName() {
        return BossEntities.CHESED.get().getDescription();
    }

    @Override
    public int getBaseStringColor() {
        return 0x55ccff;
    }


    @Override
    public void tick() {
        super.tick();
        animationSystem.tick();
        AnimationTicker ticker = animationSystem.getTicker("IDLE");
        if (ticker == null) {
            animationSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_IDLE).build());
        }
        this.hurtTime = Mth.clamp(hurtTime - 1, 0, Integer.MAX_VALUE);
        hurtText.tick();
    }

    @Override
    protected void renderBoss(GuiGraphics graphics,float mx,float my,float pticks){
        PoseStack matrices = graphics.pose();
        this.animationSystem.applyAnimations(chesed, FDRenderUtil.tryGetPartialTickIgnorePause());
        matrices.pushPose();
        matrices.translate(0, 0, -100);
        int overlay = hurtTime > 0 ? OverlayTexture.RED_OVERLAY_V : OverlayTexture.NO_OVERLAY;
        FDRenderUtil.renderFDModelInScreen(matrices, chesed, this.bossX, this.bossY,
                0, FDMathUtil.FPI + FDMathUtil.FPI / 16, 0, 50, LightTexture.FULL_BRIGHT, overlay, RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")));

        FDRenderUtil.renderFDModelInScreen(matrices, chesed, this.bossX, this.bossY,
                0, FDMathUtil.FPI + FDMathUtil.FPI / 16, 0, 50, LightTexture.FULL_BRIGHT, overlay, RenderType.eyes(FDBosses.location("textures/entities/chesed_crystals.png")));
        matrices.popPose();

        Component text = hurtText.text;
        if (text != null){
            int i = 0;
            for (FormattedCharSequence sequence : font.split(text,200)) {
                FDRenderUtil.renderScaledText(graphics, sequence, bossX - font.width(sequence) / 2f, bossY - 200 + i * font.lineHeight, 1f, true, this.getBaseStringColor());
                i++;
            }
        }
    }



    @Override
    public float getScreenWidth() {
        return 0;
    }

    @Override
    public float getScreenHeight() {
        return 0;
    }

    @SubscribeEvent
    public static void key(InputEvent.Key event){
        if (Minecraft.getInstance().level == null) return;
        if (event.getKey() == GLFW.GLFW_KEY_M){
            Minecraft.getInstance().setScreen(BossScreens.CHESED.get());
        }
    }
}
