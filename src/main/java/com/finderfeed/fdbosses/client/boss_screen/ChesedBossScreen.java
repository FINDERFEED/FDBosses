package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.TextBlockParser;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
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
public class ChesedBossScreen extends BaseBossScreen {

    private static FDModel chesed;

    private AnimationSystem animationSystem;

    public ChesedBossScreen() {
        super();
    }

    @Override
    protected void init() {
        super.init();


        var components = TextBlockParser.parseComponent(Component.translatable("skill.crystal.main_description"),1f,false,this.getBaseStringColor());

        TextBlock textBlock = new TextBlock(this,10,10,400,100)
                .addTextBlockEntries(components);

//        this.addRenderableWidget(textBlock);
        

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
        return 0xffffff;
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
        var anchor = this.getAnchor(0.25f,0.5f);
        float offsetX = 0;
        float offsetY = 100;
        BossRenderUtil.renderFDModelInScreen(matrices,chesed,anchor.x + offsetX,anchor.y + offsetY,
                0, FDMathUtil.FPI + FDMathUtil.FPI / 8,0,50);
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
            Minecraft.getInstance().setScreen(new ChesedBossScreen());
        }
    }
}
