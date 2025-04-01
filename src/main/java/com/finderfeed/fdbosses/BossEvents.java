package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.entities.chesed_sword_buff.FlyingSwordEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BossEvents {

    @SubscribeEvent
    public static void onEntityAttack(AttackEntityEvent event){
        Player player = event.getEntity();
        if (!player.level().isClientSide && event.getTarget() instanceof LivingEntity livingEntity){
            FlyingSwordEntity.summonAtTarget(player, player, player.getMainHandItem());
        }

    }

}
