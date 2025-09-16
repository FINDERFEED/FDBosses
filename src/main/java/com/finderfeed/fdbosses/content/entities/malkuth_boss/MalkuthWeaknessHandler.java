package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.packets.SetClientMalkuthWeaknessAmountPacket;
import com.finderfeed.fdbosses.init.BossDataAttachments;
import com.finderfeed.fdlib.network.FDPacketHandler;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.joml.Math;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = FDBosses.MOD_ID)
public class MalkuthWeaknessHandler {

    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone event){
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        if (!original.level().isClientSide){
            setCurrentWeakness(newPlayer, getCurrentWeaknessLevel(original));
        }
    }


    public static String WEAKNESS_LEVEL = "weakness_level";

    //positive - fire weakness
    //negative - ice weakness

    public static final int MAX = 100;
    public static final int MIN = -MAX;

    public static CompoundTag getWeaknessTag(Player player){
        return BossUtil.getOrCreateTag(BossUtil.getPlayerTag(player),"malkuth_weakness");
    }


    public static int getCurrentWeaknessLevel(Player player){
        var tag = getWeaknessTag(player);
        return FDMathUtil.clamp(tag.getInt(WEAKNESS_LEVEL), MIN, MAX);
    }

    public static void setCurrentWeakness(Player player, int amount){
        amount = FDMathUtil.clamp(amount, MIN, MAX);
        var tag = getWeaknessTag(player);
        tag.putInt(WEAKNESS_LEVEL, amount);
        if (player instanceof ServerPlayer serverPlayer){
            FDPacketHandler.INSTANCE.sendTo(new SetClientMalkuthWeaknessAmountPacket(amount), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void damageWeakness(MalkuthAttackType damageType, Player player, int amount){
        int current = getCurrentWeaknessLevel(player);
        if (current >= 0){
            if (damageType.isIce()){
                if (current - amount < 0){
                    setCurrentWeakness(player, current - amount * 2);
                }else{
                    setCurrentWeakness(player, current - amount);
                }
            }else{
                setCurrentWeakness(player, current + amount);
            }
        }else{
            if (damageType.isFire()){
                if (current + amount >= 0){
                    setCurrentWeakness(player, current + amount * 2);
                }else{
                    setCurrentWeakness(player, current + amount);
                }
            }else{
                setCurrentWeakness(player, current - amount);
            }
        }
    }

    public static MalkuthAttackType getWeakTo(Player player){
        return getCurrentWeaknessLevel(player) >= 0 ? MalkuthAttackType.FIRE : MalkuthAttackType.ICE;
    }

    public static boolean isWeakTo(Player player, MalkuthAttackType malkuthAttackType){
        return getWeakTo(player).equals(malkuthAttackType);
    }

}
