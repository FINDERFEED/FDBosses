package com.finderfeed.fdbosses.client.overlay;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdlib.init.FDCoreShaders;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDShaderRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = FDBosses.MOD_ID,value = Dist.CLIENT)
public class ElectrifiedOverlay implements IGuiOverlay {

    private static int electrifiedEffectTick = 0;
    private static int electrifiedEffectTickO = 0;
    private static int electrifiedEffectTickMax = 20;


    @SubscribeEvent
    public static void detectChesedEffect(TickEvent.ClientTickEvent event){

        if (event.phase != TickEvent.Phase.START) return;
        if (event.side != LogicalSide.CLIENT) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            electrifiedEffectTick = 0;
            electrifiedEffectTickO = 0;
            return;
        }

        electrifiedEffectTickMax = 10;


        electrifiedEffectTickO = electrifiedEffectTick;
        if (player.hasEffect(BossEffects.CHESED_ENERGIZED.get())){
            electrifiedEffectTick = Mth.clamp(electrifiedEffectTick + 1,0,electrifiedEffectTickMax);
        }else{
            electrifiedEffectTick = Mth.clamp(electrifiedEffectTick - 1,0,electrifiedEffectTickMax);
        }

    }

    public static float getElectrifiedEffectPercent(float partialTick){
        return FDMathUtil.lerp(electrifiedEffectTickO,electrifiedEffectTick,partialTick) / electrifiedEffectTickMax;
    }


    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {

        if (electrifiedEffectTick == 0 && electrifiedEffectTickO == 0) return;

        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        PoseStack matrices = graphics.pose();
        matrices.pushPose();

        float alphamod = getElectrifiedEffectPercent(partialTick);

        alphamod = FDEasings.easeOut(alphamod);

        renderElectrifiedThing(graphics,0,0,30,alphamod);
        renderElectrifiedThing(graphics,width,0,-30,alphamod);
        renderElectrifiedThing(graphics,width,height,205,alphamod);
        renderElectrifiedThing(graphics,0,height,-205,alphamod);

        matrices.popPose();
    }

    private void renderElectrifiedThing(GuiGraphics graphics,float x,float y,float angle,float alphamod){

        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        float time = (System.currentTimeMillis() % 356556456) / 100000f;

        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;

        PoseStack matrices = graphics.pose();

        matrices.pushPose();

        matrices.translate(x,y,0);

        float noiseAlpha = 0.7f * alphamod;

        matrices.mulPose(Axis.ZN.rotationDegrees(angle));

        matrices.pushPose();
        matrices.translate(0,20,0);
        FDShaderRenderer.start(graphics, FDCoreShaders.NOISE)
                .position(0,0,0)
                .setUpColor(0.5f,1f,1f,noiseAlpha)
                .setDownColor(0.5f,1f,1f,0f)
                .setResolution(width,40)
                .setCentered(true)
                .setShaderUniform("size",width,40)
                .setShaderUniform("xyOffset",0,-time * 3)
                .setShaderUniform("sections",50)
                .setShaderUniform("octaves",4)
                .setShaderUniform("time",time * 5)
                .end();
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(0,40,0);
        FDShaderRenderer.start(graphics, GameRenderer.getPositionColorShader())
                .position(0,0,0)
                .setUpColor(0.5f,1f,1f,noiseAlpha)
                .setDownColor(0.5f,1f,1f,0f)
                .setResolution(width,80)
                .setCentered(true)
                .end();
        matrices.popPose();

        float lightningAlpha = alphamod;

        matrices.pushPose();
        matrices.translate(0,30,0);
        Matrix4f t = matrices.last().pose();

        List<Vec3> positions = List.of(
                new Vec3(-150,0,0),
                new Vec3(150,0,0)
        );
        ArcLightningParticle.fullLightningImmediateDraw(System.currentTimeMillis() / 50,42354355,10,t, positions, 5f,10,0.3f,1f,1f,alphamod);
        ArcLightningParticle.fullLightningImmediateDraw(System.currentTimeMillis() / 50,42354355,10,t, positions, 1.5f,10,1f,1f,1f,alphamod);
        ArcLightningParticle.fullLightningImmediateDraw(System.currentTimeMillis() / 50,54354355,10,t, positions, 1.5f,20,1f,1f,1f,alphamod);
        matrices.popPose();



        matrices.popPose();
    }
}
