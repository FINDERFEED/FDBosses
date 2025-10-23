package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.ActivePlayerSinInstance;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.content.util.WorldBox;
import com.finderfeed.fdbosses.init.BossDataAttachments;
import com.finderfeed.fdbosses.init.GeburahSins;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Iterator;

@EventBusSubscriber(modid = FDBosses.MOD_ID)
public class PlayerSinsHandler {

    @SubscribeEvent
    public static void tickPlayer(PlayerTickEvent.Pre event){
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide) {
            tickPlayerSins(player);
        }
    }


    public static void tickPlayerSins(Player player){
        PlayerSins playerSins = PlayerSins.getPlayerSins(player);

        Iterator<ActivePlayerSinInstance> instanceIterator = playerSins.getActiveSins().iterator();

        int currentCooldown = playerSins.getSinGainCooldown();
        playerSins.setSinGainCooldown(Mth.clamp(currentCooldown - 1,0,Integer.MAX_VALUE));

        while (instanceIterator.hasNext()){
            ActivePlayerSinInstance activeSin = instanceIterator.next();

            WorldBox worldBox = activeSin.getSinActiveBox();

            if (worldBox.isEntityInBox(player)) {
                var sin = activeSin.getSin();
                sin.playerTick(player, activeSin);
                activeSin.setActiveSinTime(activeSin.getActiveSinTime() + 1);
            }else{
                instanceIterator.remove();
            }
        }

    }

    public static void sin(ServerPlayer player, int cooldown){
        PlayerSins playerSins = PlayerSins.getPlayerSins(player);
        if (!playerSins.isGainingSinsOnCooldown() && BossUtil.isPlayerInSurvival(player)) {
            PacketDistributor.sendToPlayer(player, new GeburahTriggerSinEffectPacket());
            playerSins.setSinnedTimes(playerSins.getSinnedTimes() + 1);
            playerSins.setSinGainCooldown(cooldown);
            player.setData(BossDataAttachments.PLAYER_SINS, playerSins);
        }
    }

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent event){
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer player){
            PlayerSins playerSins = PlayerSins.getPlayerSins(player);
            if (playerSins.hasSinActive(GeburahSins.JUMPING_SIN.get())) {
                sin(player, 100);
            }
        }
    }




}
