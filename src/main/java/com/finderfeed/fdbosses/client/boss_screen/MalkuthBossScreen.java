package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class MalkuthBossScreen extends BaseBossScreen{

    private static FDModel malkuthModel;

    private AnimationSystem animationSystem;

    public MalkuthBossScreen(int bossSpawnerId, BossScreenOptions options) {
        super(bossSpawnerId, options);
        if (malkuthModel == null) {
            malkuthModel = new FDModel(BossModels.MALKUTH_SCREEN.get());
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
        animationSystem.startAnimation("APPEAR", AnimationTicker.builder(BossAnims.MALKUTH_SCREEN_APPEAR)
                .build());
    }

    private int tickCount = 0;

    @Override
    public void tick() {
        super.tick();
        tickCount++;
        animationSystem.tick();

        if (tickCount == 20){
            animationSystem.startAnimation("APPEAR", AnimationTicker.builder(BossAnims.MALKUTH_SCREEN_IDLE)
                    .build());
        }

    }

    @Override
    public Component getBossName() {
        return BossEntities.MALKUTH.get().getDescription();
    }

    @Override
    public int getBaseStringColor() {
        return DEFAULT_TEXT_COLOR;
    }

    @Override
    protected void renderBoss(GuiGraphics graphics, float mx, float my, float pticks) {

        PoseStack matrices = graphics.pose();

        matrices.pushPose();

        matrices.translate(0,0,-100);

        Vector2f anchor = this.getAnchor(0.25f, 0.5f);

        this.animationSystem.applyAnimations(malkuthModel, pticks);

        FDRenderUtil.renderFDModelInScreen(matrices, malkuthModel, this.getBossMenuXStart()/2 - 20,anchor.y + 90, 0, FDMathUtil.FPI, 0, 50, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth.png")));

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
}
