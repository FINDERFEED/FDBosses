package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.client.netzach_charging_up.NetzachChargingUpEffect;
import com.finderfeed.fdbosses.client.netzach_charging_up.NetzachChargingUpEffectHandler;
import com.finderfeed.fdbosses.content.entities.netzach.netzach_cane_attack.NetzachCaneVisualStrike;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DebugStick extends Item {

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (level.isClientSide){
//            Vec3 position = player.getEyePosition().add(player.getLookAngle().scale(5));
//            NetzachChargingUpEffectHandler.setNetzachChargingUpEffect(new NetzachChargingUpEffect(position, 6f, 5f, 10, 400, 20));
        }else{
            NetzachCaneVisualStrike.summon(level, player.getEyePosition(), player.getLookAngle(), 10, 10, 10);
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }


}
