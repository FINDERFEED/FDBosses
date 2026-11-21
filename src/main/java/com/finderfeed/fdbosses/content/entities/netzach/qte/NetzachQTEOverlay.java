package com.finderfeed.fdbosses.content.entities.netzach.qte;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class NetzachQTEOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation WARNING = FDBosses.location("textures/gui/netzach_minigame/netzach_qte_warning.png");

    private static final double TERMINAL_DOT = 0.85;
    private static List<NetzachQteAttack> attacks = List.of();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event){
        if (event.getEntity() instanceof LocalPlayer player){
            attacks = player.level().getEntitiesOfClass(NetzachQteAttack.class, player.getBoundingBox().inflate(10), attack -> {
                return attack.getOwner() == player && !attack.isResolved();
            });
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event){
        attacks = List.of();
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker){
        var player = Minecraft.getInstance().player;
        if (player == null || attacks.isEmpty()) return;

        float partialTick = tracker.getGameTimeDeltaPartialTick(false);
        Vec3 forward = player.getLookAngle().multiply(1,0,1).normalize();
        Vec3 playerPos = player.getPosition(partialTick);
        boolean left = false;
        boolean right = false;

        for (var attack : attacks){
            if (attack.isRemoved() || attack.isResolved() || attack.getOwner() != player) continue;

            Vec3 direction = attack.getPosition(partialTick).subtract(playerPos).multiply(1, 0, 1).normalize();

            if (direction.lengthSqr() == 0 || forward.dot(direction) >= TERMINAL_DOT) continue;

            if (forward.cross(direction).y > 0){
                left = true;
            }else{
                right = true;
            }
            if (left && right) break;
        }

        if (!left && !right){
            return;
        }

        FDRenderUtil.bindTexture(WARNING);
        int signU = (player.tickCount / 2 % 2) * 4;

        if (left){
            this.drawWarning(graphics, false, signU);
        }

        if (right){
            this.drawWarning(graphics, true, signU);
        }
    }

    private void drawWarning(GuiGraphics graphics, boolean right, int signU){
        var pose = graphics.pose();

        Window window = Minecraft.getInstance().getWindow();

        pose.pushPose();
        pose.translate(right ? window.getGuiScaledWidth() - 36 : 10, (window.getGuiScaledHeight() - 100) / 2f, 0);
        FDRenderUtil.blitWithBlend(pose, 0, 0, 26, 100, right ? 26 : 0, 0, 26, 100, 52, 124, 0, 1);
        FDRenderUtil.blitWithBlend(pose, right ? -8 : 30, 38, 4, 24, signU, 100, 4, 24, 52, 124, 0, 1);
        pose.popPose();
    }

}
