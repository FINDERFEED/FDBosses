package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.*;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.animated_item.AnimatedItemStackContext;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class PhaseSphere extends Item implements AnimatedItem {

    public PhaseSphere(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            if (PhaseSphereHandler.canContinueUsingChesedItem(player)) {
                FDServerItemAnimations.startItemAnimation(player, "USE", AnimationTicker.builder(BossAnims.CHESED_ITEM_USE)
                        .setToNullTransitionTime(0)
                        .build(), hand);
                player.startUsingItem(hand);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1f, 0.9f);
                return InteractionResultHolder.consume(player.getItemInHand(hand));
            }else{
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 72000;
    }

    @Override
    public void onUseTick(Level p_41428_, LivingEntity entity, ItemStack p_41430_, int p_41431_) {

        super.onUseTick(p_41428_, entity, p_41430_, p_41431_);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        if (!entity.level().isClientSide){
            entity.level().playSound(null, entity.getX(),entity.getY(),entity.getZ(), SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.PLAYERS, 1f, 0.75f);
        }
    }


    @Override
    public void inventoryTick(ItemStack stack, Level p_41405_, Entity entity, int slot, boolean p_41408_) {
        super.inventoryTick(stack, p_41405_, entity, slot, p_41408_);
//        if (entity instanceof ServerPlayer serverPlayer){
//            serverPlayer.getInventory().setItem(slot, ItemStack.EMPTY);
//            serverPlayer.sendSystemMessage(Component.translatable("fdbosses.word.item_is_not_released_yet").withStyle(ChatFormatting.RED));
//        }
    }

    @Override
    public void animatedItemTick(AnimatedItemStackContext ctx) {
        var animSystem = FDItemAnimationHandler.getItemAnimationSystem(ctx);
        if (animSystem != null) {
            animSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_ITEM_IDLE.get())
                    .build());
        }
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Level p_339594_, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_339594_, components, p_41424_);
        components.add(Component.translatable("fdbosses.word.phase_sphere_description").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(FDModelItemRenderer.createExtensions(FDModelItemRendererOptions.create()
                .addModel(BossModels.CHESED_ITEM, RenderType.entityCutoutNoCull(FDBosses.location("textures/item/chesed_item.png")))
                .addModel(FDItemModelOptions.builder()
                        .modelInfo(BossModels.CHESED_ITEM)
                        .renderType(RenderType.eyes(FDBosses.location("textures/item/chesed_item_emissive.png")))
                        .itemColor(((itemDisplayContext, itemStack) -> {

                            Player player = FDClientHelpers.getClientPlayer();
                            if (player.getUseItem().equals(itemStack)) {
                                float pticks = FDRenderUtil.tryGetPartialTickIgnorePause();
                                int useTime = PhaseSphereHandler.clientsideChesedItemUseTick;
                                if (useTime != -1) {
                                    float maxTime = BossConfigs.BOSS_CONFIG.get().itemConfig.phaseSphereUseDuration;
                                    float time = Mth.clamp(useTime + pticks, 0, maxTime);
                                    float p = time / maxTime;
                                    p = FDEasings.easeOut(p);
                                    return new FDColor(1f, 1 - p, 1 - p, 1f);
                                }
                            }

                            return new FDColor(1f,1f,1f,1f);
                        }))
                        .build())
                .setScale((itemDisplayContext -> {
                    return 0.75f;
                }))
                .addRotation((itemDisplayContext -> {
                    return 0f;
                }))
                .addTranslation((itemDisplayContext -> {
                    return new Vector3f();
                }))
        ));
    }
}
