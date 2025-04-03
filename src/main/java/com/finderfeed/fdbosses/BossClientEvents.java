package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;


@EventBusSubscriber(modid = FDBosses.MOD_ID,bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class BossClientEvents {

    public static Double cachedGamma = null;

    public static int chesedGazeEffectTick = 0;
    public static int chesedGazeEffectTickO = 0;
    public static int chesedGazeEffectTickMax = 20;

    public static int chesedDarkenEffectTick = 0;
    public static int chesedDarkenEffectTickO = 0;
    public static int chesedDarkenEffectTickMax = 10;


    @SubscribeEvent
    public static void collectTooltips(ItemTooltipEvent event){
        var componentList = event.getToolTip();
        ItemStack itemStack = event.getItemStack();
        if (itemStack.has(BossDataComponents.ITEM_CORE.get())){
            ItemCoreDataComponent itemCoreDataComponent = itemStack.get(BossDataComponents.ITEM_CORE);
            if (itemCoreDataComponent.getCoreType() == ItemCoreDataComponent.CoreType.LIGHTNING) {
                componentList.add(BossItems.LIGHTNING_CORE.get().getDefaultInstance().getDisplayName().copy().withStyle(Style.EMPTY.withColor(0x11ffff)));
            }
        }
    }

    @SubscribeEvent
    public static void tickEvent(ClientTickEvent.Pre event){
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            tickChesedGaze(player);
            tickChesedDarken(player);
            lowerGammaWhenAnyDarkenEffect(player);
        }
    }

    private static void lowerGammaWhenAnyDarkenEffect(Player player){
        Options options = Minecraft.getInstance().options;
        if (player.hasEffect(MobEffects.NIGHT_VISION) && player.hasEffect(BossEffects.CHESED_DARKEN)){
            if (cachedGamma == null){
                cachedGamma = options.gamma().get();
            }
            options.gamma().set(0d);
        }else{
            if (cachedGamma != null){
                options.gamma().set(cachedGamma);
                cachedGamma = null;
            }
        }
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
                chesedDarkenEffectTick = Mth.clamp(chesedDarkenEffectTick - chesedDarkenEffectTickMax / 4,0,chesedDarkenEffectTickMax);
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
