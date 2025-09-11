package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.entities.IEffectImmune;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_mini_ray.ChesedMiniRay;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthWeaknessHandler;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.packets.SetClientMalkuthWeaknessAmountPacket;
import com.finderfeed.fdbosses.content.projectiles.MalkuthPlayerFireIceBall;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BossEvents {

    public static final TagKey<MobEffect> NOT_CURABLE_EFFECTS = TagKey.create(Registries.MOB_EFFECT, FDBosses.location("not_curable"));

    @SubscribeEvent
    public static void effectApplicable(MobEffectEvent.Applicable event){
        if (event.getEntity() instanceof IEffectImmune){
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public static void preventRemovingEffects(MobEffectEvent.Remove event){
        var effect = event.getEffect();
        if (effect.is(NOT_CURABLE_EFFECTS) && event.getCure() != null){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void playerEnterWorld(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new SetClientMalkuthWeaknessAmountPacket(MalkuthWeaknessHandler.getCurrentWeaknessLevel(serverPlayer)));
        }
    }

    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new SetClientMalkuthWeaknessAmountPacket(MalkuthWeaknessHandler.getCurrentWeaknessLevel(serverPlayer)));
        }
    }

    @SubscribeEvent
    public static void preventArenaDestruction(BlockEvent.BreakEvent event){
        LevelAccessor level = event.getLevel();
        if (!level.isClientSide()){
            BlockPos pos = event.getPos();
            var spawners = level.getEntitiesOfClass(BossSpawnerEntity.class, new AABB(-100,-100,-100,100,100,100).move(pos.getCenter()));

            if (!spawners.isEmpty()){

                BlockState state = level.getBlockState(pos);
                boolean v = checkBlockStateAllowedToBreakInArena(state);
                if (v){
                    return;
                }
            }


            Component message = null;
            for (BossSpawnerEntity bossSpawnerEntity : spawners){
                if (!bossSpawnerEntity.canInteractWithBlockPos(pos)){
                    message = bossSpawnerEntity.onArenaDestructionMessage();
                    event.setCanceled(true);
                    break;
                }
            }
            if (event.isCanceled()){
                if (event.getPlayer() != null && message != null){
                    event.getPlayer().sendSystemMessage(message);
                }
            }
        }
    }

    public static boolean checkBlockStateAllowedToBreakInArena(BlockState blockState){

        List<Pattern> patterns = BossConfigs.BOSS_CONFIG.get().blocksAllowedToBreakInArenaPatterns;

        for (Pattern pattern : patterns){
            ResourceLocation location = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
            String string = location.toString();
            var test = pattern.asPredicate().test(string);
            if (test){
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void preventArenaDestruction(BlockEvent.EntityPlaceEvent event){
        LevelAccessor level = event.getLevel();
        if (!level.isClientSide()){
            BlockPos pos = event.getPos();
            var spawners = level.getEntitiesOfClass(BossSpawnerEntity.class, new AABB(-100,-100,-100,100,100,100).move(pos.getCenter()));
            Component message = null;
            for (BossSpawnerEntity bossSpawnerEntity : spawners){
                if (!bossSpawnerEntity.canInteractWithBlockPos(pos)){
                    message = bossSpawnerEntity.onArenaDestructionMessage();
                    event.setCanceled(true);
                    break;
                }
            }
            if (event.isCanceled()){
                if (event.getEntity() instanceof Player player && message != null){
                    player.sendSystemMessage(message);
                }
            }
        }
    }

    @SubscribeEvent
    public static void preventArenaDestruction(ExplosionEvent.Detonate event){
        Level level = event.getLevel();
        if (!level.isClientSide()){


            var list = event.getAffectedBlocks();

            Iterator<BlockPos> blockPosIterator = list.listIterator();

            var spawners = level.getEntitiesOfClass(BossSpawnerEntity.class, new AABB(-100, -100, -100, 100, 100, 100).move(event.getExplosion().center()));

            Component message = null;

            while (blockPosIterator.hasNext()) {

                BlockPos pos = blockPosIterator.next();
                for (BossSpawnerEntity bossSpawnerEntity : spawners) {
                    if (!bossSpawnerEntity.canInteractWithBlockPos(pos)) {
                        message = bossSpawnerEntity.onArenaDestructionMessage();
                        blockPosIterator.remove();
                        break;
                    }
                }
            }

            if (message != null && event.getExplosion().getIndirectSourceEntity() instanceof Player player){
                player.sendSystemMessage(message);
            }

        }
    }

    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event){
        if (event.getEntity() instanceof Player player && !player.level().isClientSide && event.getSource() != null){
            DamageSource source = event.getSource();
            if (source.is(BossDamageSources.CHESED_LOR_EASTER_EGG_ATTACK)){

                ItemStack itemStack = Items.BOOK.getDefaultInstance();

                itemStack.set(DataComponents.ITEM_NAME,player.getName());

                ItemEntity item = new ItemEntity(player.level(),player.getX(),player.getY(),player.getZ(),itemStack);

                player.level().addFreshEntity(item);

            }
        }
    }

    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event){
        ItemStack itemStack = event.getItemStack();
        Player player = event.getEntity();
        Level level = player.level();
        if (!level.isClientSide && itemStack.has(BossDataComponents.ITEM_CORE)){

            var itemCore = itemStack.get(BossDataComponents.ITEM_CORE).getCoreType();

            if (itemCore == ItemCoreDataComponent.CoreType.FIRE_AND_ICE){

                Vec3 look = player.getLookAngle();
                float speed = 4f;
                Vec3 speedVec = look.multiply(speed,speed,speed);
                MalkuthAttackType attackType = player.isCrouching() ? MalkuthAttackType.ICE : MalkuthAttackType.FIRE;
                MalkuthPlayerFireIceBall.summon(player, player.getEyePosition().add(0,-0.25f,0).add(look), speedVec, attackType, itemStack);

                if (!player.isCreative()) {
                    int cooldown = BossConfigs.BOSS_CONFIG.get().itemConfig.playerMalkuthFireballAbilityCooldown;
                    player.getCooldowns().addCooldown(itemStack.getItem(), cooldown);
                }

                player.swing(event.getHand(), true);

            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttack(AttackEntityEvent event){
        Player player = event.getEntity();
        ItemStack itemStack = player.getMainHandItem();
        if (!player.level().isClientSide && event.getTarget() instanceof LivingEntity livingEntity && itemStack.has(BossDataComponents.ITEM_CORE)){

            Item item = itemStack.getItem();
            float power = player.getAttackStrengthScale(0.5f);
            ItemCoreDataComponent core = itemStack.get(BossDataComponents.ITEM_CORE);

            if (power > 0.5f && core.getCoreType() == ItemCoreDataComponent.CoreType.LIGHTNING && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)) {

                float p = Mth.clamp(BossConfigs.BOSS_CONFIG.get().itemConfig.chanceToSummonLightningStrike / 100,0,1);
                RandomSource randomSource = livingEntity.getRandom();
                float n = randomSource.nextFloat();
                if (n < p) {
                    ChesedMiniRay.summon(player.level(), livingEntity, player.getMainHandItem(), player);
                }

            }

        }

    }

    @SubscribeEvent
    public static void deathEvent(LivingDropsEvent event){
        var entity = event.getEntity();
        if (entity instanceof ServerPlayer serverPlayer){
            Level level = serverPlayer.level();
            Vec3 pos = serverPlayer.position();
            float radius = 50;
            var spawners = level.getEntitiesOfClass(BossSpawnerEntity.class, new AABB(-radius,-radius,-radius,radius,radius,radius).move(pos), bossSpawner -> !bossSpawner.isActive());

            for (var spawner : spawners){

                Vec3 itemsPos = spawner.getPlayerItemsDropPosition(pos);

                if (itemsPos != null){
                    for (ItemEntity itemEntity : event.getDrops()){
                        itemEntity.setPos(itemsPos);
                        itemEntity.setDeltaMovement(Vec3.ZERO);
                    }
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
