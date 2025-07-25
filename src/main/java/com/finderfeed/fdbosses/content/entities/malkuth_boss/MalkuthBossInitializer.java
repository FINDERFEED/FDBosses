package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdlib.data_structures.Pair;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class MalkuthBossInitializer extends BossInitializer<MalkuthEntity> {


    public MalkuthBossInitializer(MalkuthEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {

        MalkuthEntity malkuth = this.getBoss();

        Vec3 v1 = new Vec3(-6.5,-1,-25).add(malkuth.position());
        Vec3 v2 = new Vec3(6.5,-1,-25).add(malkuth.position());

        AABB aabb1 = new AABB(-5,-5,-5,5,5,5).move(v1);
        AABB aabb2 = new AABB(-5,-5,-5,5,5,5).move(v2);

        if (malkuth.level().getEntitiesOfClass(MalkuthCannonEntity.class, aabb1).isEmpty()) {
            MalkuthCannonEntity cannon1 = MalkuthCannonEntity.summon(malkuth.level(), v1, v1.add(0, 0, 100), MalkuthAttackType.FIRE);
            cannon1.setPlayerControlled(true);
        }
        if (malkuth.level().getEntitiesOfClass(MalkuthCannonEntity.class, aabb2).isEmpty()) {
            MalkuthCannonEntity cannon2 = MalkuthCannonEntity.summon(malkuth.level(), v2, v2.add(0, 0, 100), MalkuthAttackType.ICE);
            cannon2.setPlayerControlled(true);
        }

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onTick() {

        MalkuthEntity boss = this.getBoss();

        int tick = this.getTick();

        HashMap<Integer, Pair<Vec3,MalkuthAttackType>> cannonSpawnMap = new HashMap<>(Map.of(
                10,new Pair<>(new Vec3(9, 10.0, 29.5),MalkuthAttackType.ICE),
                11,new Pair<>(new Vec3(13, 10.0, 27.5),MalkuthAttackType.FIRE),
                12,new Pair<>(new Vec3(11, 3.0, 29),MalkuthAttackType.FIRE),

                20,new Pair<>(new Vec3(-9, 10.0, 29.5),MalkuthAttackType.ICE),
                21,new Pair<>(new Vec3(-13, 10.0, 27.5),MalkuthAttackType.FIRE),
                22,new Pair<>(new Vec3(-11, 3.0, 29),MalkuthAttackType.ICE)
        ));

        if (cannonSpawnMap.containsKey(tick)){

            Pair<Vec3, MalkuthAttackType> sppos = cannonSpawnMap.get(tick);

            Vec3 pos = sppos.first.add(boss.position());

            MalkuthCannonEntity.summon(boss.level(),pos, pos.add(0,0,-100), sppos.second);

        }else if (tick >= 60){
            this.setFinished();
        }




    }


}
