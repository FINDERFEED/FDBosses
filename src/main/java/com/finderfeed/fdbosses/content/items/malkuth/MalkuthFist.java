package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.FDLibCalls;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MalkuthFist extends Item {

    public MalkuthFist(Properties properties) {
        super(properties.component(BossDataComponents.MALKUTH_FIST_COMPONENT, new MalkuthFistDataComponent()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide){

            ItemStack item = player.getItemInHand(hand);

            if (hand == InteractionHand.MAIN_HAND){
                var data = item.get(BossDataComponents.MALKUTH_FIST_COMPONENT);

                if (data.canUseChain()){
                    data.setCanUseChain(false);
                    MalkuthFistChain.summon(player, false);
                    player.startUsingItem(hand);
                    item.set(BossDataComponents.MALKUTH_FIST_COMPONENT, data);
                    return InteractionResultHolder.consume(item);
                }else {
                    if (player.onGround()) {

                        Vec3 lookAngle = player.getLookAngle().multiply(1,0,1);
                        if (!lookAngle.equals(Vec3.ZERO)){
                            lookAngle = lookAngle.normalize();
                        }

                        Vec3 speed = new Vec3(
                                lookAngle.x * 0.1f,
                                1.5,
                                lookAngle.z * 0.1f
                        );
                        FDLibCalls.setServerPlayerSpeed((ServerPlayer) player, speed);
                        player.setIgnoreFallDamageFromCurrentImpulse(true);
                        player.currentImpulseImpactPos = player.position();

                        data.setCanUseChain(true);
                        item.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);

                        player.getCooldowns().addCooldown(BossItems.MALKUTH_FIST.get(), 5);

                        return InteractionResultHolder.consume(item);
                    }
                }

                return InteractionResultHolder.fail(item);
            }else{
                MalkuthFistChain.summon(player, true);
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(item);
            }

        }
        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack p_41454_, LivingEntity p_344979_) {
        return 72000;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean p_41408_) {
        super.inventoryTick(stack, level, entity, slot, p_41408_);
        if (entity instanceof ServerPlayer serverPlayer){

            var data = stack.get(BossDataComponents.MALKUTH_FIST_COMPONENT);
            if (data.canUseChain()){
                if (serverPlayer.onGround() && !serverPlayer.getCooldowns().isOnCooldown(stack.getItem())){
                    data.setCanUseChain(false);
                    stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT, data);
                }
            }

            if (data.canSkipCooldown()){
                var cooldowns = serverPlayer.getCooldowns();
                if (cooldowns.isOnCooldown(stack.getItem())){
                    if (slot != Inventory.SLOT_OFFHAND) {
                        data.setCanSkipCooldown(false);
                        stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);
                        cooldowns.removeCooldown(stack.getItem());
                    }
                }else{
                    data.setCanSkipCooldown(false);
                    stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);
                }
            }


        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide){
            var data = stack.get(BossDataComponents.MALKUTH_FIST_COMPONENT);
            data.setCanSkipCooldown(false);
            data.setCanUseChain(false);
            stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}
