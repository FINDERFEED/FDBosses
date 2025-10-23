package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.instances.JumpSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.instances.MoveClockwiseSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.instances.PressedTooManyButtonsSin;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GeburahSins {

    public static final DeferredRegister<PlayerSin> SINS = DeferredRegister.create(BossRegistries.PLAYER_SIN, FDBosses.MOD_ID);

    public static final Supplier<JumpSin> JUMPING_SIN = SINS.register("jumping_sin", JumpSin::new);
    public static final Supplier<MoveClockwiseSin> MOVE_CLOCKWISE_SIN = SINS.register("move_clockwise_sin", MoveClockwiseSin::new);
    public static final Supplier<PressedTooManyButtonsSin> PRESSED_TOO_MANY_BUTTONS_SIN = SINS.register("pressed_too_many_buttons", PressedTooManyButtonsSin::new);

}
