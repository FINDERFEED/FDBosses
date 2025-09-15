package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.particles.particle_processors.ChesedRayCircleParticleProcessor;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.particle.FDParticleProcessors;
import com.finderfeed.fdlib.systems.particle.ParticleProcessorType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = FDBosses.MOD_ID)
public class BossModEvents {

    public static ParticleProcessorType<ChesedRayCircleParticleProcessor> CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE;

    @SubscribeEvent
    public static void setupEvent(FMLCommonSetupEvent event){

        CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE = FDParticleProcessors.register(new ChesedRayCircleParticleProcessor.Type());

        event.enqueueWork(()->{
        });

    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event){
        event.register(BossEntities.ICE_MALKUTH_WARRIOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BossEntities.FIRE_MALKUTH_WARRIOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

}
