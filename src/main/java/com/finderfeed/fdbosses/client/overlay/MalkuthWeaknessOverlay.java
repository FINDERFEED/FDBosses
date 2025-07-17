package com.finderfeed.fdbosses.client.overlay;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthWeaknessHandler;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class MalkuthWeaknessOverlay implements LayeredDraw.Layer {

    public static final int MAX_IN_TIME = 10;

    private static int iceTicker = 0;
    private static int fireTicker = 0;

    private static int iceTickerO = 0;
    private static int fireTickerO = 0;

    @SubscribeEvent
    public static void tickClient(ClientTickEvent.Pre event){
        Player player = FDClientHelpers.getClientPlayer();
        if (player == null) {
            iceTicker = 0;
            fireTicker = 0;
            return;
        }

        MalkuthAttackType weakTo = MalkuthWeaknessHandler.getWeakTo(player);

        iceTickerO = iceTicker;
        fireTickerO = fireTicker;

        if (weakTo.isIce()){
            iceTicker = Math.clamp(iceTicker + 1, 0, MAX_IN_TIME);
            fireTicker = Math.clamp(fireTicker - 1, 0, MAX_IN_TIME);
        }else{
            iceTicker = Math.clamp(iceTicker - 1, 0, MAX_IN_TIME);
            fireTicker = Math.clamp(fireTicker + 1, 0, MAX_IN_TIME);
        }

    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {

        Window window = Minecraft.getInstance().getWindow();

        float w = window.getGuiScaledWidth();
        float h = window.getGuiScaledHeight();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);


        Vector3f ice = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.ICE);
        Vector3f fire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.FIRE);

        float pticks = tracker.getGameTimeDeltaPartialTick(false);

        float iceAlpha = FDMathUtil.lerp(iceTickerO,iceTicker,pticks) / MAX_IN_TIME;
        float fireAlpha = FDMathUtil.lerp(fireTickerO,fireTicker,pticks) / MAX_IN_TIME;

        float size = 5;


        PoseStack matrices = new PoseStack();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);


        for (int i = 0; i < 2;i++){
            matrices.pushPose();

            matrices.translate(w/2,h - size/2,0);

            QuadRenderer.start(builder)
                .pose(matrices)
                .direction(new Vec3(0,0,-1))
                .color1(ice.x,ice.y,ice.z,iceAlpha)
                .color2(ice.x,ice.y,ice.z,iceAlpha)
                .color3(ice.x,ice.y,ice.z,0)
                .color4(ice.x,ice.y,ice.z,0)
                .sizeX(w/2)
                .sizeY(size)
                .render();


            QuadRenderer.start(builder)
                    .pose(matrices)
                    .direction(new Vec3(0, 0, -1))
                    .color1(fire.x, fire.y, fire.z, fireAlpha)
                    .color2(fire.x, fire.y, fire.z, fireAlpha)
                    .color3(fire.x, fire.y, fire.z, 0)
                    .color4(fire.x, fire.y, fire.z, 0)
                    .sizeX(w/2)
                    .sizeY(size)
                    .render();

//            matrices.translate(0,-h + size,0);
//            QuadRenderer.start(builder)
//                .pose(matrices)
//                .direction(new Vec3(0,0,-1))
//                .color1(ice.x,ice.y,ice.z,0)
//                .color2(ice.x,ice.y,ice.z,0)
//                .color3(ice.x,ice.y,ice.z,iceAlpha)
//                .color4(ice.x,ice.y,ice.z,iceAlpha)
//                .sizeX(w/2)
//                .sizeY(size)
//                .render();
//
//
//            QuadRenderer.start(builder)
//                    .pose(matrices)
//                    .direction(new Vec3(0, 0, -1))
//                    .color1(fire.x, fire.y, fire.z, 0)
//                    .color2(fire.x, fire.y, fire.z, 0)
//                    .color3(fire.x, fire.y, fire.z, fireAlpha)
//                    .color4(fire.x, fire.y, fire.z, fireAlpha)
//                    .sizeX(w/2)
//                    .sizeY(size)
//                    .render();

            matrices.popPose();
        }

        BufferUploader.drawWithShader(builder.build());

        RenderSystem.defaultBlendFunc();

    }


}
