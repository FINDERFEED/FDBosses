package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.criterion_triggers.BossKilledCriterionTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossCriteriaTriggers {

    public static final DeferredRegister<CriterionTrigger<?>> CRITERION_TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, FDBosses.MOD_ID);

    public static final Supplier<BossKilledCriterionTrigger> BOSS_KILLED = CRITERION_TRIGGERS.register("boss_killed", BossKilledCriterionTrigger::new);

}
