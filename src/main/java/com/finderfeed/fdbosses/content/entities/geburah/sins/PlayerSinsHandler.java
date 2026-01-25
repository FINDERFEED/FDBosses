package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.ActivePlayerSinInstance;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.content.util.WorldBox;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.GeburahSins;
import com.finderfeed.fdlib.network.FDPacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSinsHandler {

    @SubscribeEvent
    public static void tickPlayer(TickEvent.PlayerTickEvent event){

        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
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

        PlayerSins.setPlayerSins(player, playerSins, false);

    }

    public static void setActiveSins(ServerPlayer player, List<ActivePlayerSinInstance> instances){
        PlayerSins playerSins = PlayerSins.getPlayerSins(player);
        playerSins.setActiveSins(instances);
        PlayerSins.setPlayerSins(player,playerSins);
    }

    public static void sin(ServerPlayer player, int cooldown){
        sin(player,cooldown,1, 1);
    }

    public static void sin(ServerPlayer player, int cooldown, float soundPitch, int amount){
        PlayerSins playerSins = PlayerSins.getPlayerSins(player);
        if (!playerSins.isGainingSinsOnCooldown() && BossUtil.isPlayerInSurvival(player) && player.isAlive()) {

            FDPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new GeburahTriggerSinEffectPacket(soundPitch));

            int sinnedTimes = playerSins.getSinnedTimes();

            if (sinnedTimes + amount >= BossConfigs.BOSS_CONFIG.get().geburahConfig.maxPlayerSins){
                player.hurt(BossDamageSources.GEBURAH_SINNED_TOO_MUCH_SOURCE, BossUtil.JUST_ENOUGH_DAMAGE);
            }else {
                playerSins.setSinnedTimes(sinnedTimes + amount);
                playerSins.setSinGainCooldown(cooldown);

                PlayerSins.setPlayerSins(player, playerSins);
            }
        }
    }

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent event){
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer player){
            PlayerSins playerSins = PlayerSins.getPlayerSins(player);
            if (playerSins.hasSinActive(GeburahSins.JUMPING_SIN.get())) {
                sin(player, 40);
            }
        }
    }




}
