package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.BossClientPackets;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class QliphothicCodex extends Item {

    public QliphothicCodex(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand p_41434_) {
        if (level.isClientSide()){
            BossClientPackets.openQliphoticCodex();
        }
        return super.use(level, player, p_41434_);
    }
}
