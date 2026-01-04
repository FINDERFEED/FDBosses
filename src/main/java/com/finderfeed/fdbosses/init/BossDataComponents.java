package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.malkuth.MalkuthFistDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, FDBosses.MOD_ID);

    public static final Supplier<DataComponentType<ItemCoreDataComponent>> ITEM_CORE = DATA_COMPONENTS.register("core", ()-> DataComponentType.<ItemCoreDataComponent>builder()
            .networkSynchronized(ItemCoreDataComponent.STREAM_CODEC)
            .persistent(ItemCoreDataComponent.CODEC)
            .build());

    public static final Supplier<DataComponentType<MalkuthFistDataComponent>> MALKUTH_FIST_COMPONENT = DATA_COMPONENTS.register("malkuth_fist", ()-> DataComponentType.<MalkuthFistDataComponent>builder()
            .networkSynchronized(MalkuthFistDataComponent.STREAM_CODEC)
            .persistent(MalkuthFistDataComponent.CODEC)
            .build());

}
