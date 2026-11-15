package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.boss_notes.BossNote;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = FDBosses.MOD_ID)
public class BossRegistries {

    public static final ResourceKey<Registry<PlayerSin>> PLAYER_SIN = key("player_sin");
    public static final ResourceKey<Registry<BossNote>> BOSS_NOTE = key("boss_note");

    public static Registry<PlayerSin> PLAYER_SINS = new RegistryBuilder<>(PLAYER_SIN).sync(true).create();
    public static final Registry<BossNote> BOSS_NOTES = new RegistryBuilder<>(BOSS_NOTE).sync(true).create();

    @SubscribeEvent
    public static void newRegistryEvent(NewRegistryEvent event){
        event.register(PLAYER_SINS);
        event.register(BOSS_NOTES);
    }

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.tryBuild(FDBosses.MOD_ID, name));
    }

}
