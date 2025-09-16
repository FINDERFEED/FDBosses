package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.LocatorEye;
import com.finderfeed.fdbosses.content.items.WeaponCoreItem;
import com.finderfeed.fdbosses.debug.DebugStick;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FDBosses.MOD_ID);

    public static final Supplier<Item> DEBUG_STICK = ITEMS.register("debug",()->new DebugStick(new Item.Properties()));

    public static final Supplier<Item> MALKUTH_CANNON_REPAIR_MATERIAL_ICE = ITEMS.register("malkuth_cannon_repair_material_ice",()->new Item(new Item.Properties()));
    public static final Supplier<Item> MALKUTH_CANNON_REPAIR_MATERIAL_FIRE = ITEMS.register("malkuth_cannon_repair_material_fire",()->new Item(new Item.Properties()));

    public static final Supplier<WeaponCoreItem> LIGHTNING_CORE = ITEMS.register("lightning_core",()->new WeaponCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.LIGHTNING,false,
            Component.translatable("fdbosses.core_ability.lightning_core").withStyle(ChatFormatting.AQUA)));
    public static final Supplier<WeaponCoreItem> FIRE_AND_ICE_CORE = ITEMS.register("fire_and_ice_core",()->new WeaponCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.FIRE_AND_ICE, false,
            Component.translatable("fdbosses.core_ability.fire_and_ice_core").withStyle(ChatFormatting.GOLD)));

    public static final Supplier<LocatorEye<?>> EYE_OF_CHESED = ITEMS.register("eye_of_chesed", ()->new LocatorEye<>(BossUtil.StructureTags.EYE_OF_CHESED_LOCATED, BossEntities.EYE_OF_CHESED));
    public static final Supplier<LocatorEye<?>> EYE_OF_MALKUTH = ITEMS.register("eye_of_malkuth", ()->new LocatorEye<>(BossUtil.StructureTags.EYE_OF_MALKUTH_LOCATED, BossEntities.EYE_OF_MALKUTH));

    public static final Supplier<SpawnEggItem> MALKUTH_FIRE_WARRIOR_SPAWN_EGG = ITEMS.register("malkuth_fire_warrior_spawn_egg", ()->new ForgeSpawnEggItem(BossEntities.FIRE_MALKUTH_WARRIOR,
            0xffffff,0xffffff,new Item.Properties()));

    public static final Supplier<SpawnEggItem> MALKUTH_ICE_WARRIOR_SPAWN_EGG = ITEMS.register("malkuth_ice_warrior_spawn_egg", ()->new ForgeSpawnEggItem(BossEntities.ICE_MALKUTH_WARRIOR,
            0xffffff,0xffffff,new Item.Properties()));

    public static final Supplier<Item> NO_ENTITY_SPAWN_BLOCK = ITEMS.register("no_entity_spawn_block",
            ()->new BlockItem(BossBlocks.NO_ENTITY_SPAWN_BLOCK.get(), new Item.Properties()));

    public static final Supplier<Item> CHESED_TROPHY = ITEMS.register("chesed_trophy",
            ()->new BlockItem(BossBlocks.CHESED_TROPHY.get(), new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> MALKUTH_TROPHY = ITEMS.register("malkuth_trophy",
            ()->new BlockItem(BossBlocks.MALKUTH_TROPHY.get(), new Item.Properties().stacksTo(1)));

    @Mod.EventBusSubscriber(modid = FDBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class AddToCreativeTabs{

        @SubscribeEvent
        public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event){
            if (event.getTab().equals(BossCreativeTabs.MAIN.get())){
                event.accept(LIGHTNING_CORE.get());
                event.accept(FIRE_AND_ICE_CORE.get());
                event.accept(EYE_OF_CHESED.get());
                event.accept(EYE_OF_MALKUTH.get());
                event.accept(CHESED_TROPHY.get());
                event.accept(MALKUTH_TROPHY.get());
            }else if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)){
                event.accept(MALKUTH_FIRE_WARRIOR_SPAWN_EGG.get());
                event.accept(MALKUTH_ICE_WARRIOR_SPAWN_EGG.get());
            }
        }

    }

}
