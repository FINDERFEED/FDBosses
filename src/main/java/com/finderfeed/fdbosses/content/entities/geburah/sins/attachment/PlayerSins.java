package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.init.BossDataAttachments;
import com.finderfeed.fdlib.network.FDPacketHandler;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerSins {

    public static final Codec<PlayerSins> CODEC = RecordCodecBuilder.create(p->p.group(
            Codec.list(ActivePlayerSinInstance.CODEC).fieldOf("active_sins").forGetter(v->v.activeSins),
            Codec.INT.fieldOf("sinned_times").forGetter(v->v.sinnedTimes),
            Codec.INT.fieldOf("cooldown").forGetter(v->v.sinGainCooldown)
    ).apply(p, PlayerSins::new));


    public static final NetworkCodec<PlayerSins> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.listOf(ActivePlayerSinInstance.STREAM_CODEC),v->v.activeSins,
            NetworkCodec.INT,v -> v.sinnedTimes,
            NetworkCodec.INT, v->v.sinGainCooldown,
            PlayerSins::new
    );

    private List<ActivePlayerSinInstance> activeSins = new ArrayList<>();

    private int sinnedTimes = 0;

    private int sinGainCooldown = 0;

    public PlayerSins(){}

    protected PlayerSins(List<ActivePlayerSinInstance> activeSins, int sinnedTimes, int cooldown){
        this(activeSins);
        this.sinnedTimes = sinnedTimes;
        this.sinGainCooldown = cooldown;
    }

    public PlayerSins(PlayerSins other){
        this.activeSins = new ArrayList<>(other.activeSins);
        this.sinnedTimes = other.sinnedTimes;
        this.sinGainCooldown = other.sinGainCooldown;
    }

    public PlayerSins(List<ActivePlayerSinInstance> activeSins){
        this.activeSins = new ArrayList<>(activeSins);
    }

    public static PlayerSins getPlayerSins(Player player){
        var playerTag = getPlayerSinsTag(player);
        if (!playerTag.contains("playerSins")){
            setPlayerSins(player, new PlayerSins(), true);
        }
        var tag = playerTag.get("playerSins");
        PlayerSins sins = CODEC.decode(NbtOps.INSTANCE, tag).get().left().get().getFirst();
        return sins;
    }

    public static void setPlayerSins(Player player, PlayerSins playerSins, boolean sendUpdate){
        var t = CODEC.encodeStart(NbtOps.INSTANCE, playerSins);
        var tag = t.get().left().get();
        var playerTag = getPlayerSinsTag(player);
        playerTag.put("playerSins", tag);

        if (player instanceof ServerPlayer serverPlayer && sendUpdate){
            FDPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->serverPlayer), new SyncPlayerSinsPacket(playerSins));
        }

    }

    public static void setPlayerSins(Player player, PlayerSins playerSins){
        setPlayerSins(player,playerSins,true);
    }

    public static CompoundTag getPlayerSinsTag(Player player){
        var playerSins = BossUtil.getOrCreateTag(BossUtil.getPlayerTag(player), "player_sins");
        return playerSins;
    }



    public boolean hasSinActive(PlayerSin playerSin){
        return activeSins.stream().anyMatch(inst -> inst.getSin() == playerSin);
    }

    public boolean hasExactlyThisSins(Collection<PlayerSin> playerSins){
        int count = 0;
        for (var sin : this.getActiveSins()){
            var s = sin.getSin();
            if (playerSins.contains(s)){
                count++;
            }
        }
        return count == playerSins.size() && playerSins.size() == this.getActiveSins().size();
    }

    public void setActiveSins(List<ActivePlayerSinInstance> instances){
        this.activeSins = new ArrayList<>(instances);
    }

    public ActivePlayerSinInstance getSin(PlayerSin playerSin){
        ActivePlayerSinInstance instance = null;
        for (var sin : this.activeSins){
            if (sin.getSin() == playerSin){
                instance = sin;
                break;
            }
        }
        return instance;
    }

    public int getSinnedTimes() {
        return sinnedTimes;
    }

    public void setSinnedTimes(int sinnedTimes) {
        this.sinnedTimes = sinnedTimes;
    }

    public List<ActivePlayerSinInstance> getActiveSins() {
        return activeSins;
    }

    public int getSinGainCooldown() {
        return sinGainCooldown;
    }

    public void setSinGainCooldown(int sinGainCooldown) {
        this.sinGainCooldown = Mth.clamp(sinGainCooldown, 0, Integer.MAX_VALUE);
    }

    public boolean isGainingSinsOnCooldown(){
        return this.sinGainCooldown > 0;
    }

}
