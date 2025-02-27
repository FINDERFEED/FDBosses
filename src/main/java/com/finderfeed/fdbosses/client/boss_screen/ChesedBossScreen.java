package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.ClientsideEntityAnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.EntityAnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;


@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChesedBossScreen extends Screen {

    private ChesedEntity chesedEntity;

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

//        FDRenderUtil.fill(matrices,relX,relY,this.getScreenWidth(),this.getScreenHeight(),0,0,0,1f);

        float offs = 3;

//        FDRenderUtil.fill(matrices, relX + offs, relY + offs,this.getScreenWidth() / 2 - offs,this.getScreenHeight() / 2 - offs * 1.5f,1f,0f,0f,1f);
//        FDRenderUtil.fill(matrices, relX + offs, relY  + this.getScreenHeight() / 2f + 0.5f * offs,this.getScreenWidth() / 2 - offs,this.getScreenHeight() / 2 - offs * 1.5f,0f,0f,1f,1f);




        matrices.pushPose();



        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")));

        matrices.translate(relX + 100,relY + 100,0);

        matrices.mulPose(Axis.YN.rotationDegrees(160));

        matrices.scale(-30f,-30f,-30f);

        Lighting.setupForEntityInInventory();

        animationSystem.applyAnimations(chesed,pticks);
        chesed.render(matrices,builder,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,1f,1f,1f,1f);



        Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();


        Lighting.setupFor3DItems();

        matrices.popPose();


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
