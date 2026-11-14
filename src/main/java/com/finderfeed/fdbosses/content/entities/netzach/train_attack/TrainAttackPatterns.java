package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import net.minecraft.core.BlockPos;

import java.util.HashMap;

public class TrainAttackPatterns {

    public static final HashMap<String, TrainAttackPattern> PATTERNS = new HashMap<>();

    public static final String STRAIGHT = "straight";
    public static final String CORNER = "corner";
    public static final String SNAKE = "snake";
    public static final String DETOUR = "detour";
    public static final String MANY_TURNS = "many_turns";

    public static final TrainAttackPattern STRAIGHT_ATTACK_PATTERN = register(STRAIGHT, new TrainAttackPattern(new BlockPos(0,0,-18), new BlockPos(0,0,18)));
    public static final TrainAttackPattern CORNER_ATTACK_PATTERN = register(CORNER, new TrainAttackPattern(new BlockPos(-18,0,0), BlockPos.ZERO, new BlockPos(0,0,18)));
    public static final TrainAttackPattern SNAKE_ATTACK_PATTERN = register(SNAKE, new TrainAttackPattern(new BlockPos(-18,0,-12), new BlockPos(-6,0,-12),
            new BlockPos(-6,0,0), new BlockPos(6,0,0), new BlockPos(6,0,12), new BlockPos(18,0,12)));
    public static final TrainAttackPattern DETOUR_ATTACK_PATTERN = register(DETOUR, new TrainAttackPattern(new BlockPos(-18,0,0), new BlockPos(-9,0,0),
            new BlockPos(-9,0,-12), new BlockPos(9,0,-12), new BlockPos(9,0,0), new BlockPos(18,0,0)));

    public static final TrainAttackPattern MANY_TURNS_ATTACK_PATTERN = register(MANY_TURNS, new TrainAttackPattern(
            new BlockPos(-21,0,-18),
            new BlockPos(-18,0,-18),
            new BlockPos(-18,0,-12),
            new BlockPos(-12,0,-12),
            new BlockPos(-12,0,-18),
            new BlockPos(-6,0,-18),
            new BlockPos(-6,0,-12),
            new BlockPos(0,0,-12),
            new BlockPos(0,0,-18),
            new BlockPos(6,0,-18),
            new BlockPos(6,0,-12),
            new BlockPos(12,0,-12),
            new BlockPos(12,0,-18),
            new BlockPos(18,0,-18),

            new BlockPos(18,0,0),
            new BlockPos(12,0,0),
            new BlockPos(12,0,-6),
            new BlockPos(6,0,-6),
            new BlockPos(6,0,0),
            new BlockPos(0,0,0),
            new BlockPos(0,0,-6),
            new BlockPos(-6,0,-6),
            new BlockPos(-6,0,0),
            new BlockPos(-12,0,0),
            new BlockPos(-12,0,-6),
            new BlockPos(-18,0,-6),

            new BlockPos(-18,0,12),
            new BlockPos(-12,0,12),
            new BlockPos(-12,0,6),
            new BlockPos(-6,0,6),
            new BlockPos(-6,0,12),
            new BlockPos(0,0,12),
            new BlockPos(0,0,6),
            new BlockPos(6,0,6),
            new BlockPos(6,0,12),
            new BlockPos(12,0,12),
            new BlockPos(12,0,6),
            new BlockPos(18,0,6),
            new BlockPos(18,0,18),
            new BlockPos(21,0,18)
    ));

    public static TrainAttackPattern register(String id, TrainAttackPattern pattern){
        if (id == null || id.isBlank() || pattern == null) throw new IllegalArgumentException("Invalid train pattern");
        if (PATTERNS.putIfAbsent(id, pattern) != null) throw new IllegalArgumentException("Duplicate train pattern: " + id);
        return pattern;
    }
}
