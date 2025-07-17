package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BossDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FDBosses.MOD_ID);

    public static final Supplier<AttachmentType<Integer>> MALKUTH_WEAKNESS = ATTACHMENTS.register("malkuth_weakness",()-> AttachmentType.builder(()->0)
            .copyOnDeath()
            .serialize(Codec.INT)
            .copyHandler((attachment, holder, provider) -> {
                int a = (int) attachment;
                return a;
            })
            .build());

}
