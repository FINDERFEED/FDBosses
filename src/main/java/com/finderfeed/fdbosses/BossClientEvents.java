package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FDBosses.MOD_ID,bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class BossClientEvents {


    @SubscribeEvent
    public static void inputEvent(InputEvent.Key event){
        if (event.getKey() == GLFW.GLFW_KEY_I && Minecraft.getInstance().screen == null){
//            Minecraft.getInstance().setScreen(new ComponentTestScreen());
//            System.out.println(Minecraft.getInstance().font.getSplitter().splitLines("Test string to\nsplit",300, Style.EMPTY)
//                    .stream().map(FormattedText::getString).toList());
        }
    }

    public static int chesedGazeEffectTick = 0;
    public static int chesedGazeEffectTickO = 0;
    public static int chesedGazeEffectTickMax = 20;

    public static int chesedDarkenEffectTick = 0;
    public static int chesedDarkenEffectTickO = 0;
    public static int chesedDarkenEffectTickMax = 10;

    @SubscribeEvent
    public static void tickEvent(ClientTickEvent.Pre event){
        Player player = Minecraft.getInstance().player;
        tickChesedGaze(player);
        tickChesedDarken(player);
    }

    private static void tickChesedGaze(Player player){
        if (player != null){
            chesedGazeEffectTickO = chesedGazeEffectTick;
            if (player.hasEffect(BossEffects.CHESED_GAZE)){
                chesedGazeEffectTick = Mth.clamp(chesedGazeEffectTick + 1,0,chesedGazeEffectTickMax);
            }else{
                chesedGazeEffectTick = Mth.clamp(chesedGazeEffectTick - 1,0,chesedGazeEffectTickMax);
            }
        }else{
            chesedGazeEffectTick = 0;
        }
    }

    private static void tickChesedDarken(Player player){
        if (player != null){
            chesedDarkenEffectTickO = chesedDarkenEffectTick;
            if (player.hasEffect(BossEffects.CHESED_DARKEN)){
                chesedDarkenEffectTick = Mth.clamp(chesedDarkenEffectTick + 1,0,chesedDarkenEffectTickMax);
            }else{
                chesedDarkenEffectTick = Mth.clamp(chesedDarkenEffectTick - chesedDarkenEffectTickMax / 2,0,chesedDarkenEffectTickMax);
            }
        }else{
            chesedDarkenEffectTick = 0;
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event){

        if (chesedGazeEffectTick != 0) {
            chesedGazeEffectTickMax = 20;
            float p = getChesedGazePercent(event.getPartialTick());


            float r = event.getRed();
            float g = event.getGreen();
            float b = event.getBlue();

            event.setRed(r * (1 - p));
            event.setGreen(g * (1 - p));
            event.setBlue(b * (1 - p));

        }

    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event){


        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        if (chesedGazeEffectTick != 0) {
            event.setCanceled(true);

            float levelTime = (float) ((level.getGameTime() + event.getPartialTick()) % 120);
            levelTime /= 120;


            float p = getChesedGazePercent(event.getPartialTick());




            float farPlane = event.getFarPlaneDistance();
            float nearPlane = event.getNearPlaneDistance();
            event.setFogShape(FogShape.SPHERE);

            float farPlaneDistance = farPlane * (1 - p) + p * (2  * ((float)Math.sin(levelTime * FDMathUtil.FPI * 2) + 1) / 2 + 3);


            float nearPlaneDistance = nearPlane * (1 - p);

            event.setFarPlaneDistance(farPlaneDistance);
            event.setNearPlaneDistance(nearPlaneDistance);

        }
    }

    public static float getChesedGazePercent(double pticks){
        float time = (float) Mth.lerp(pticks,chesedGazeEffectTickO,chesedGazeEffectTick);
        float p = FDEasings.easeOut(time / chesedGazeEffectTickMax);
        return p;
    }

    public static float getChesedDarkenPercent(double pticks){
        float time = (float) Mth.lerp(pticks,chesedDarkenEffectTickO,chesedDarkenEffectTick);
        float p = FDEasings.easeOut(time / chesedDarkenEffectTickMax);
        return p;
    }


}
