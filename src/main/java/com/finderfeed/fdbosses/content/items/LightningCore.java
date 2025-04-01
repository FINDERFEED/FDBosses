package com.finderfeed.fdbosses.content.items;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LightningCore extends Item {

    public LightningCore(Properties props) {
        super(props);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack p_150892_, ItemStack p_150893_, Slot p_150894_, ClickAction p_150895_, Player player, SlotAccess p_150897_) {
        System.out.println(player.level());
        return super.overrideOtherStackedOnMe(p_150892_, p_150893_, p_150894_, p_150895_, player, p_150897_);
    }
}
