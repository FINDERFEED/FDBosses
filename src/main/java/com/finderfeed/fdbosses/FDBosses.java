package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.init.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(FDBosses.MOD_ID)
public class FDBosses {

    public static final String MOD_ID = "fdbosses";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation location(String loc){
        return ResourceLocation.tryBuild(MOD_ID,loc);
    }

    public FDBosses(IEventBus bus, ModContainer container) {
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
        BossTileEntities.TILE_ENTITIES.register(bus);
        BossCreativeTabs.CREATIVE_TABS.register(bus);
        BossDataComponents.DATA_COMPONENTS.register(bus);
        BossRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
    }


}
