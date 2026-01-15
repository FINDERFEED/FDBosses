package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDServerItemAnimations;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FDBosses.MOD_ID)
public class PhaseSphereHandler {

    public static final String CHESED_ITEM_DATANAME = "phase_sphere_data";
    public static final String CHESED_ITEM_USE_TIME = "phase_sphere_time_data";

    public static boolean isUsingChesedItem = false;
    public static int clientsideChesedItemUseTick = -1;

    public static void onChesedItemUse(Player player){
        if (!player.isPassenger()) {

            if (player instanceof ServerPlayer serverPlayer) {
                var item = player.getUseItem();
                if (item.is(BossItems.PHASE_SPHERE.get()) && canContinueUsingChesedItem(player)) {
                    chesedItemServerUseTick(player);
                } else {
                    stopUsingChesedItem(player, CHESED_ITEM_DATANAME);
                }
            }else{
                if (isUsingChesedItem){
                    Abilities abilities = player.getAbilities();
                    abilities.setFlyingSpeed(0.15f);
                    abilities.mayfly = true;
                    abilities.instabuild = false;
                    abilities.flying = true;
                    player.noPhysics = true;
                    player.setOnGround(false);
                    clientsideChesedItemUseTick = Mth.clamp(clientsideChesedItemUseTick + 1,0, BossConfigs.BOSS_CONFIG.get().itemConfig.phaseSphereUseDuration);
                }else{
                    clientsideChesedItemUseTick = -1;
                }
            }


        }
    }

    private static void chesedItemServerUseTick(Player player){
        var perdata = player.getPersistentData();

        boolean sendPacket = false;

        if (!perdata.contains(CHESED_ITEM_DATANAME)) {
            CompoundTag tag = new CompoundTag();
            player.getAbilities().addSaveData(tag);
            tag.putInt(CHESED_ITEM_USE_TIME, 0);
            perdata.put(CHESED_ITEM_DATANAME, tag);
            sendPacket = true;
        }

        Abilities abilities = player.getAbilities();
        abilities.setFlyingSpeed(0.15f);
        abilities.mayfly = true;
        abilities.instabuild = false;
        abilities.flying = true;
        player.noPhysics = true;
        player.setOnGround(false);


        if (sendPacket){
            PacketDistributor.sendToPlayer((ServerPlayer) player, new PhaseSpherePacket(player, true));
        }

        var compound = perdata.getCompound(CHESED_ITEM_DATANAME);
        int time = compound.getInt(CHESED_ITEM_USE_TIME);
        compound.putInt(CHESED_ITEM_USE_TIME, time + 1);
    }

    private static int getServerChesedItemUsedTime(Player player){
        var data = player.getPersistentData();
        if (data.contains(CHESED_ITEM_DATANAME)){
            var tag = data.getCompound(CHESED_ITEM_DATANAME);
            return tag.getInt(CHESED_ITEM_USE_TIME);
        }else{
            return -1;
        }
    }

    private static void stopUsingChesedItem(Player player, String dataname){
        var perdata = player.getPersistentData();
        if (perdata.contains(CHESED_ITEM_DATANAME)) {
            if (player instanceof ServerPlayer serverPlayer) {
                CompoundTag tag = perdata.getCompound(dataname);
                player.getAbilities().loadSaveData(tag);
                player.noPhysics = false;
                PacketDistributor.sendToPlayer(serverPlayer, new PhaseSpherePacket(player, false));
                FDServerItemAnimations.startItemAnimation(player, "STOP_USE", AnimationTicker.builder(BossAnims.CHESED_ITEM_USE)
                        .setToNullTransitionTime(0)
                        .reversed()
                        .build(), player.getUsedItemHand());

                player.stopUsingItem();
                player.getCooldowns().addCooldown(BossItems.PHASE_SPHERE.get(), BossConfigs.BOSS_CONFIG.get().itemConfig.phaseSphereCooldown);
            }
            perdata.remove(dataname);
        }
    }

    public static boolean canContinueUsingChesedItem(Player player){

        int useTime = getServerChesedItemUsedTime(player);

        if (useTime > BossConfigs.BOSS_CONFIG.get().itemConfig.phaseSphereUseDuration){
            return false;
        }

        BlockPos pos = player.getOnPos().above();
        Level level = player.level();

        int nonAirBlocks = 0;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos p = pos.offset(x, y, z);
                    BlockState blockState = level.getBlockState(p);
                    if (!blockState.isAir() && !blockState.getCollisionShape(level, p).isEmpty()) {
                        nonAirBlocks++;
                    }

                }
            }
        }

        return useTime > 20 ? nonAirBlocks >= 10 : nonAirBlocks > 0;
    }

    @SubscribeEvent
    public static void deathEvent(PlayerEvent.Clone event){
        Player original = event.getOriginal();
        Player copy = event.getEntity();
        var data = original.getPersistentData();
        if (data.contains(CHESED_ITEM_DATANAME)) {
            copy.getPersistentData().put(CHESED_ITEM_DATANAME, data.getCompound(CHESED_ITEM_DATANAME));
        }
    }

}
