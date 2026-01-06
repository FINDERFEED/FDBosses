package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.AnimatedItem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDItemAnimationHandler;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.animated_item.AnimatedItemStackContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DivineGearItem extends Item implements AnimatedItem {

    public DivineGearItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        var player = ctx.getPlayer();
        if (player instanceof ServerPlayer serverPlayer){

            Direction direction = ctx.getClickedFace();
            var item = ctx.getItemInHand();
            var data = getComponent(item);
            int charges = data.getCharge();

            if (charges > 0 || player.isCreative()) {
                BlockPos pos = ctx.getClickedPos();
                if (canPlaceOn(ctx.getLevel(), pos, direction)) {
                    Vec3 place = getSpawnPlace(pos, direction);

                    DivineGear.summon(player, place);
                    if (!player.isCreative()) {
                        data.setCooldown();
                        data.setCharge(charges - 1);
                    }
                    item.set(BossDataComponents.DIVINE_GEAR_COMPONENT.get(), new DivineGearComponent(data));
                    return InteractionResult.SUCCESS;
                }else{
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.SUCCESS;

        }

        return super.useOn(ctx);
    }

    public static Vec3 getSpawnPlace(BlockPos pos, Direction direction){
        Vec3 place;

        if (direction == Direction.UP){
            place = pos.getCenter().add(0,0.5,0);
        }else if (Direction.Plane.HORIZONTAL.test(direction)){
            double x = direction.getStepX();
            double z = direction.getStepZ();
            place = pos.getCenter().add(x * 2, -1.5, z * 2);
        }else{
            place = pos.getCenter().add(0,-3.5,0);
        }

        return place;
    }

    public static boolean canPlaceOn(Level level, BlockPos pos, Direction direction){

        BlockPos centerPos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
        var center = centerPos.getCenter();
        AABB checkAABB = new AABB(center.x - 1.5, center.y - 1.5, center.z - 1.5, center.x + 1.5, center.y + 1.5, center.z + 1.5).inflate(0.2);



        return level.getBlockState(pos.offset(direction.getNormal())).getCollisionShape(level,pos).isEmpty() &&

                level.getBlockState(centerPos).getCollisionShape(level,pos).isEmpty() &&

                level.getEntitiesOfClass(DivineGear.class, checkAABB).isEmpty();
    }

    public static DivineGearComponent getComponent(ItemStack itemStack){
        if (!itemStack.has(BossDataComponents.DIVINE_GEAR_COMPONENT)) {
            itemStack.set(BossDataComponents.DIVINE_GEAR_COMPONENT, new DivineGearComponent());
        }
        return itemStack.get(BossDataComponents.DIVINE_GEAR_COMPONENT);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
        if (entity instanceof ServerPlayer serverPlayer){
            var data = getComponent(stack);
            int charges = data.getCharge();

            if (charges < BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearCharges){
                int cooldown = data.getCooldown();
                if (cooldown > 0){
                    data.setCooldown(data.getCooldown() - 1);
                }else{
                    data.setCharge(charges + 1);
                    data.setCooldown();
                }

                stack.set(BossDataComponents.DIVINE_GEAR_COMPONENT, new DivineGearComponent(data));
            }

        }else{

        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void animatedItemTick(AnimatedItemStackContext ctx) {
        var animSystem = FDItemAnimationHandler.getItemAnimationSystem(ctx);
        if (animSystem != null){
            animSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.DIVINE_GEAR_ITEM_IDLE)
                    .build());
        }
    }

}
