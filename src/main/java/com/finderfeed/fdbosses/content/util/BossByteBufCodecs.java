package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;

public class BossByteBufCodecs {

    public static NetworkCodec<AABB> AABB = new NetworkCodec<AABB>() {
        @Override
        public AABB fromNetwork(FriendlyByteBuf buf) {
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
        public void toNetwork(FriendlyByteBuf b, AABB aabb) {
            b.writeDouble(aabb.minX);
            b.writeDouble(aabb.minY);
            b.writeDouble(aabb.minZ);
            b.writeDouble(aabb.maxX);
            b.writeDouble(aabb.maxY);
            b.writeDouble(aabb.maxZ);
        }
    };

}
