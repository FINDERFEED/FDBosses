package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthBossSpawner extends BossSpawnerEntity {

    public MalkuthBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide){

            for (var player : BossTargetFinder.getEntitiesInArc(Player.class, level(), this.position().add(0,-2,0), new Vec2(0,-1),
                    FDMathUtil.FPI, MalkuthEntity.ENRAGE_HEIGHT + 2, MalkuthEntity.ENRAGE_RADIUS)){

                if (!this.isActive()) {
                    if (!player.hasEffect(BossEffects.MARK_OF_A_KNIGHT)) {
                        player.addEffect(new MobEffectInstance(BossEffects.MARK_OF_A_KNIGHT, -1, 0, true, false));
                    }
                }else{
                    if (player.hasEffect(BossEffects.MARK_OF_A_KNIGHT)) {
                        player.removeEffect(BossEffects.MARK_OF_A_KNIGHT);
                    }
                    if (player.hasEffect(BossEffects.MARK_OF_A_COWARD)) {
                        player.removeEffect(BossEffects.MARK_OF_A_COWARD);
                    }
                }

            }



        }else{
            if (this.isActive()){

                if (tickCount % 2 == 0) {

                    Vec3 add = new Vec3(0.25 + 0.25 * random.nextFloat(), 0, 0).yRot(FDMathUtil.FPI * 2 * random.nextFloat()).add(0, 0.75, 0);

                    Vec3 rnd = this.position().add(add);

                    double l = add.multiply(1, 0, 1).length() * 0.1f;
                    l *= l;

                    Vector3f color;
                    if (random.nextBoolean()) {
                        color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.FIRE, random);
                    } else {
                        color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.ICE, random);
                    }

                    float vspeed = 0.025f;

                    level().addParticle(BallParticleOptions.builder()
                            .size(0.15f)
                                    .brightness(2)
                            .color(color.x, color.y, color.z)
                            .scalingOptions(10, 0, 30)
                            .build(), true, rnd.x, rnd.y, rnd.z, add.x * vspeed, (0.025f + random.nextFloat() * 0.025f) / Math.max(1, l), add.z * vspeed);

                }


                if (tickCount % 10 == 0) {
                    Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.getRandom(random));

                    FDColor fireColorStart = new FDColor(colFire.x, colFire.y - random.nextFloat() * 0.1f - 0.3f, colFire.z, 0.5f);
                    FDColor fireColor = new FDColor(colFire.x, colFire.y + random.nextFloat() * 0.1f, colFire.z, 1f);

                    StripeParticleOptions stripeParticleOptions = StripeParticleOptions.createHorizontalCircling(fireColorStart, fireColor, new Vec3(0.01f, 1, 0),
                            FDMathUtil.FPI, 0.05f, 20, 40, 2, 0.75f, 2, 0.5f, random.nextBoolean(), false);

                    level().addParticle(stripeParticleOptions, true, this.getX(),this.getY(), this.getZ(),0,0,0);
                }

            }
        }

    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.MALKUTH.get();
    }

    @Override
    public Vec3 getPlayerItemsDropPosition(Vec3 deathPosition) {
        if (BossTargetFinder.isPointInCylinder(deathPosition, this.position().add(0,-2,0), MalkuthEntity.ENRAGE_HEIGHT + 2, MalkuthEntity.ENRAGE_RADIUS)){
            return this.position().add(0,3,-MalkuthEntity.ENRAGE_RADIUS - 5);
        }
        return null;
    }

    @Override
    public boolean canInteractWithBlockPos(BlockPos blockPos) {

        Vec3 v = blockPos.getCenter();
        Vec3 pos = this.position();

        double yDiff = v.y - pos.y;

        var hdist = v.multiply(1,0,1).distanceTo(pos.multiply(1,0,1));


        if (hdist < MalkuthEntity.ENRAGE_RADIUS + 10 && yDiff > -4 && yDiff < MalkuthEntity.ENRAGE_HEIGHT + 200){
            return false;
        }

        return true;
    }

    @Override
    public Component onArenaDestructionMessage() {
        return Component.translatable("fdbosses.word.tried_to_break_arena").withStyle(ChatFormatting.RED);
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }
}
