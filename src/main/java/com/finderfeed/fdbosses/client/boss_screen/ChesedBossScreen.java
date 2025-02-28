package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.ClientsideEntityAnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.EntityAnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.RenderShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;


@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChesedBossScreen extends Screen {

    private static FDModel chesed;

    private AnimationSystem animationSystem;

    private float relX;
    private float relY;

    public ChesedBossScreen() {
        super(Component.empty());
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



        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        this.relX = width / 2 - this.getScreenWidth() / 2;
        this.relY = height / 2 - this.getScreenHeight() / 2;

    }

    @Override
    public void tick() {
        super.tick();
        animationSystem.tick();
        animationSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_IDLE).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {
        super.render(graphics, mx, my, pticks);

        PoseStack matrices = graphics.pose();

        var anchor = this.getAnchor(0.5f,0);
        var w = this.getDownRightAnchor().sub(anchor);
        float offs = 5;

        FDRenderUtil.fill(matrices,anchor.x + offs,anchor.y + offs,w.x - offs * 2,w.y - offs * 2,1,1,1,1f);


        this.renderBoss(graphics, mx, my, pticks);
    }

    protected void renderBoss(GuiGraphics graphics,float mx,float my,float pticks){
        PoseStack matrices = graphics.pose();

        this.animationSystem.applyAnimations(chesed,pticks);

        var anchor = this.getAnchor(0.25f,0.5f);

        float offsetX = 0;
        float offsetY = 100;

        BossRenderUtil.renderFDModelInScreen(matrices,chesed,anchor.x + offsetX,anchor.y + offsetY,
                0, FDMathUtil.FPI + FDMathUtil.FPI / 8,0,50);
    }

    public Vector2f getAnchor(float wMod,float hMod){
        Window window = Minecraft.getInstance().getWindow();
        float height = window.getGuiScaledHeight();
        float width = window.getGuiScaledWidth();
        return new Vector2f(width * wMod,height * hMod);
    }

    public Vector2f getCenterAnchor(){
        return this.getAnchor(0.5f,0.5f);
    }

    public Vector2f getUpRightAnchor(){
        return this.getAnchor(1,0);
    }

    public Vector2f getDownRightAnchor(){
        return this.getAnchor(1,1);
    }

    public Vector2f getDownLeftAnchor(){
        return this.getAnchor(0,1);
    }

    public float getScreenWidth(){
        return 300;
    }

    public float getScreenHeight(){
        return 200;
    }




    @SubscribeEvent
    public static void key(InputEvent.Key event){
        if (Minecraft.getInstance().level == null) return;
        if (event.getKey() == GLFW.GLFW_KEY_M){
            Minecraft.getInstance().setScreen(new ChesedBossScreen());
        }
    }
}
