package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BossDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FDBosses.MOD_ID);

    public static final Supplier<AttachmentType<Integer>> MALKUTH_WEAKNESS = ATTACHMENTS.register("malkuth_weakness",()-> AttachmentType.builder(()->0)
            .serialize(new IAttachmentSerializer<CompoundTag, Integer>() {
                @Override
                public Integer read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                    return tag.getInt("weakness");
                }

                @Override
                public @Nullable CompoundTag write(Integer attachment, HolderLookup.Provider provider) {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("weakness", attachment);
                    return tag;
                }
            })
            .copyOnDeath()
            .copyHandler((attachment, holder, provider) -> {
                int a = (int) attachment;
                return a;
            })
            .build());

}
