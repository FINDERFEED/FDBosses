package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.instances.*;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GeburahSins {

    public static final DeferredRegister<PlayerSin> SINS = DeferredRegister.create(BossRegistries.PLAYER_SIN, FDBosses.MOD_ID);

    public static final Supplier<JumpSin> JUMPING_SIN = SINS.register("jumping_sin", JumpSin::new);
    public static final Supplier<MoveClockwiseSin> MOVE_CLOCKWISE_SIN = SINS.register("move_clockwise_sin", MoveClockwiseSin::new);
    public static final Supplier<MoveClockwiseSin> MOVE_COUNTERCLOCKWISE_SIN = SINS.register("move_counterclockwise_sin", MoveClockwiseSin::new);
    public static final Supplier<PressedTooManyButtonsSin> PRESSED_TOO_MANY_BUTTONS_SIN = SINS.register("pressed_too_many_buttons", PressedTooManyButtonsSin::new);
    public static final Supplier<CrystalsOfSinSin> CRYSTAL_OF_SIN = SINS.register("crystal_of_sin", CrystalsOfSinSin::new);
    public static final Supplier<KillEntitySin> KILL_ENTITY_SIN = SINS.register("kill_entity_sin", KillEntitySin::new);
    public static final Supplier<StayStillSin> STAY_STILL_SIN = SINS.register("stay_still_sin", StayStillSin::new);

}
