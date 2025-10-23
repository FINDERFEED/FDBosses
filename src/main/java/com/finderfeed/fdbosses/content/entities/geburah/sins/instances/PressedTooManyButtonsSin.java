package com.finderfeed.fdbosses.content.entities.geburah.sins.instances;

import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.ActivePlayerSinInstance;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import net.minecraft.world.entity.player.Player;

public class PressedTooManyButtonsSin extends PlayerSin {

    public static final int MAX_BUTTON_PRESSES_ALLOWED = 5;

    @Override
    public void playerTick(Player player, ActivePlayerSinInstance sinInstance) {

    }

    @Override
    public void onSinAdded(Player player, ActivePlayerSinInstance sinInstance) {

    }

}
