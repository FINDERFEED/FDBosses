package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.packets.SetClientMalkuthWeaknessAmountPacket;
import com.finderfeed.fdbosses.init.BossDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class MalkuthWeaknessHandler {

    //positive - fire weakness
    //negative - ice weakness

    public static final int MAX = 100;
    public static final int MIN = -MAX;

    public static int getCurrentWeaknessLevel(Player player){
        return Math.clamp(player.getData(BossDataAttachments.MALKUTH_WEAKNESS.get()), MIN, MAX);
    }

    public static void setCurrentWeakness(Player player, int amount){
        player.setData(BossDataAttachments.MALKUTH_WEAKNESS.get(), Math.clamp(amount,MIN,MAX));
        if (player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new SetClientMalkuthWeaknessAmountPacket(amount));
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
                setCurrentWeakness(player, current + amount);
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
