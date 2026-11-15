package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import java.util.HashMap;
import java.util.List;

public class FullTrainAttackPatterns {

    public static final HashMap<String, List<TrainAttackPattern>> PATTERNS = new HashMap<>();

    public static final List<TrainAttackPattern> DIRECT_ROWS_A_FULL_ATTACK_PATTERN = register("direct_rows_a",
            TrainAttackPatterns.DIRECT_ROWS_A_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_A_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_A_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_A_PATH_4_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_A_PATH_5_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_A_PATH_6_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> DIRECT_ROWS_B_FULL_ATTACK_PATTERN = register("direct_rows_b",
            TrainAttackPatterns.DIRECT_ROWS_B_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_B_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_B_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_B_PATH_4_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_B_PATH_5_ATTACK_PATTERN,
            TrainAttackPatterns.DIRECT_ROWS_B_PATH_6_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> TRACK_MANIA_1_FULL_ATTACK_PATTERN = register("track_mania_1",
            TrainAttackPatterns.TRACK_MANIA_1_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_1_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_1_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_1_PATH_4_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> TRACK_MANIA_2_FULL_ATTACK_PATTERN = register("track_mania_2",
            TrainAttackPatterns.TRACK_MANIA_2_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_2_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_2_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_2_PATH_4_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> STABILIZER_BLOCKING_TRAINS_FULL_ATTACK_PATTERN = register("stabilizer_blocking_trains",
            TrainAttackPatterns.STABILIZER_BLOCKING_TRAINS_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.STABILIZER_BLOCKING_TRAINS_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.STABILIZER_BLOCKING_TRAINS_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.STABILIZER_BLOCKING_TRAINS_PATH_4_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> TRACK_MANIA_3_FULL_ATTACK_PATTERN = register("track_mania_3",
            TrainAttackPatterns.TRACK_MANIA_3_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_3_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_3_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_3_PATH_4_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> TRACK_MANIA_1_ROTATED_90_FULL_ATTACK_PATTERN = register("track_mania_1_rotated_90",
            TrainAttackPatterns.TRACK_MANIA_1_ROTATED_90_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_1_ROTATED_90_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_1_ROTATED_90_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_1_ROTATED_90_PATH_4_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> TRACK_MANIA_2_ROTATED_90_FULL_ATTACK_PATTERN = register("track_mania_2_rotated_90",
            TrainAttackPatterns.TRACK_MANIA_2_ROTATED_90_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_2_ROTATED_90_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_2_ROTATED_90_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_2_ROTATED_90_PATH_4_ATTACK_PATTERN);

    public static final List<TrainAttackPattern> TRACK_MANIA_3_ROTATED_90_FULL_ATTACK_PATTERN = register("track_mania_3_rotated_90",
            TrainAttackPatterns.TRACK_MANIA_3_ROTATED_90_PATH_1_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_3_ROTATED_90_PATH_2_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_3_ROTATED_90_PATH_3_ATTACK_PATTERN,
            TrainAttackPatterns.TRACK_MANIA_3_ROTATED_90_PATH_4_ATTACK_PATTERN);

    public static List<TrainAttackPattern> register(String id, TrainAttackPattern... patterns){
        List<TrainAttackPattern> result = List.of(patterns);
        PATTERNS.put(id, result);
        return result;
    }
}
