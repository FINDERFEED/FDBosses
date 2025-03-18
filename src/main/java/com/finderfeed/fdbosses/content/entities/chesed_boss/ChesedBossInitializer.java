package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChesedBossInitializer extends BossInitializer<ChesedEntity> {

    public ChesedBossInitializer(ChesedEntity boss) {
        super(boss);
    }

    @Override
    public void onStart() {
        ChesedEntity chesedEntity = this.getBoss();

        Level level = chesedEntity.level();

        Vec3 position = chesedEntity.position();

        Vec3 forward = chesedEntity.getForward();




    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onTick() {
        if (this.getTick() >= 200){
            this.setFinished();
        }
    }

}
