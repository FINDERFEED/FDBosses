package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.EyeOfChesedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EyeOfChesed extends Item {

    public EyeOfChesed() {
        super(new Properties().stacksTo(64).rarity(Rarity.EPIC));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        player.startUsingItem(hand);

        if (level instanceof ServerLevel serverlevel) {
            BlockPos blockpos = serverlevel.findNearestMapStructure(BossUtil.StructureTags.EYE_OF_CHESED_LOCATED, player.blockPosition(), 100, false);
            if (blockpos != null) {
                EyeOfChesedEntity eyeofender = new EyeOfChesedEntity(level, player.getX(), player.getY(0.5), player.getZ());
                eyeofender.setItem(itemstack);
                eyeofender.signalTo(blockpos);
                level.gameEvent(GameEvent.PROJECTILE_SHOOT, eyeofender.position(), GameEvent.Context.of(player));
                level.addFreshEntity(eyeofender);

                float f = Mth.lerp(level.random.nextFloat(), 0.33F, 0.5F);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, f);
                itemstack.consume(1, player);
                player.awardStat(Stats.ITEM_USED.get(this));
                player.swing(hand, true);
                return InteractionResultHolder.success(itemstack);
            }
        }
        return InteractionResultHolder.consume(itemstack);

    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

}
