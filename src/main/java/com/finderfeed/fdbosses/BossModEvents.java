package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.client.particles.particle_processors.ChesedRayCircleParticleProcessor;
import com.finderfeed.fdlib.systems.particle.FDParticleProcessors;
import com.finderfeed.fdlib.systems.particle.ParticleProcessorType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,modid = FDBosses.MOD_ID)
public class BossModEvents {

    public static ParticleProcessorType<ChesedRayCircleParticleProcessor> CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE;

    @SubscribeEvent
    public static void setupEvent(FMLCommonSetupEvent event){

        CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE = FDParticleProcessors.register(new ChesedRayCircleParticleProcessor.Type());

        event.enqueueWork(()->{
        });

    }

}
