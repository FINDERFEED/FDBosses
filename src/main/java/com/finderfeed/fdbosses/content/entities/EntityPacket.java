package com.finderfeed.fdbosses.content.entities;

import com.finderfeed.fdlib.network.FDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public abstract class EntityPacket<T extends Entity> extends FDPacket {

    public int id;

    public EntityPacket(T entity){
        this.id = entity.getId();
    }

    public EntityPacket(RegistryFriendlyByteBuf byteBuf){
        this.id = byteBuf.readInt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.id);
    }

}
