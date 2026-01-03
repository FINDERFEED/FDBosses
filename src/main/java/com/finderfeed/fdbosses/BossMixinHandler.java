package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthWeaknessHandler;
import com.finderfeed.fdbosses.content.items.chesed.ChesedItem;
import com.finderfeed.fdbosses.content.items.chesed.ChesedItemPacket;
import com.finderfeed.fdbosses.content.structures.MalkuthArenaStructure;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdbosses.mixin.LivingEntityAccessor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

public class BossMixinHandler {

    public static void onChesedItemUse(Player player){
        if (!player.isPassenger()) {

            var item = player.getUseItem();

            String dataname = "chesed_item_data";

            var perdata = player.getPersistentData();
            if (item.getItem() instanceof ChesedItem && canContinueUsingChesedItem(player)) {
                if (!perdata.contains(dataname)) {
                    CompoundTag tag = new CompoundTag();
                    player.getAbilities().addSaveData(tag);
                    perdata.put(dataname, tag);

                }
                Abilities abilities = player.getAbilities();
                abilities.setFlyingSpeed(0.15f);
                abilities.mayfly = true;
                abilities.instabuild = false;
                abilities.flying = true;
                player.noPhysics = true;
                player.setOnGround(false);
            } else {
                if (perdata.contains(dataname)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        CompoundTag tag = player.getPersistentData().getCompound(dataname);
                        player.getAbilities().loadSaveData(tag);
                        player.noPhysics = false;
                        PacketDistributor.sendToPlayer(serverPlayer, new ChesedItemPacket(player));
                        player.stopUsingItem();
                        player.getCooldowns().addCooldown(BossItems.CHESED_ITEM.get(), 5);
                    }
                    perdata.remove(dataname);
                }
            }
        }
    }

    public static boolean canContinueUsingChesedItem(Player player){

        BlockPos pos = player.getOnPos();
        Level level = player.level();

        for (int x = 0; x < 1; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = 0; z < 1; z++) {
                    BlockPos p = pos.offset(x,y,z);
                    BlockState blockState = level.getBlockState(p);
                    if (!blockState.isAir()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void actuallyHurtPlayerMixin(DamageSource source, Player player){
        LivingEntityAccessor accessor = (LivingEntityAccessor) player;
        var damageContainers = accessor.getDamageContainers();
        DamageContainer damageContainer = damageContainers.peek();
        float newDamage = damageContainer.getNewDamage();

        Level level = player.level();

        if (!level.isClientSide && source instanceof MalkuthDamageSource damageSource) {

            int malkuthDamageAmount = damageSource.getMalkuthAttackAmount();
            MalkuthAttackType malkuthAttackType = damageSource.getMalkuthAttackType();

            if (!MalkuthWeaknessHandler.isWeakTo(player, malkuthAttackType)) {
                float damage = newDamage;
                float reduction = 1 - BossConfigs.BOSS_CONFIG.get().malkuthConfig.nonWeakToDamageReduction / 100f;
               damageContainer.setNewDamage(damage * reduction);
            }

            MalkuthWeaknessHandler.damageWeakness(malkuthAttackType, player, malkuthDamageAmount);

        }

    }

    public static boolean addBeardifiersToStructureManually(StructureManager structureManager, ChunkPos chunkPos, ObjectList<Beardifier.Rigid> beardifiers, StructureStart structureStart){

        Structure structure = structureStart.getStructure();

        if (structure instanceof MalkuthArenaStructure malkuthArenaStructure){

            var bb = structureStart.getBoundingBox();

            var center = bb.getCenter();

            BlockPos startPos = new BlockPos(center.getX(),bb.minY(),center.getZ());

            int centerBBRadius = 60;
            int height = 40;



            BoundingBox centerBB = new BoundingBox(
                    -centerBBRadius,-height,-centerBBRadius,
                    centerBBRadius,-6,centerBBRadius
            ).moved(startPos.getX(),startPos.getY(),startPos.getZ());


            if (boxIntersectsChunk(centerBB, chunkPos, 6)) {
                beardifiers.add(new Beardifier.Rigid(centerBB, TerrainAdjustment.ENCAPSULATE, 0));
            }

            int otherBoxRadius = 13;

            addMalkuthArenaBeardifiersInRadius(chunkPos, startPos, structureManager, beardifiers, centerBBRadius + otherBoxRadius - 1, otherBoxRadius, 6, 32, height);

            return true;
        }

        return false;
    }

    private static boolean boxIntersectsChunk(BoundingBox box, ChunkPos chunkPos, int bigger){
        return box.intersects(chunkPos.getMinBlockX() - bigger, chunkPos.getMinBlockZ() - bigger, chunkPos.getMaxBlockX() + bigger, chunkPos.getMaxBlockZ() + bigger);
    }

    private static void addMalkuthArenaBeardifiersInRadius(ChunkPos chunkPos, BlockPos startPos, StructureManager structureManager, ObjectList<Beardifier.Rigid> beardifiers, int startRadius, int boxRadius, int verticalOffset, int count, int height){

        float angle = 2 * FDMathUtil.FPI / count;

        for (int i = 0; i < count; i++){

            Vec3 offset = new Vec3(startRadius,0,0).yRot(angle * i);

            BlockPos pos = startPos.offset(
                    (int) Math.floor(offset.x),
                    (int) Math.floor(offset.y),
                    (int) Math.floor(offset.z)
            );

            BoundingBox box = new BoundingBox(-boxRadius, -height, -boxRadius, boxRadius, -verticalOffset, boxRadius)
                    .moved(pos.getX(), pos.getY(), pos.getZ());

            if (boxIntersectsChunk(box, chunkPos, 6)) {
                beardifiers.add(new Beardifier.Rigid(box, TerrainAdjustment.ENCAPSULATE, 0));
            }

        }
    }

    public static Rotation getRotationForStructure(Optional<ResourceLocation> location){

        if (location.isPresent()){
            if (location.get().equals(FDBosses.location("malkuth_arena_part_1"))){
                return Rotation.NONE;
            }
        }

        return null;
    }



}
