package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.instances.JumpSin;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossGeburahSins {

    public static final DeferredRegister<PlayerSin> SINS = DeferredRegister.create(BossRegistries.PLAYER_SIN, FDBosses.MOD_ID);

    public static final Supplier<JumpSin> JUMPING_SIN = SINS.register("jumping_sin", JumpSin::new);

}
