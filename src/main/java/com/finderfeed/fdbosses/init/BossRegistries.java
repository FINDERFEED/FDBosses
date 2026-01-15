package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BossRegistries {

    public static final ResourceKey<Registry<PlayerSin>> PLAYER_SIN = key("player_sin");

    public static Supplier<IForgeRegistry<PlayerSin>> PLAYER_SINS;

    @SubscribeEvent
    public static void newRegistryEvent(NewRegistryEvent event){
        PLAYER_SINS = event.create(new RegistryBuilder<PlayerSin>().setName(PLAYER_SIN.location()));
    }

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.tryBuild(FDBosses.MOD_ID, name));
    }

}
