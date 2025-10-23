package com.finderfeed.fdbosses.content.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;

public class BossByteBufCodecs {

    public static StreamCodec<? super FriendlyByteBuf, AABB> AABB = new StreamCodec<FriendlyByteBuf, net.minecraft.world.phys.AABB>() {
        @Override
        public net.minecraft.world.phys.AABB decode(FriendlyByteBuf buf) {
            return new AABB(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        }

        @Override
        public void encode(FriendlyByteBuf b, net.minecraft.world.phys.AABB aabb) {
            b.writeDouble(aabb.minX);
            b.writeDouble(aabb.minY);
            b.writeDouble(aabb.minZ);
            b.writeDouble(aabb.maxX);
            b.writeDouble(aabb.maxY);
            b.writeDouble(aabb.maxZ);
        }
    };

}
