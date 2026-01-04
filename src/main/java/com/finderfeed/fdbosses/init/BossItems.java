package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.entities.geburah.ExplosiveCrystalItem;
import com.finderfeed.fdbosses.content.items.ArmorCoreItem;
import com.finderfeed.fdbosses.content.items.LocatorEye;
import com.finderfeed.fdbosses.content.items.CoreItem;
import com.finderfeed.fdbosses.content.items.WeaponCoreItem;
import com.finderfeed.fdbosses.content.items.chesed.PhaseSphere;
import com.finderfeed.fdbosses.content.items.malkuth.MalkuthFist;
import com.finderfeed.fdbosses.debug.DebugStick;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    public static final Supplier<CoreItem> LIGHTNING_CORE = ITEMS.register("lightning_core",()->new WeaponCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.LIGHTNING,false,
            ()->Component.translatable("fdbosses.core_ability.lightning_core").withStyle(ChatFormatting.AQUA)));
    public static final Supplier<CoreItem> FIRE_AND_ICE_CORE = ITEMS.register("fire_and_ice_core",()->new WeaponCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.FIRE_AND_ICE, false,
            ()->Component.translatable("fdbosses.core_ability.fire_and_ice_core").withStyle(ChatFormatting.GOLD)));
    public static final Supplier<CoreItem> JUSTICE_CORE = ITEMS.register("justice_core",()->new ArmorCoreItem(new Item.Properties().stacksTo(1), ItemCoreDataComponent.CoreType.JUSTICE_CORE, false,
            ()->Component.translatable("fdbosses.core_ability.justice_core", BossConfigs.BOSS_CONFIG.get().itemConfig.justiceCoreDamageReduction).withStyle(ChatFormatting.AQUA)));

    public static final Supplier<LocatorEye<?>> EYE_OF_CHESED = ITEMS.register("eye_of_chesed", ()->new LocatorEye<>(BossUtil.StructureTags.EYE_OF_CHESED_LOCATED, BossEntities.EYE_OF_CHESED));
    public static final Supplier<LocatorEye<?>> EYE_OF_MALKUTH = ITEMS.register("eye_of_malkuth", ()->new LocatorEye<>(BossUtil.StructureTags.EYE_OF_MALKUTH_LOCATED, BossEntities.EYE_OF_MALKUTH));
    public static final Supplier<LocatorEye<?>> EYE_OF_GEBURAH = ITEMS.register("eye_of_geburah", ()->new LocatorEye<>(BossUtil.StructureTags.EYE_OF_GEBURAH_LOCATED, BossEntities.EYE_OF_GEBURAH));

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

    public static final Supplier<Item> GEBURAH_TROPHY = ITEMS.register("geburah_trophy",
            ()->new BlockItem(BossBlocks.GEBURAH_TROPHY.get(), new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> JUSTICESTONE_BRICKS = ITEMS.register("justicestone_bricks",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_BRICKS.get(), new Item.Properties()));

    public static final Supplier<Item> CHISELED_JUSTICESTONE_BRICKS = ITEMS.register("chiseled_justicestone_bricks",
            ()->new BlockItem(BossBlocks.CHISELED_JUSTICESTONE_BRICKS.get(), new Item.Properties()));

    public static final Supplier<Item> JUSTICESTONE_BEAM = ITEMS.register("justicestone_beam",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_BEAM.get(), new Item.Properties()));

    public static final Supplier<Item> JUSTICESTONE_COLUMN = ITEMS.register("justicestone_column",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_COLUMN.get(), new Item.Properties()));

    public static final Supplier<Item> JUSTICESTONE_WALL = ITEMS.register("justicestone_wall",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_WALL.get(), new Item.Properties()));

    public static final Supplier<Item> POLISHED_JUSTICESTONE = ITEMS.register("polished_justicestone",
            ()->new BlockItem(BossBlocks.POLISHED_JUSTICESTONE.get(), new Item.Properties()));

    public static final Supplier<Item> JUSTICESTONE_COLUMN_WALL = ITEMS.register("justicestone_column_wall",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_COLUMN_WALL.get(), new Item.Properties()));

    public static final Supplier<Item> JUSTICESTONE_STAIRS = ITEMS.register("justicestone_stairs",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_STAIRS.get(), new Item.Properties()));

    public static final Supplier<Item> POLISHED_JUSTICESTONE_STAIRS = ITEMS.register("polished_justicestone_stairs",
            ()->new BlockItem(BossBlocks.POLISHED_JUSTICESTONE_STAIRS.get(), new Item.Properties()));

    public static final Supplier<Item> POLISHED_JUSTICESTONE_SLAB = ITEMS.register("polished_justicestone_slab",
            ()->new BlockItem(BossBlocks.POLISHED_JUSTICESTONE_SLAB.get(), new Item.Properties()));

    public static final Supplier<Item> JUSTICESTONE_BRICKS_SLAB = ITEMS.register("justicestone_bricks_slab",
            ()->new BlockItem(BossBlocks.JUSTICESTONE_BRICKS_SLAB.get(), new Item.Properties()));

    public static final Supplier<Item> GEBURAH_RESPITE_POINT = ITEMS.register("geburah_respite_point",
            ()->new BlockItem(BossBlocks.GEBURAH_RESPAWN_POINT_SETTER.get(), new Item.Properties()));

    public static final Supplier<Item> GEBURAH_EXPLOSIVE_CRYSTAL = ITEMS.register("geburah_explosive_crystal",
            ()->new ExplosiveCrystalItem(new Item.Properties()));

    public static final Supplier<Item> PHASE_SPHERE = ITEMS.register("phase_sphere",
            ()->new PhaseSphere(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> MALKUTH_FIST = ITEMS.register("malkuth_fist",
            ()->new MalkuthFist(new Item.Properties().stacksTo(1)));



    @EventBusSubscriber(modid = FDBosses.MOD_ID)
    public static class AddToCreativeTabs{

        @SubscribeEvent
        public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event){
            if (event.getTab().equals(BossCreativeTabs.MAIN.get())){
                event.accept(GEBURAH_EXPLOSIVE_CRYSTAL.get());
                event.accept(LIGHTNING_CORE.get());
                event.accept(FIRE_AND_ICE_CORE.get());
                event.accept(JUSTICE_CORE.get());
                event.accept(EYE_OF_CHESED.get());
                event.accept(EYE_OF_MALKUTH.get());
                event.accept(EYE_OF_GEBURAH.get());
                event.accept(CHESED_TROPHY.get());
                event.accept(PHASE_SPHERE.get());
                event.accept(MALKUTH_TROPHY.get());
                event.accept(MALKUTH_FIST.get());
                event.accept(GEBURAH_TROPHY.get());
                event.accept(JUSTICESTONE_BRICKS.get());
                event.accept(CHISELED_JUSTICESTONE_BRICKS.get());
                event.accept(JUSTICESTONE_BEAM.get());
                event.accept(JUSTICESTONE_COLUMN.get());
                event.accept(JUSTICESTONE_WALL.get());
                event.accept(JUSTICESTONE_COLUMN_WALL.get());
                event.accept(JUSTICESTONE_STAIRS.get());
                event.accept(POLISHED_JUSTICESTONE.get());
                event.accept(POLISHED_JUSTICESTONE_STAIRS.get());
                event.accept(POLISHED_JUSTICESTONE_SLAB.get());
                event.accept(JUSTICESTONE_BRICKS_SLAB.get());
                event.accept(GEBURAH_RESPITE_POINT.get());
            }else if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)){
                event.accept(MALKUTH_FIRE_WARRIOR_SPAWN_EGG.get());
                event.accept(MALKUTH_ICE_WARRIOR_SPAWN_EGG.get());
            }
        }

    }

}
