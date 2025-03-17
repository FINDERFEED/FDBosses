package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreens;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;


@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChesedBossScreen extends BaseBossScreen {

    private static FDModel chesed;

    private AnimationSystem animationSystem;

    public ChesedBossScreen(BossScreenOptions options) {
        super(options);
    }

    @Override
    protected void init() {
        super.init();

        if (chesed == null){
            chesed = new FDModel(BossModels.CHESED.get());
        }

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
        animationSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_IDLE).build());
    }

    @Override
    protected void renderBoss(GuiGraphics graphics,float mx,float my,float pticks){
        PoseStack matrices = graphics.pose();
        this.animationSystem.applyAnimations(chesed,pticks);
        var anchor = this.getAnchor(0,0.5f);
        float offsetX = this.getBossMenuXStart() / 2;
        float offsetY = 100;
        matrices.pushPose();
        matrices.translate(0,0,-100);
        FDRenderUtil.renderFDModelInScreen(matrices,chesed,anchor.x + offsetX,anchor.y + offsetY,
                0, FDMathUtil.FPI + FDMathUtil.FPI / 8,0,50,RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")));
        matrices.popPose();
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
