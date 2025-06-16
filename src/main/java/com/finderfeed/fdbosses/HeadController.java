package com.finderfeed.fdbosses;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class HeadController {

    private Mob entity;

    public HeadController(Mob mob){
        this.entity = mob;
    }

    public void tickClient(){

    }

    public Mob getEntity() {
        return entity;
    }

}
