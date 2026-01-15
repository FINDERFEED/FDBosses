package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;

public class GeburahWeaponAttackController {

    private GeburahEntity geburah;

    private GeburahWeaponAttack currentAttack;

    public GeburahWeaponAttackController(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void tick(){
        if (currentAttack != null){
            currentAttack.tick();
            if (currentAttack.hasEnded()){
                currentAttack.onAttackEnd();
                currentAttack = null;
            }
        }
    }

    public void setCurrentAttack(GeburahWeaponAttack geburahWeaponAttack, boolean forceChange){
        if (currentAttack == null || forceChange || currentAttack.canBeChanged()) {
            if (currentAttack != null){
                currentAttack.onAttackEnd();
            }
            geburahWeaponAttack.onAttackStart();
            this.currentAttack = geburahWeaponAttack;
        }
    }

    public void stopAttack(){
        if (currentAttack != null) {
            currentAttack.onAttackEnd();
            currentAttack = null;
        }
    }

    public boolean isAttacking(){
        return currentAttack != null;
    }

    public GeburahWeaponAttack getCurrentAttack() {
        return currentAttack;
    }

}
