package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.structures.MalkuthArenaStructure;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

public class BossMixinHandler {

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

    public static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci){
        if (!level.isClientSide){

            ServerPlayer serverplayer = (ServerPlayer) player;


            ItemStack weapon = null;
            ItemStack lightningCore = null;

            for (ItemStack itemStack : craftingContainer.getItems()){

                Item item = itemStack.getItem();
                if (item != BossItems.LIGHTNING_CORE && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)){
                    if (weapon == null){
                        weapon = itemStack;
                    }else{
                        weapon = null;
                        break;
                    }
                }else if (item == BossItems.LIGHTNING_CORE.get()){

                    if (lightningCore == null){
                        lightningCore = itemStack;
                    }else{
                        lightningCore = null;
                        break;
                    }
                }else if (!itemStack.isEmpty()){
                    lightningCore = null;
                    break;
                }
            }

            if (lightningCore != null && weapon != null) {

                ItemStack result = weapon.copy();
                result.set(BossDataComponents.ITEM_CORE.get(),new ItemCoreDataComponent(ItemCoreDataComponent.CoreType.LIGHTNING));

                resultContainer.setItem(0, result);
                menu.setRemoteSlot(0, result);
                serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));

                ci.cancel();
            }
        }
    }

}
