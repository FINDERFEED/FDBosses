package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.content.items.EyeOfChesed;
import com.finderfeed.fdbosses.content.items.LightningCore;
import com.finderfeed.fdbosses.debug.DebugStick;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(FDBosses.MOD_ID);

    public static final Supplier<Item> DEBUG_STICK = ITEMS.register("debug",()->new DebugStick(new Item.Properties()));

    public static final Supplier<LightningCore> LIGHTNING_CORE = ITEMS.register("lightning_core",()->new LightningCore(new Item.Properties().stacksTo(1)));

    public static final Supplier<EyeOfChesed> EYE_OF_CHESED = ITEMS.register("eye_of_chesed", EyeOfChesed::new);

    public static final Supplier<Item> NO_ENTITY_SPAWN_BLOCK = ITEMS.register("no_entity_spawn_block",
            ()->new BlockItem(BossBlocks.NO_ENTITY_SPAWN_BLOCK.get(), new Item.Properties()));

    public static final Supplier<Item> CHESED_TROPHY = ITEMS.register("chesed_trophy",
            ()->new BlockItem(BossBlocks.CHESED_TROPHY.get(), new Item.Properties().stacksTo(1)));

    @EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class AddToCreativeTabs{

        @SubscribeEvent
        public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event){
            if (event.getTab().equals(BossCreativeTabs.MAIN.get())){
                event.accept(LIGHTNING_CORE.get());
                event.accept(EYE_OF_CHESED.get());
                event.accept(CHESED_TROPHY.get());
            }
        }

    }

}
