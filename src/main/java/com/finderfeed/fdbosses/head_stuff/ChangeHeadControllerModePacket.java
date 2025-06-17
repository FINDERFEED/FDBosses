package com.finderfeed.fdbosses.head_stuff;

import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdlib:head_controller_change_mode")
public class ChangeHeadControllerModePacket<T extends Mob & AnimatedObject  & IHasHead<T>> extends FDPacket {

    private HeadControllerContainer.Mode mode;
    private int entityId;

    public ChangeHeadControllerModePacket(T entity, HeadControllerContainer.Mode mode){
        this.entityId = entity.getId();
        this.mode = mode;
    }

    public ChangeHeadControllerModePacket(RegistryFriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.mode = buf.readEnum(HeadControllerContainer.Mode.class);
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(entityId);
        registryFriendlyByteBuf.writeEnum(mode);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        //to packet class
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof Mob mob){
            T e = (T) mob;
            e.getHeadControllerContainer().setControllersMode(mode);
        }
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
