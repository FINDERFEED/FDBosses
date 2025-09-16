package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

@Mod(FDBosses.MOD_ID)
public class FDBosses {

    public static final String MOD_ID = "fdbosses";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation location(String loc){
        return ResourceLocation.tryBuild(MOD_ID,loc);
    }

    public FDBosses() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BossItems.ITEMS.register(bus);
        BossModels.INFOS.register(bus);
        BossBlocks.BLOCKS.register(bus);
        BossSounds.SOUNDS.register(bus);
        BossBars.BOSS_BARS.register(bus);
        BossConfigs.CONFIGS.register(bus);
        BossAnims.ANIMATIONS.register(bus);
        BossEntities.ENTITIES.register(bus);
        BossEffects.MOB_EFFECTS.register(bus);
        BossParticles.PARTICLES.register(bus);
        BossStructures.STRUCTURES.register(bus);
        BossTileEntities.TILE_ENTITIES.register(bus);
        BossCreativeTabs.CREATIVE_TABS.register(bus);
//        BossDataAttachments.ATTACHMENTS.register(bus);
//        BossDataComponents.DATA_COMPONENTS.register(bus);
//        BossCriteriaTriggers.CRITERION_TRIGGERS.register(bus);
        BossRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        BossEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(bus);
    }


}
