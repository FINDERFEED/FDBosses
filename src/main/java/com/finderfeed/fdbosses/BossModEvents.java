package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.particles.particle_processors.ChesedRayCircleParticleProcessor;
import com.finderfeed.fdbosses.content.criterion_triggers.BossKilledCriterionTrigger;
import com.finderfeed.fdbosses.init.BossCriteriaTriggers;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.particle.FDParticleProcessors;
import com.finderfeed.fdlib.systems.particle.ParticleProcessorType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = FDBosses.MOD_ID)
public class BossModEvents {

    public static ParticleProcessorType<ChesedRayCircleParticleProcessor> CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE;

    @SubscribeEvent
    public static void setupEvent(FMLCommonSetupEvent event){

        CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE = FDParticleProcessors.register(new ChesedRayCircleParticleProcessor.Type());

        event.enqueueWork(()->{

            CriteriaTriggers.register(BossCriteriaTriggers.BOSS_KILLED);

        });

    }



//    @SubscribeEvent
//    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event){
//        event.register(BossEntities.ICE_MALKUTH_WARRIOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
//        event.register(BossEntities.FIRE_MALKUTH_WARRIOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
//    }

}
