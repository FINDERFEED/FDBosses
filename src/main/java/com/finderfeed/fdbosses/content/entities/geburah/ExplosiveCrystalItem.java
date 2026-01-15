package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.geburah.sins.PlayerSinsHandler;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExplosiveCrystalItem extends Item {

    public ExplosiveCrystalItem(Properties props) {
        super(props.stacksTo(1).durability(450));
    }

    @Override
    public void inventoryTick(ItemStack item, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(item, level, entity, p_41407_, p_41408_);

        if (!level.isClientSide){
            item.setDamageValue(item.getDamageValue() + 1);
            if (item.getDamageValue() >= item.getMaxDamage()){
                if (entity instanceof ServerPlayer player) {
                    PlayerSinsHandler.sin(player, 20);
                }
                item.setCount(0);
            }
        }

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide){
            var item = entity.getItem();
            item.setDamageValue(item.getDamageValue() + 1);
            entity.setItem(item.copy());
            if (item.getDamageValue() >= item.getMaxDamage()){

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .size(0.25f)
                        .scalingOptions(0,0,10)
                        .color(0.3f,0.7f,1f)
                        .friction(0.7f)
                        .build();

                ((ServerLevel)entity.level()).sendParticles(ballParticleOptions, entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ(),70,0.1f,0.1f,0.1f,0.4f);

                entity.discard();
            }
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Level p_339594_, List<Component> text, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_339594_, text, p_41424_);

    }
}
