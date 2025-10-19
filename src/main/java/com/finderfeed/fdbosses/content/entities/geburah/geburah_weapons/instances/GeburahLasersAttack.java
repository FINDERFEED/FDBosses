package com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttack;
import net.minecraft.server.level.ServerLevel;

public class GeburahLasersAttack extends GeburahWeaponAttack {

    public GeburahLasersAttack(GeburahEntity geburah) {
        super(geburah);
    }

    @Override
    public void onAttackStart() {
        BossUtil.geburahWeaponsStartLaser((ServerLevel) geburah.level(), geburah.position(), 120, geburah);
    }

    @Override
    public void tickAttack() {
        this.geburah.setLaserVisualsState(true);
    }

    @Override
    public boolean hasEnded() {
        return false;
    }

    @Override
    public void onAttackEnd() {
        this.geburah.setLaserVisualsState(false);
    }

    @Override
    public boolean canBeChanged() {
        return true;
    }

}
