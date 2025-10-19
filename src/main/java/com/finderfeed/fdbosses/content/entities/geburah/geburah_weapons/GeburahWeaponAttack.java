package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;

public abstract class GeburahWeaponAttack {

    protected GeburahEntity geburah;

    private int currentTick;

    public GeburahWeaponAttack(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void tick(){
        this.tickAttack();
        this.currentTick++;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public abstract void onAttackStart();

    public abstract void tickAttack();

    public abstract boolean hasEnded();

    public abstract void onAttackEnd();

    public abstract boolean canBeChanged();

}
