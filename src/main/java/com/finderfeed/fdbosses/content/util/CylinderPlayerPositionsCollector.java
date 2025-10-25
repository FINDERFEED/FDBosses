package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdlib.data_structures.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class CylinderPlayerPositionsCollector {

    private HashMap<Player, PlayerPositionData> positionData = new HashMap<>();

    private Level level;
    private float radius;
    private float height;
    private Predicate<Player> predicate;

    public CylinderPlayerPositionsCollector(Level level, float radius, float height, Predicate<Player> predicate){
        this.level = level;
        this.radius = radius;
        this.height = height;
        this.predicate = predicate;
    }

    public void tick(Vec3 pos){

        var players = BossTargetFinder.getEntitiesInCylinder(Player.class, level, pos, height, radius, predicate);

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

    public Collection<Player> getPlayers(){
        return this.positionData.keySet();
    }

    public Pair<Vec3, Vec3> getOldAndCurrentPlayerPosition(Player player){
        var data = this.positionData.get(player);
        if (data == null){
            return new Pair<>(player.position(),player.position());
        }else{
            return new Pair<>(data.oldPos, data.currentPos);
        }
    }

    public List<Vec3> getCurrentPlayerPositions(){
        return new ArrayList<>(this.positionData.values().stream().map(positionData -> positionData.currentPos).toList());
    }

    public List<Vec3> getOldPlayerPositions(){
        return new ArrayList<>(this.positionData.values().stream().map(positionData -> positionData.oldPos).toList());
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
