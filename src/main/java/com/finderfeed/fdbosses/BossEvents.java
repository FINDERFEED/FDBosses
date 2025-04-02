package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.entities.chesed_sword_buff.FlyingSwordEntity;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossEffects;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BossEvents {

    @SubscribeEvent
    public static void onEntityAttack(AttackEntityEvent event){
        Player player = event.getEntity();
        ItemStack itemStack = player.getMainHandItem();
        if (!player.level().isClientSide && event.getTarget() instanceof LivingEntity livingEntity && itemStack.has(BossDataComponents.ITEM_CORE)){

            Item item = itemStack.getItem();
            float power = player.getAttackStrengthScale(0.5f);
            ItemCoreDataComponent core = itemStack.get(BossDataComponents.ITEM_CORE);

            if (power > 0.5f && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE) && core.getCoreType() == ItemCoreDataComponent.CoreType.LIGHTNING) {

                float p = Mth.clamp(BossConfigs.BOSS_CONFIG.get().itemConfig.chanceToSummonFlyingSword / 100,0,1);
                RandomSource randomSource = livingEntity.getRandom();
                float n = randomSource.nextFloat();
                if (n < p) {
                    FlyingSwordEntity.summonAtTarget(player, livingEntity, player.getMainHandItem());
                }

            }

        }

    }

    @SubscribeEvent
    public static void livingDamageEvent(LivingIncomingDamageEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level().isClientSide){
            DamageSource damageSource = event.getSource();
            if (damageSource.getEntity() instanceof LivingEntity damager && damager.hasEffect(BossEffects.SHOCKED)){
                float amount = event.getAmount();

                float p = Mth.clamp(1 - BossConfigs.BOSS_CONFIG.get().effectConfig.shockDamageReductionPercent / 100f,0,1);

                event.setAmount(amount * p);
            }
        }
    }

}
