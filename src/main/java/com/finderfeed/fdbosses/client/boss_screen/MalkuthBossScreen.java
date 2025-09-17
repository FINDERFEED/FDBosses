package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.client.boss_screen.util.KilledASefirotException;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDClientPacketExecutables;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.FDButtonTextures;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.WidgetTexture;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticle;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class MalkuthBossScreen extends BaseBossScreen{

    private static FDModel malkuthModel;

    private AnimationSystem animationSystem;

    private int tickCount = 0;

    private float bossX;
    private float bossY;


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
                        .setSpeed(0.75f)
                .build());
    }

    @Override
    protected void init() {
        super.init();
        Vector2f anchor = this.getAnchor(0, 0.5f);

        this.bossX = this.getBossMenuXStart()/2 - 20;
        this.bossY = anchor.y + 90;

        float height = 150;

        FDButton button = new FDButton(this, bossX - 30, bossY - height, 60,height)
                .setOnClickAction(((fdWidget, mx, my, b) -> {
                    if (b == GLFW.GLFW_MOUSE_BUTTON_LEFT){
                        this.resetAbilities(this.options.getSkills(), true);
                        this.shuffleParticles();
                        return true;
                    }
                    return false;
                }));

        this.addWidget(button);
    }

    private void shuffleParticles(){


        RandomSource random = RandomSource.create();

        int count = 0;

        for (var children : this.bossAbilitesWidget.bossAbilitiesButtonContainer.getChildren().entrySet()) {

            count++;

            var button = children.getValue();

            float wx = button.getWidth();
            float wy = button.getHeight();

            float px = button.getX() + wx / 2;
            float py = button.getY() + wy / 2;

            for (int i = 0; i < 20; i++) {
                Vector3f color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.getRandom(random), random);
                Vector3f v = new Vector3f(random.nextFloat() * 4 + 1, 0, 0).rotateZ(FDMathUtil.FPI * 2 * random.nextFloat());

                FDTexturedSParticle.create(FDRenderUtil.ParticleRenderTypesS.TEXTURES_BLUR_ADDITIVE, BallParticle.LOCATION)
                        .setPos(px, py, true)
                        .setAcceleration(-v.x * 0.06, -v.y * 0.06)
                        .setDefaultScaleOut()
                        .setMaxQuadSize(20f + random.nextFloat() * 5)
                        .setSpeed(v.x, v.y)
                        .setColor(color.x,color.y,color.z,1f)
                        .setLifetime(15 + random.nextInt(5))
                        .sendToScreen();
            }

            if (count >= 10){
                break;
            }

        }


    }

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

        pticks = FDRenderUtil.tryGetPartialTickIgnorePause();

        matrices.translate(0,0,-75);

        this.animationSystem.applyAnimations(malkuthModel, pticks);

        FDRenderUtil.renderFDModelInScreen(matrices, malkuthModel, bossX, bossY, 0, FDMathUtil.FPI, 0, 50, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_screen.png")));

        FDRenderUtil.renderFDModelInScreen(matrices, malkuthModel, bossX, bossY, 0, FDMathUtil.FPI, 0, 50, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth_screen_emissive.png")));

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
