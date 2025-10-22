package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID)
public class PlayerSinsHandler {

    @SubscribeEvent
    public static void tickPlayer(PlayerTickEvent.Pre event){
        Player player = event.getEntity();
        PlayerSins.tickPlayerSins(player);
    }

}
