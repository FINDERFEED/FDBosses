package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.debug.DebugStick;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(FDBosses.MOD_ID);

    public static final Supplier<Item> DEBUG_STICK = ITEMS.register("debug",()->new DebugStick(new Item.Properties()));

}
