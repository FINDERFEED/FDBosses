package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerSin {

    public abstract void playerTick(Player player, ActivePlayerSinInstance sinInstance);

    public abstract void onSinAdded(Player player, ActivePlayerSinInstance sinInstance);

}
