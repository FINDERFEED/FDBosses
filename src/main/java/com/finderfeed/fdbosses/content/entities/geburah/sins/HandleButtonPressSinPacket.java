package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.content.entities.geburah.sins.instances.PressedTooManyButtonsSin;
import com.finderfeed.fdbosses.init.GeburahSins;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:player_pressed_button")
public class HandleButtonPressSinPacket extends FDPacket {

    public HandleButtonPressSinPacket(){

    }

    public HandleButtonPressSinPacket(FriendlyByteBuf buf){
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {

    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

        Player player = iPayloadContext.player();

        var sins = PlayerSins.getPlayerSins(player);

        if (BossUtil.isPlayerInSurvival(player) && sins.hasSinActive(GeburahSins.PRESSED_TOO_MANY_BUTTONS_SIN.get())){

            var sin = sins.getSin(GeburahSins.PRESSED_TOO_MANY_BUTTONS_SIN.get());

            int pressedTimes = sin.getCustomData();

            if (pressedTimes + 1 > PressedTooManyButtonsSin.MAX_BUTTON_PRESSES_ALLOWED) {
                PlayerSinsHandler.sin((ServerPlayer) player, 0);
            }else{
                sin.setCustomData(pressedTimes + 1);
            }

        }

    }

}
