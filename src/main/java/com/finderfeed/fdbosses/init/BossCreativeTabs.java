package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FDBosses.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN = CREATIVE_TABS.register("main",()-> CreativeModeTab.builder()
            .icon(()->BossItems.EYE_OF_CHESED.get().getDefaultInstance())
            .title(Component.translatable("fdbosses.word.fdbosses"))
            .build());

}
