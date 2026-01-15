package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.*;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.animated_item.AnimatedItemStackContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class DivineGearItem extends Item implements AnimatedItem {


    public static final ResourceLocation DIVINE_GEAR = FDBosses.location("textures/entities/geburah/divine_gear.png");



    public DivineGearItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        var player = ctx.getPlayer();
        if (player instanceof ServerPlayer serverPlayer && ctx.getHand() == InteractionHand.MAIN_HAND){

            Direction direction = ctx.getClickedFace();
            var item = ctx.getItemInHand();
            int charges = getCharge(item);

            if (charges > 0 || player.isCreative()) {
                BlockPos pos = ctx.getClickedPos();

                var posAndDir = getPosAndDirection(ctx.getLevel(), pos, direction);

                pos = posAndDir.first;
                direction = posAndDir.second;

                if (canPlaceOn(ctx.getLevel(), pos, direction)) {
                    Vec3 place = getSpawnPlace(pos, direction);

                    DivineGear.summon(player, place);
                    if (!player.isCreative()) {
                        setOnCooldown(item);
                        setCharge(item, charges - 1);
                    }
                    return InteractionResult.SUCCESS;
                }else{
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.SUCCESS;

        }

        return super.useOn(ctx);
    }

    public static Pair<BlockPos, Direction> getPosAndDirection(Level level, BlockPos clickedPos, Direction direction){
        var state = level.getBlockState(clickedPos);
        if (state.getCollisionShape(level,clickedPos).isEmpty()){
            for (var dir : Direction.values()){
                BlockPos offs = clickedPos.offset(dir.getNormal());
                if (level.getBlockState(offs).getCollisionShape(level,offs).isEmpty()){
                    return new Pair<>(clickedPos.offset(dir.getOpposite().getNormal()), dir);
                }
            }
            return new Pair<>(clickedPos.below(), direction);
        }else{
            return new Pair<>(clickedPos, direction);
        }
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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
        if (entity instanceof ServerPlayer serverPlayer){

            int charges = getCharge(stack);

            if (charges < BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearCharges){
                int cooldown = getCooldown(stack);
                if (cooldown > 0){
                    setCooldown(stack, cooldown - 1);
                }else{
                    setOnCooldown(stack);
                    setCharge(stack, charges + 1);
                }
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

    @Override
    public void appendHoverText(ItemStack p_41421_, Level level, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, level, components, p_41424_);
        components.add(Component.translatable("fdbosses.word.divine_gear_description").withStyle(ChatFormatting.GOLD));
    }

    public static CompoundTag getData(ItemStack itemStack){

        var tag = itemStack.getOrCreateTagElement("divineGearData");
        if (!tag.contains("charge")){
            tag.putInt("charge", BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearCharges);
        }

        return tag;
    }

    public static int getCooldown(ItemStack itemStack){
        var tag = getData(itemStack);
        return tag.getInt("cooldown");
    }

    public static int getCharge(ItemStack itemStack){
        var tag = getData(itemStack);
        return tag.getInt("charge");
    }

    public static void setCooldown(ItemStack itemStack, int cooldown){
        var tag = getData(itemStack);
        tag.putInt("cooldown", cooldown);
    }

    public static void setCharge(ItemStack itemStack, int charge){
        var tag = getData(itemStack);
        tag.putInt("charge", charge);
    }

    public static void setOnCooldown(ItemStack itemStack){
        setCooldown(itemStack, BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearChargeReplenishTime);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(FDModelItemRenderer.createExtensions(FDModelItemRendererOptions.create()
                .addModel(FDItemModelOptions.builder()
                        .modelInfo(BossModels.DIVINE_GEAR)
                        .renderType((ctx,item)->{
                            return RenderType.entityCutout(DIVINE_GEAR);
                        })
                        .build())

                .setScale((ctx -> {
                    return 0.25f;
                }))
                .addRotation3((itemDisplayContext -> {
                    return new Vector3f();
                }))
                .addTranslation((itemDisplayContext -> {
                    return new Vector3f(0,-0.1f,0);
                }))
                .freeRender(new DivineGearItemRenderer())
        ));
    }
}
