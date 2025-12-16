package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreenOptions;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GeburahBossScreen extends BaseBossScreen {

    private AnimationSystem animationSystem;

    private float bossX;
    private float bossY;

    private int geburahHelicopterTick = 0;
    private int oldTick = 0;
    private int geburahRotationSpeed = -1;


    public GeburahBossScreen(int bossSpawnerId, List<Item> possibleDrops, BossScreenOptions options) {
        super(bossSpawnerId, possibleDrops, options);
        this.animationSystem = new AnimationSystem() {
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
    protected void init() {
        super.init();
        Vector2f anchor = this.getAnchor(0, 0.5f);

        this.bossX = this.getBossMenuXStart()/2;
        this.bossY = anchor.y + 100;

        float height = 150;

        geburahRotationSpeed = -1;

        FDButton button = new FDButton(this, bossX - 30, bossY - height, 60,height)
                .setOnClickAction(((fdWidget, mx, my, b) -> {
                    if (b == GLFW.GLFW_MOUSE_BUTTON_LEFT){
                        if (geburahRotationSpeed == -1){
                            geburahRotationSpeed = 0;
                        }
                        return true;
                    }
                    return false;
                }));

        this.addWidget(button);
    }

    @Override
    public void renderBoss(GuiGraphics graphics, float mx, float my, float pticks) {

        FDModel model = GeburahEntity.getClientModel();



        float time = FDMathUtil.lerp(oldTick, geburahHelicopterTick, pticks) / 100f;


        float yOffs = 0;
        if (geburahRotationSpeed > 100){
            yOffs  = -(geburahRotationSpeed - 100 +  pticks) * 5;
        }


        FDRenderUtil.renderFDModelInScreen(graphics.pose(), model, bossX, bossY + yOffs,25,45 + time,0,8, RenderType.entityCutout(FDBosses.location("textures/entities/geburah/geburah.png")));

    }

    @Override
    public void tick() {
        super.tick();
        animationSystem.tick();

        oldTick = geburahHelicopterTick;

        if (geburahRotationSpeed == 100){
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            soundManager.play(SimpleSoundInstance.forUI(BossSounds.GEBURAH_HELICOPTER.get(), 1f, 1f));
        }

        if (geburahRotationSpeed != -1){
            geburahRotationSpeed++;
            geburahHelicopterTick += geburahRotationSpeed;
        }else{
            geburahHelicopterTick++;
        }

    }

    @Override
    public Component getBossName() {
        return BossEntities.GEBURAH.get().getDescription();
    }

    @Override
    public int getBaseStringColor() {
        return DEFAULT_TEXT_COLOR;
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
