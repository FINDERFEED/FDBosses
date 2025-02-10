package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelInfo;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossModels {

    public static final DeferredRegister<FDModelInfo> INFOS = DeferredRegister.create(FDRegistries.MODELS, FDBosses.MOD_ID);

    public static final Supplier<FDModelInfo> CHESED = INFOS.register("chesed",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"),1f));
    public static final Supplier<FDModelInfo> CHESED_INFLATED = INFOS.register("chesed_inflated",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"),1.2f));
    public static final Supplier<FDModelInfo> CHESED_CRYSTAL_LAYER = INFOS.register("chesed_crystal_layer",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"),1.05f));
    public static final Supplier<FDModelInfo> CHESED_ELECTRIC_SPHERE = INFOS.register("chesed_electric_sphere",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"electric_orb"),1f));
    public static final Supplier<FDModelInfo> CHESED_CRYSTAL = INFOS.register("chesed_crystal",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_crystal"),1f));
    public static final Supplier<FDModelInfo> CHESED_MONOLITH = INFOS.register("chesed_monolith",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_monolith"),1f));
    public static final Supplier<FDModelInfo> CHESED_KINETIC_FIELD = INFOS.register("chesed_kinetic_field",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_kinetic_field"),1f));

}
