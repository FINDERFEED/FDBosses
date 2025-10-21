package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BossAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FDBosses.MOD_ID);

    public static final Supplier<AttachmentType<PlayerSins>> PLAYER_SINS = ATTACHMENT_TYPES.register("player_sins", ()->{
        return AttachmentType.builder(v->new PlayerSins())
                .serialize(PlayerSins.CODEC)
                .copyHandler(((attachment, holder, provider) -> {
                    return new PlayerSins(attachment);
                }))
                .build();
    });

}
