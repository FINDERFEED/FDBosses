package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:geburah_prepare_laser_attack")
public class GeburahPrepareAttackPacket extends FDPacket {

    public int geburah;
    public int timeUntilAttack;

    public GeburahPrepareAttackPacket(GeburahEntity geburah, int timeUntilAttack){
        this.geburah = geburah.getId();
        this.timeUntilAttack = timeUntilAttack;
    }

    public GeburahPrepareAttackPacket(FriendlyByteBuf buf){
        this.geburah = buf.readInt();
        this.timeUntilAttack = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(geburah);
        registryFriendlyByteBuf.writeInt(timeUntilAttack);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.launchGeburahLaserPrepare(geburah, timeUntilAttack);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }
}
