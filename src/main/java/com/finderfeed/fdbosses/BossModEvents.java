package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.particles.particle_processors.MoveOnACircleParticleProcessor;
import com.finderfeed.fdlib.systems.particle.FDParticleProcessors;
import com.finderfeed.fdlib.systems.particle.ParticleProcessorType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,modid = FDBosses.MOD_ID)
public class BossModEvents {

    public static ParticleProcessorType<MoveOnACircleParticleProcessor> MOVE_ON_A_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE;

    @SubscribeEvent
    public static void setupEvent(FMLCommonSetupEvent event){

        MOVE_ON_A_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE = FDParticleProcessors.register(new MoveOnACircleParticleProcessor.Type());

        event.enqueueWork(()->{
        });

    }

}
