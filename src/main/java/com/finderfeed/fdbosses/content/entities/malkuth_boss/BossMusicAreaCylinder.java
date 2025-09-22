package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdlib.systems.music.music_areas.FDMusicArea;
import com.finderfeed.fdlib.systems.music.music_areas.shapes.FDMusicAreaCylinder;
import com.finderfeed.fdlib.systems.music.music_areas.shapes.FDMusicAreaShape;
import com.finderfeed.fdlib.util.FDTargetFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BossMusicAreaCylinder extends FDMusicAreaShape {

    private float radius;
    private float height;

    public BossMusicAreaCylinder(float radius, float height) {
        this.radius = radius;
        this.height = height;
    }

    @Override
    public List<ServerPlayer> getPlayersInside(ServerLevel level, Vec3 position) {

        var playerList = level.getServer().getPlayerList();

        var players = playerList.getPlayers().stream().filter(player->{
            return BossTargetFinder.isPointInCylinder(player.position(), position, height, radius);
        }).toList();

        return players;
    }
}
