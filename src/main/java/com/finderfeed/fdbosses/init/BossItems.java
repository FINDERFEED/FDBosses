package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.EyeOfChesed;
import com.finderfeed.fdbosses.content.items.WeaponCoreItem;
import com.finderfeed.fdbosses.debug.DebugStick;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(FDBosses.MOD_ID);

    public static final Supplier<Item> DEBUG_STICK = ITEMS.register("debug",()->new DebugStick(new Item.Properties()));

    public static final Supplier<Item> MALKUTH_CANNON_REPAIR_MATERIAL_ICE = ITEMS.register("malkuth_cannon_repair_material_ice",()->new Item(new Item.Properties()));
    public static final Supplier<Item> MALKUTH_CANNON_REPAIR_MATERIAL_FIRE = ITEMS.register("malkuth_cannon_repair_material_fire",()->new Item(new Item.Properties()));

    public static final Supplier<WeaponCoreItem> LIGHTNING_CORE = ITEMS.register("lightning_core",()->new WeaponCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.LIGHTNING));
    public static final Supplier<WeaponCoreItem> FIRE_AND_ICE_CORE = ITEMS.register("fire_and_ice_core",()->new WeaponCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.FIRE_AND_ICE));

    public static final Supplier<EyeOfChesed> EYE_OF_CHESED = ITEMS.register("eye_of_chesed", EyeOfChesed::new);

    public static final Supplier<SpawnEggItem> MALKUTH_FIRE_WARRIOR_SPAWN_EGG = ITEMS.register("malkuth_fire_warrior_spawn_egg", ()->new DeferredSpawnEggItem(BossEntities.FIRE_MALKUTH_WARRIOR,
            0xffffff,0xffffff,new Item.Properties()));

    public static final Supplier<SpawnEggItem> MALKUTH_ICE_WARRIOR_SPAWN_EGG = ITEMS.register("malkuth_ice_warrior_spawn_egg", ()->new DeferredSpawnEggItem(BossEntities.ICE_MALKUTH_WARRIOR,
            0xffffff,0xffffff,new Item.Properties()));

    public static final Supplier<Item> NO_ENTITY_SPAWN_BLOCK = ITEMS.register("no_entity_spawn_block",
            ()->new BlockItem(BossBlocks.NO_ENTITY_SPAWN_BLOCK.get(), new Item.Properties()));

    public static final Supplier<Item> CHESED_TROPHY = ITEMS.register("chesed_trophy",
            ()->new BlockItem(BossBlocks.CHESED_TROPHY.get(), new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> MALKUTH_TROPHY = ITEMS.register("malkuth_trophy",
            ()->new BlockItem(BossBlocks.MALKUTH_TROPHY.get(), new Item.Properties().stacksTo(1)));

    @EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class AddToCreativeTabs{

        @SubscribeEvent
        public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event){
            if (event.getTab().equals(BossCreativeTabs.MAIN.get())){
                event.accept(LIGHTNING_CORE.get());
                event.accept(EYE_OF_CHESED.get());
                event.accept(CHESED_TROPHY.get());
                event.accept(MALKUTH_TROPHY.get());
            }else if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)){
                event.accept(MALKUTH_FIRE_WARRIOR_SPAWN_EGG.get());
                event.accept(MALKUTH_ICE_WARRIOR_SPAWN_EGG.get());
            }
        }

    }

}
