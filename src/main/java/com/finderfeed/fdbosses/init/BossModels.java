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
    public static final Supplier<FDModelInfo> CHESED_CRYSTAL_LAYER = INFOS.register("chesed_crystal_layer",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"),1.01f));
    public static final Supplier<FDModelInfo> CHESED_ELECTRIC_SPHERE = INFOS.register("chesed_electric_sphere",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"electric_orb"),1f));
    public static final Supplier<FDModelInfo> CHESED_CRYSTAL = INFOS.register("chesed_crystal",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_crystal"),1f));
    public static final Supplier<FDModelInfo> CHESED_MONOLITH = INFOS.register("chesed_monolith",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_monolith"),1f));
    public static final Supplier<FDModelInfo> CHESED_KINETIC_FIELD = INFOS.register("chesed_kinetic_field",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_kinetic_field"),1f));
    public static final Supplier<FDModelInfo> BOSS_SPAWNER = INFOS.register("boss_spawner",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"boss_spawner"),1f));
    public static final Supplier<FDModelInfo> BOSS_SPAWNER_CRYSTAL_LAYER = INFOS.register("boss_spawner_crystal_layer",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"boss_spawner"),1.01f));
    public static final Supplier<FDModelInfo> CHESED_RAY_REFLECTOR = INFOS.register("chesed_ray_reflector",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_ray_reflector"),1f));
    public static final Supplier<FDModelInfo> WIP_MODEL = INFOS.register("wip",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"wip"),1f));

    //Malkuth
    public static final Supplier<FDModelInfo> MALKUTH = INFOS.register("malkuth",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_SWORD = INFOS.register("malkuth_sword",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_sword"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_CRUSH_ATTACK = INFOS.register("malkuth_crush_attack",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_crush_attack"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_CANNON = INFOS.register("malkuth_cannon",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_CANNON_PROJECTILE = INFOS.register("malkuth_cannon_projectile",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon_projectile"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_EARTHQUAKE_PART = INFOS.register("malkuth_earthquake_part",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_earthquake_part"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_BOULDER_ICE = INFOS.register("malkuth_boulder_ice",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_boulder_ice"),1f));
    public static final Supplier<FDModelInfo> MALKUTH_BOULDER_FIRE = INFOS.register("malkuth_boulder_fire",()->new FDModelInfo(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_boulder_fire"),1f));

}
