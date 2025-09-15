package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.structures.MalkuthArenaStructure;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, FDBosses.MOD_ID);

    public static final Supplier<StructureType<MalkuthArenaStructure>> MALKUTH_ARENA = STRUCTURES.register("malkuth_arena",()-> new StructureType<>() {
        @Override
        public MapCodec<MalkuthArenaStructure> codec() {
            return MalkuthArenaStructure.CODEC;
        }
    });

}
