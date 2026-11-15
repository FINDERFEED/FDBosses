package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import net.minecraft.core.BlockPos;

import java.util.HashMap;

public class TrainAttackPatterns {

    public static final HashMap<String, TrainAttackPattern> PATTERNS = new HashMap<>();

    public static final String DIRECT_ROWS_A_PATH_1 = "direct_rows_a_path_1";
    public static final String DIRECT_ROWS_A_PATH_2 = "direct_rows_a_path_2";
    public static final String DIRECT_ROWS_A_PATH_3 = "direct_rows_a_path_3";
    public static final String DIRECT_ROWS_A_PATH_4 = "direct_rows_a_path_4";
    public static final String DIRECT_ROWS_A_PATH_5 = "direct_rows_a_path_5";
    public static final String DIRECT_ROWS_A_PATH_6 = "direct_rows_a_path_6";

    public static final String DIRECT_ROWS_B_PATH_1 = "direct_rows_b_path_1";
    public static final String DIRECT_ROWS_B_PATH_2 = "direct_rows_b_path_2";
    public static final String DIRECT_ROWS_B_PATH_3 = "direct_rows_b_path_3";
    public static final String DIRECT_ROWS_B_PATH_4 = "direct_rows_b_path_4";
    public static final String DIRECT_ROWS_B_PATH_5 = "direct_rows_b_path_5";
    public static final String DIRECT_ROWS_B_PATH_6 = "direct_rows_b_path_6";

    public static final String TRACK_MANIA_1_PATH_1 = "track_mania_1_path_1";
    public static final String TRACK_MANIA_1_PATH_2 = "track_mania_1_path_2";
    public static final String TRACK_MANIA_1_PATH_3 = "track_mania_1_path_3";
    public static final String TRACK_MANIA_1_PATH_4 = "track_mania_1_path_4";

    public static final String TRACK_MANIA_2_PATH_1 = "track_mania_2_path_1";
    public static final String TRACK_MANIA_2_PATH_2 = "track_mania_2_path_2";
    public static final String TRACK_MANIA_2_PATH_3 = "track_mania_2_path_3";
    public static final String TRACK_MANIA_2_PATH_4 = "track_mania_2_path_4";

    public static final String STABILIZER_BLOCKING_TRAINS_PATH_1 = "stabilizer_blocking_trains_path_1";
    public static final String STABILIZER_BLOCKING_TRAINS_PATH_2 = "stabilizer_blocking_trains_path_2";
    public static final String STABILIZER_BLOCKING_TRAINS_PATH_3 = "stabilizer_blocking_trains_path_3";
    public static final String STABILIZER_BLOCKING_TRAINS_PATH_4 = "stabilizer_blocking_trains_path_4";

    public static final String TRACK_MANIA_3_PATH_1 = "track_mania_3_path_1";
    public static final String TRACK_MANIA_3_PATH_2 = "track_mania_3_path_2";
    public static final String TRACK_MANIA_3_PATH_3 = "track_mania_3_path_3";
    public static final String TRACK_MANIA_3_PATH_4 = "track_mania_3_path_4";

    public static final String TRACK_MANIA_1_ROTATED_90_PATH_1 = "track_mania_1_rotated_90_path_1";
    public static final String TRACK_MANIA_1_ROTATED_90_PATH_2 = "track_mania_1_rotated_90_path_2";
    public static final String TRACK_MANIA_1_ROTATED_90_PATH_3 = "track_mania_1_rotated_90_path_3";
    public static final String TRACK_MANIA_1_ROTATED_90_PATH_4 = "track_mania_1_rotated_90_path_4";

    public static final String TRACK_MANIA_2_ROTATED_90_PATH_1 = "track_mania_2_rotated_90_path_1";
    public static final String TRACK_MANIA_2_ROTATED_90_PATH_2 = "track_mania_2_rotated_90_path_2";
    public static final String TRACK_MANIA_2_ROTATED_90_PATH_3 = "track_mania_2_rotated_90_path_3";
    public static final String TRACK_MANIA_2_ROTATED_90_PATH_4 = "track_mania_2_rotated_90_path_4";

