package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.structures.MalkuthArenaStructure;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BossStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, FDBosses.MOD_ID);

    public static final Supplier<StructureType<MalkuthArenaStructure>> MALKUTH_ARENA = STRUCTURES.register("malkuth_arena",()-> new StructureType<>() {
        @Override
        public Codec<MalkuthArenaStructure> codec() {
            return MalkuthArenaStructure.CODEC;
        }
    });

}
