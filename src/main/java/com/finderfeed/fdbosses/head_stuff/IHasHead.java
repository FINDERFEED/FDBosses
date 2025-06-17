package com.finderfeed.fdbosses.head_stuff;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import net.minecraft.world.entity.Mob;

public interface IHasHead<T extends Mob & AnimatedObject & IHasHead<T>> {

    HeadControllerContainer<T> getHeadControllerContainer();

}
