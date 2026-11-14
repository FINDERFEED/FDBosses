package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainAttackPattern {

    public static final int RAIL_SIZE = 3;

    private final List<BlockPos> points;
    private final List<Rail> rails;

    public TrainAttackPattern(BlockPos... vertices){
        this.points = Arrays.stream(vertices).map(BlockPos::immutable).toList();
        List<BlockPos> centers = new ArrayList<>();
        centers.add(points.getFirst());
        for (int i = 1; i < points.size(); i++){
            BlockPos from = points.get(i - 1);
            BlockPos to = points.get(i);
            int dx = to.getX() - from.getX();
            int dz = to.getZ() - from.getZ();
            int length = Math.abs(dx) + Math.abs(dz);
            Direction direction = direction(from, to);
            for (int distance = RAIL_SIZE; distance <= length; distance += RAIL_SIZE){
                centers.add(from.offset(direction.getStepX() * distance, 0, direction.getStepZ() * distance));
            }
        }
        List<Rail> sections = new ArrayList<>();
        for (int i = 0; i < centers.size(); i++){
            BlockPos center = centers.get(i);
            Direction before = i == 0 ? direction(center, centers.get(1)).getOpposite() : direction(center, centers.get(i - 1));
            Direction after = i == centers.size() - 1 ? before.getOpposite() : direction(center, centers.get(i + 1));
            boolean turn = before.getAxis() != after.getAxis();
            float yaw = 0;
            if (turn){
                for (int rotation = 0; rotation < 4; rotation++){
                    Direction a = Direction.SOUTH;
                    Direction b = Direction.EAST;
                    for (int k = 0; k < rotation; k++){
                        a = a.getCounterClockWise();
                        b = b.getCounterClockWise();
                    }
                    if ((a == before && b == after) || (b == before && a == after)) yaw = rotation * 90;
                }
            }else if (after.getAxis() == Direction.Axis.X){
                yaw = 90;
            }
            sections.add(new Rail(center, turn, yaw));
        }
        this.rails = List.copyOf(sections);
    }

    public List<BlockPos> getPoints(){
        return points;
    }

    public List<Rail> getRails(){
        return rails;
    }

    public Direction getExitDirection(){
        return direction(points.getFirst(), points.get(1));
    }

    public Direction getEntranceDirection(){
        return direction(points.getLast(), points.get(points.size() - 2));
    }

    private static Direction direction(BlockPos from, BlockPos to){
        return Direction.fromDelta(to.getX() - from.getX(), 0, to.getZ() - from.getZ());
    }

    public record Rail(BlockPos position, boolean turn, float yaw){}
}