    public static final String TRACK_MANIA_3_ROTATED_90_PATH_1 = "track_mania_3_rotated_90_path_1";
    public static final String TRACK_MANIA_3_ROTATED_90_PATH_2 = "track_mania_3_rotated_90_path_2";
    public static final String TRACK_MANIA_3_ROTATED_90_PATH_3 = "track_mania_3_rotated_90_path_3";
    public static final String TRACK_MANIA_3_ROTATED_90_PATH_4 = "track_mania_3_rotated_90_path_4";


    public static final TrainAttackPattern DIRECT_ROWS_A_PATH_1_ATTACK_PATTERN = register(DIRECT_ROWS_A_PATH_1, new TrainAttackPattern(
            new BlockPos(-21,0,18),
            new BlockPos(21,0,18)));

    public static final TrainAttackPattern DIRECT_ROWS_A_PATH_2_ATTACK_PATTERN = register(DIRECT_ROWS_A_PATH_2, new TrainAttackPattern(
            new BlockPos(21,0,11),
            new BlockPos(-21,0,11)));

    public static final TrainAttackPattern DIRECT_ROWS_A_PATH_3_ATTACK_PATTERN = register(DIRECT_ROWS_A_PATH_3, new TrainAttackPattern(
            new BlockPos(-21,0,4),
            new BlockPos(21,0,4)));

    public static final TrainAttackPattern DIRECT_ROWS_A_PATH_4_ATTACK_PATTERN = register(DIRECT_ROWS_A_PATH_4, new TrainAttackPattern(
            new BlockPos(21,0,-4),
            new BlockPos(-21,0,-4)));

    public static final TrainAttackPattern DIRECT_ROWS_A_PATH_5_ATTACK_PATTERN = register(DIRECT_ROWS_A_PATH_5, new TrainAttackPattern(
            new BlockPos(-21,0,-11),
            new BlockPos(21,0,-11)));

    public static final TrainAttackPattern DIRECT_ROWS_A_PATH_6_ATTACK_PATTERN = register(DIRECT_ROWS_A_PATH_6, new TrainAttackPattern(
            new BlockPos(21,0,-18),
            new BlockPos(-21,0,-18)));

    public static final TrainAttackPattern DIRECT_ROWS_B_PATH_1_ATTACK_PATTERN = register(DIRECT_ROWS_B_PATH_1, new TrainAttackPattern(
            new BlockPos(18,0,21),
            new BlockPos(18,0,-21)));

    public static final TrainAttackPattern DIRECT_ROWS_B_PATH_2_ATTACK_PATTERN = register(DIRECT_ROWS_B_PATH_2, new TrainAttackPattern(
            new BlockPos(4,0,21),
            new BlockPos(4,0,-21)));

    public static final TrainAttackPattern DIRECT_ROWS_B_PATH_3_ATTACK_PATTERN = register(DIRECT_ROWS_B_PATH_3, new TrainAttackPattern(
            new BlockPos(11,0,-21),
            new BlockPos(11,0,21)));

    public static final TrainAttackPattern DIRECT_ROWS_B_PATH_4_ATTACK_PATTERN = register(DIRECT_ROWS_B_PATH_4, new TrainAttackPattern(
            new BlockPos(-4,0,-21),
            new BlockPos(-4,0,21)));

    public static final TrainAttackPattern DIRECT_ROWS_B_PATH_5_ATTACK_PATTERN = register(DIRECT_ROWS_B_PATH_5, new TrainAttackPattern(
            new BlockPos(-11,0,21),
            new BlockPos(-11,0,-21)));

    public static final TrainAttackPattern DIRECT_ROWS_B_PATH_6_ATTACK_PATTERN = register(DIRECT_ROWS_B_PATH_6, new TrainAttackPattern(
            new BlockPos(-18,0,-21),
            new BlockPos(-18,0,21)));

    public static final TrainAttackPattern TRACK_MANIA_1_PATH_1_ATTACK_PATTERN = register(TRACK_MANIA_1_PATH_1, new TrainAttackPattern(
            new BlockPos(-21,0,11),
            new BlockPos(0,0,11),
            new BlockPos(0,0,-7),
            new BlockPos(21,0,-7)));

    public static final TrainAttackPattern TRACK_MANIA_1_PATH_2_ATTACK_PATTERN = register(TRACK_MANIA_1_PATH_2, new TrainAttackPattern(
            new BlockPos(-12,0,-21),
            new BlockPos(-12,0,6),
            new BlockPos(-21,0,6)));

