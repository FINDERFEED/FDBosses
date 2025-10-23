package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdlib.data_structures.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;

public class CylinderPlayerPositionsCollector {

    private HashMap<Player, PlayerPositionData> positionData = new HashMap<>();

    private Level level;
    private float radius;
    private float height;

    public CylinderPlayerPositionsCollector(Level level, float radius, float height){
        this.level = level;
        this.radius = radius;
        this.height = height;
    }

    public void tick(Vec3 pos){

        var players = BossTargetFinder.getEntitiesInCylinder(Player.class, level, pos, height, radius);

        for (var player : players){
            if (positionData.containsKey(player)){
                var data = positionData.get(player);
                data.oldPos = data.currentPos;
                data.currentPos = player.position();
            }else{
                positionData.put(player, new PlayerPositionData(player));
            }
        }

        this.removeUnusedPlayers(players);

    }

    public Pair<Vec3, Vec3> getOldAndCurrentPlayerPosition(Player player){
        var data = this.positionData.get(player);
        if (data == null){
            return new Pair<>(player.position(),player.position());
        }else{
            return new Pair<>(data.oldPos, data.currentPos);
        }
    }

    private void removeUnusedPlayers(List<Player> currentPlayersInsideCylinder){
        this.positionData.entrySet().removeIf(data -> !currentPlayersInsideCylinder.contains(data.getKey()));
    }

    private static class PlayerPositionData {

        private Vec3 currentPos;
        private Vec3 oldPos;

        public PlayerPositionData(Player player){
            this.currentPos = player.position();
            this.oldPos = player.position();
        }

        public void setCurrentPos(Vec3 currentPos) {
            this.currentPos = currentPos;
        }

        public void setOldPos(Vec3 oldPos) {
            this.oldPos = oldPos;
        }

        public Vec3 getCurrentPos() {
            return currentPos;
        }

        public Vec3 getOldPos() {
            return oldPos;
        }

    }

}
