package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainAttackPattern {

    public static final int RAIL_SIZE = 3;
    public static final double TURN_RADIUS = RAIL_SIZE / 2.0;
    public static final double TUNNEL_DEPTH = 2.5;

    private final List<BlockPos> points;
    private final List<Rail> rails;
    private final List<PathSegment> path;
    private final double pathLength;

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

        List<PathSegment> path = new ArrayList<>();
        Direction exitDirection = this.getExitDirection();
        Vec3 start = new Vec3(
                points.getFirst().getX() - exitDirection.getStepX() * TUNNEL_DEPTH,
                points.getFirst().getY(),
                points.getFirst().getZ() - exitDirection.getStepZ() * TUNNEL_DEPTH
        );
        for (int i = 1; i < centers.size() - 1; i++){
            if (!sections.get(i).turn()) continue;
            BlockPos corner = centers.get(i);
            Direction incoming = direction(centers.get(i - 1), corner);
            Direction outgoing = direction(corner, centers.get(i + 1));
            Vec3 entry = new Vec3(corner.getX() - incoming.getStepX() * TURN_RADIUS, corner.getY(), corner.getZ() - incoming.getStepZ() * TURN_RADIUS);
            Vec3 exit = new Vec3(corner.getX() + outgoing.getStepX() * TURN_RADIUS, corner.getY(), corner.getZ() + outgoing.getStepZ() * TURN_RADIUS);
            this.addStraightSegment(path, start, entry);
            path.add(new PathSegment(entry, exit, incoming, outgoing, TURN_RADIUS * Math.PI / 2));
            start = exit;
        }
        BlockPos end = points.getLast();
        Direction entranceDirection = this.getEntranceDirection();
        this.addStraightSegment(path, start, new Vec3(
                end.getX() - entranceDirection.getStepX() * TUNNEL_DEPTH,
                end.getY(),
                end.getZ() - entranceDirection.getStepZ() * TUNNEL_DEPTH
        ));
        this.path = List.copyOf(path);
        double length = 0;
        for (PathSegment segment : path){
            length += segment.length();
        }
        this.pathLength = length;
    }

    private void addStraightSegment(List<PathSegment> path, Vec3 from, Vec3 to){
        double length = Math.abs(to.x - from.x) + Math.abs(to.z - from.z);
        if (length > 0){
            path.add(new PathSegment(from, to, null, null, length));
        }
    }

    public double getPathLength(){
        return pathLength;
    }

    public Vec3 getPosition(double progress){
        double distance = Mth.clamp(progress, 0, 1) * this.pathLength;
        for (PathSegment segment : this.path){
            if (distance <= segment.length()){
                return segment.getPosition(distance / segment.length());
            }
            distance -= segment.length();
        }
        return this.path.getLast().end();
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

    private record PathSegment(Vec3 start, Vec3 end, Direction incoming, Direction outgoing, double length){

        private Vec3 getPosition(double progress){
            if (this.incoming == null){
                return FDMathUtil.interpolateVectors(this.start, this.end, (float)progress);
            }
            double angle = progress * Math.PI / 2;
            double forward = TURN_RADIUS * Math.sin(angle);
            double sideways = TURN_RADIUS * (1 - Math.cos(angle));
            return this.start.add(
                    this.incoming.getStepX() * forward + this.outgoing.getStepX() * sideways,
                    0,
                    this.incoming.getStepZ() * forward + this.outgoing.getStepZ() * sideways
            );
        }
    }
}