    public static final TrainAttackPattern TRACK_MANIA_1_PATH_3_ATTACK_PATTERN = register(TRACK_MANIA_1_PATH_3, new TrainAttackPattern(
            new BlockPos(21,0,5),
            new BlockPos(6,0,5),
            new BlockPos(6,0,21)));

    public static final TrainAttackPattern TRACK_MANIA_1_PATH_4_ATTACK_PATTERN = register(TRACK_MANIA_1_PATH_4, new TrainAttackPattern(
            new BlockPos(19,0,-21),
            new BlockPos(19,0,-12),
            new BlockPos(-5,0,-12),
            new BlockPos(-5,0,-21)));

    public static final TrainAttackPattern TRACK_MANIA_2_PATH_1_ATTACK_PATTERN = register(TRACK_MANIA_2_PATH_1, new TrainAttackPattern(
            new BlockPos(-13,0,21),
            new BlockPos(-13,0,9),
            new BlockPos(-4,0,9),
            new BlockPos(-4,0,18),
            new BlockPos(5,0,18),
            new BlockPos(5,0,9),
            new BlockPos(17,0,9),
            new BlockPos(17,0,-21)));

    public static final TrainAttackPattern TRACK_MANIA_2_PATH_2_ATTACK_PATTERN = register(TRACK_MANIA_2_PATH_2, new TrainAttackPattern(
            new BlockPos(9,0,-21),
            new BlockPos(9,0,0),
            new BlockPos(-18,0,0),
            new BlockPos(-18,0,-9),
            new BlockPos(3,0,-9),
            new BlockPos(3,0,-18),
            new BlockPos(-21,0,-18)));

    public static final TrainAttackPattern TRACK_MANIA_2_PATH_3_ATTACK_PATTERN = register(TRACK_MANIA_2_PATH_3, new TrainAttackPattern(
            new BlockPos(11,0,21),
            new BlockPos(11,0,15),
            new BlockPos(21,0,15)));

    public static final TrainAttackPattern TRACK_MANIA_2_PATH_4_ATTACK_PATTERN = register(TRACK_MANIA_2_PATH_4, new TrainAttackPattern(
            new BlockPos(-21,0,4),
            new BlockPos(-18,0,4),
            new BlockPos(-18,0,21)));

    public static final TrainAttackPattern STABILIZER_BLOCKING_TRAINS_PATH_1_ATTACK_PATTERN = register(STABILIZER_BLOCKING_TRAINS_PATH_1, new TrainAttackPattern(
            new BlockPos(-21,0,-6),
            new BlockPos(-12,0,-6),
            new BlockPos(-12,0,6),
            new BlockPos(-21,0,6)));

    public static final TrainAttackPattern STABILIZER_BLOCKING_TRAINS_PATH_2_ATTACK_PATTERN = register(STABILIZER_BLOCKING_TRAINS_PATH_2, new TrainAttackPattern(
            new BlockPos(6,0,-21),
            new BlockPos(6,0,-12),
            new BlockPos(-6,0,-12),
            new BlockPos(-6,0,-21)));

    public static final TrainAttackPattern STABILIZER_BLOCKING_TRAINS_PATH_3_ATTACK_PATTERN = register(STABILIZER_BLOCKING_TRAINS_PATH_3, new TrainAttackPattern(
            new BlockPos(21,0,6),
            new BlockPos(12,0,6),
            new BlockPos(12,0,-6),
            new BlockPos(21,0,-6)));

    public static final TrainAttackPattern STABILIZER_BLOCKING_TRAINS_PATH_4_ATTACK_PATTERN = register(STABILIZER_BLOCKING_TRAINS_PATH_4, new TrainAttackPattern(
            new BlockPos(-6,0,21),
            new BlockPos(-6,0,12),
            new BlockPos(6,0,12),
            new BlockPos(6,0,21)));

    public static final TrainAttackPattern TRACK_MANIA_3_PATH_1_ATTACK_PATTERN = register(TRACK_MANIA_3_PATH_1, new TrainAttackPattern(
            new BlockPos(-21,0,-11),
            new BlockPos(0,0,-11),
            new BlockPos(0,0,7),
            new BlockPos(21,0,7)));

