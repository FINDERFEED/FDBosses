package com.finderfeed.fdbosses;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FDBossesServerScheduler {

    private static final List<DelayedServerAction> DELAYED_SERVER_ACTIONS = new ArrayList<>();

    @SubscribeEvent
    public static void tickActions(TickEvent.ServerTickEvent event){

        if (event.phase != TickEvent.Phase.START) return;

        var iterator = DELAYED_SERVER_ACTIONS.iterator();
        while (iterator.hasNext()){
            var action = iterator.next();
            if (action.tick()){
                action.getServerAction().accept(event.getServer());
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onServerShutdown(ServerStoppingEvent event){
        DELAYED_SERVER_ACTIONS.clear();
    }

    public static void addDelayedAction(int lifetime, Consumer<MinecraftServer> action){
        DELAYED_SERVER_ACTIONS.add(new DelayedServerAction(lifetime, action));
    }

    private static class DelayedServerAction {

        private int lifetime;
        private Consumer<MinecraftServer> serverAction;

        public DelayedServerAction(int lifetime, Consumer<MinecraftServer> serverAction){
            this.lifetime = lifetime;
            this.serverAction = serverAction;
        }

        public Consumer<MinecraftServer> getServerAction() {
            return serverAction;
        }

        public int getLifetime() {
            return lifetime;
        }

        public boolean tick(){
            lifetime--;
            return lifetime <= 0;
        }

    }

}
