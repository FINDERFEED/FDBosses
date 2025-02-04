package com.finderfeed.fdbosses.entities.chesed_boss.kinetic_field;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChesedKineticFieldEntity extends FDEntity {

    public ChesedKineticFieldEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.getSystem().startAnimation("SPAWN", AnimationTicker.builder(BossAnims.CHESED_KINETIC_FIELD_SPAWN).build());
    }


    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            if (tickCount == 4) {
                double radius = this.getSquareRadius();

                this.spawnSpearParticles(-radius,-radius);
                this.spawnSpearParticles(-radius,radius);
                this.spawnSpearParticles(radius,radius);
                this.spawnSpearParticles(radius,-radius);
            }
        }

        this.getSystem().setVariable("variable.radius",(float)this.getSquareRadius() * 16);

    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (level().isClientSide){

            double radius = this.getSquareRadius();

            this.spawnSpearParticles(-radius,-radius);
            this.spawnSpearParticles(-radius,radius);
            this.spawnSpearParticles(radius,radius);
            this.spawnSpearParticles(radius,-radius);

        }
    }

    private void spawnSpearParticles(double offsetX, double offsetZ){

        Vec3 pos = this.position();
        Vec3 spear = pos.add(offsetX,0,offsetZ);

        Vec3 between = spear.subtract(pos).normalize();
        int height = 5;
        for (float i = 0; i < height;i+=0.25f){

            float p = i / (height - 1f);

            Vec3 add = between.multiply(p,p,p);

            BallParticleOptions options = BallParticleOptions.builder()
                    .friction(0.8f)
                    .size(0.5f + random.nextFloat() * 0.2f)
                    .scalingOptions(2, 10, 10)
                    .color(100 + random.nextInt(50), 255, 255)
                    .build();

            Vec3 v = spear.add(add.x,i,add.z);

            Vec3 speed = new Vec3(0.25 * random.nextFloat() + 0.25f,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

            level().addParticle(options,v.x,v.y,v.z,speed.x,0,speed.z);

        }


    }

    public VoxelShape getCollisionShape(Player player){

        double rad = this.getSquareRadius();

        Vec3 pos = this.position();

        AABB box = new AABB(
                        pos.x - rad,
                        pos.y,
                        pos.z - rad,
                        pos.x + rad,
                        pos.y + 3,
                        pos.z + rad

        ).inflate(0.025);
        if (box.contains(player.position())) {
            return Shapes.join(
                    Shapes.INFINITY,
                    Shapes.create(box),
                    BooleanOp.ONLY_FIRST
            );
        }else{
            return Shapes.empty();
        }
    }

    public double getSquareRadius(){
        return 5;
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