    public static final TrainAttackPattern TRACK_MANIA_3_PATH_2_ATTACK_PATTERN = register(TRACK_MANIA_3_PATH_2, new TrainAttackPattern(
            new BlockPos(21,0,-7),
            new BlockPos(12,0,-7),
            new BlockPos(12,0,2),
            new BlockPos(6,0,2),
            new BlockPos(6,0,-19),
            new BlockPos(-21,0,-19)));

    public static final TrainAttackPattern TRACK_MANIA_3_PATH_3_ATTACK_PATTERN = register(TRACK_MANIA_3_PATH_3, new TrainAttackPattern(
            new BlockPos(-6,0,21),
            new BlockPos(-6,0,-6),
            new BlockPos(-15,0,-6),
            new BlockPos(-15,0,6),
            new BlockPos(-21,0,6)));

    public static final TrainAttackPattern TRACK_MANIA_3_PATH_4_ATTACK_PATTERN = register(TRACK_MANIA_3_PATH_4, new TrainAttackPattern(
            new BlockPos(4,0,21),
            new BlockPos(4,0,18),
            new BlockPos(21,0,18)));


    public static final TrainAttackPattern TRACK_MANIA_1_ROTATED_90_PATH_1_ATTACK_PATTERN = register(TRACK_MANIA_1_ROTATED_90_PATH_1, rotate90(TRACK_MANIA_1_PATH_1_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_1_ROTATED_90_PATH_2_ATTACK_PATTERN = register(TRACK_MANIA_1_ROTATED_90_PATH_2, rotate90(TRACK_MANIA_1_PATH_2_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_1_ROTATED_90_PATH_3_ATTACK_PATTERN = register(TRACK_MANIA_1_ROTATED_90_PATH_3, rotate90(TRACK_MANIA_1_PATH_3_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_1_ROTATED_90_PATH_4_ATTACK_PATTERN = register(TRACK_MANIA_1_ROTATED_90_PATH_4, rotate90(TRACK_MANIA_1_PATH_4_ATTACK_PATTERN));

    public static final TrainAttackPattern TRACK_MANIA_2_ROTATED_90_PATH_1_ATTACK_PATTERN = register(TRACK_MANIA_2_ROTATED_90_PATH_1, rotate90(TRACK_MANIA_2_PATH_1_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_2_ROTATED_90_PATH_2_ATTACK_PATTERN = register(TRACK_MANIA_2_ROTATED_90_PATH_2, rotate90(TRACK_MANIA_2_PATH_2_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_2_ROTATED_90_PATH_3_ATTACK_PATTERN = register(TRACK_MANIA_2_ROTATED_90_PATH_3, rotate90(TRACK_MANIA_2_PATH_3_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_2_ROTATED_90_PATH_4_ATTACK_PATTERN = register(TRACK_MANIA_2_ROTATED_90_PATH_4, rotate90(TRACK_MANIA_2_PATH_4_ATTACK_PATTERN));

    public static final TrainAttackPattern TRACK_MANIA_3_ROTATED_90_PATH_1_ATTACK_PATTERN = register(TRACK_MANIA_3_ROTATED_90_PATH_1, rotate90(TRACK_MANIA_3_PATH_1_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_3_ROTATED_90_PATH_2_ATTACK_PATTERN = register(TRACK_MANIA_3_ROTATED_90_PATH_2, rotate90(TRACK_MANIA_3_PATH_2_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_3_ROTATED_90_PATH_3_ATTACK_PATTERN = register(TRACK_MANIA_3_ROTATED_90_PATH_3, rotate90(TRACK_MANIA_3_PATH_3_ATTACK_PATTERN));
    public static final TrainAttackPattern TRACK_MANIA_3_ROTATED_90_PATH_4_ATTACK_PATTERN = register(TRACK_MANIA_3_ROTATED_90_PATH_4, rotate90(TRACK_MANIA_3_PATH_4_ATTACK_PATTERN));

    private static TrainAttackPattern rotate90(TrainAttackPattern pattern){
        BlockPos[] points = pattern.getPoints().stream()
                .map(point -> new BlockPos(-point.getZ(), point.getY(), point.getX()))
                .toArray(BlockPos[]::new);
        return new TrainAttackPattern(points);
    }

    public static TrainAttackPattern register(String id, TrainAttackPattern pattern){
        if (PATTERNS.putIfAbsent(id, pattern) != null) throw new IllegalArgumentException("Duplicate train pattern: " + id);
        return pattern;
    }
}
