package com.finderfeed.fdbosses.content.entities.malkuth_boss.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MalkuthCowardEffect extends MobEffect {

    public MalkuthCowardEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xcc1100);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return List.of();
    }
}
