package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.FDBosses;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,modid = FDBosses.MOD_ID)
public class BossModEvents {

    @SubscribeEvent
    public static void setupEvent(FMLCommonSetupEvent event){

        event.enqueueWork(()->{
        });

    }

}
