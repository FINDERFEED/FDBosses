package com.finderfeed.fdbosses.content.data_components;

import com.finderfeed.fdbosses.content.items.CoreItem;
import com.finderfeed.fdbosses.init.BossItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.function.Supplier;

public class ItemCoreDataComponent {

    public static final Codec<ItemCoreDataComponent> CODEC = RecordCodecBuilder.create(p->p.group(
            Codec.STRING.fieldOf("type").forGetter(v->v.getCoreType().name())
    ).apply(p,ItemCoreDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemCoreDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,v->v.getCoreType().name(),
            ItemCoreDataComponent::new
    );

    private CoreType coreType;

    public ItemCoreDataComponent(CoreType coreType){
        this.coreType = coreType;
    }

    private ItemCoreDataComponent(String coreType){
        this.coreType = CoreType.valueOf(coreType);
    }

    public CoreType getCoreType() {
        return coreType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCoreDataComponent that = (ItemCoreDataComponent) o;
        return coreType == that.coreType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coreType);
    }

    public enum CoreType {
        LIGHTNING(BossItems.LIGHTNING_CORE,0x11ffff),
        FIRE_AND_ICE(BossItems.FIRE_AND_ICE_CORE, 0xffaa11),
        JUSTICE_CORE(BossItems.JUSTICE_CORE, 0x11aaff)

        ;

        private Supplier<CoreItem> item;
        private int textColor;

        CoreType(Supplier<CoreItem> item, int textColor){
            this.item = item;
            this.textColor = textColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public CoreItem getItem() {
            return item.get();
        }

    }

}
