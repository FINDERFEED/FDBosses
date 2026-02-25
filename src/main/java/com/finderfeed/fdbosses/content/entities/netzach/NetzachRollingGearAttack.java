package com.finderfeed.fdbosses.content.entities.netzach;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticleOptions;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.BossSimpleProjectile;
import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class NetzachRollingGearAttack extends BossSimpleProjectile {

    public static NetzachRollingGearAttack summon(LivingEntity livingEntity, Vec3 summonPos, Vec3 speed){
        NetzachRollingGearAttack attack = new NetzachRollingGearAttack(BossEntities.NETZACH_ROLLING_GEAR.get(), livingEntity.level());
        attack.setPos(summonPos);
        attack.setDeltaMovement(speed);
        livingEntity.level().addFreshEntity(attack);
        return attack;
    }

    public NetzachRollingGearAttack(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){

            if (tickCount > 200){
                this.explode();
            }

        }else{
            this.gearParticles();
            this.earthCracks();
        }
        this.fall();
    }

    private void earthCracks(){
        Vec3 old = new Vec3(xo,yo,zo);
        Vec3 between = this.position().subtract(old);
        Vec3 nb = between.normalize();
        double len = between.length();


        Vec3 lastKnown = this.getLastKnownDeltaMovement();
        Vec3 fwd = lastKnown.multiply(1,0,1).normalize();


        for (float i = 0; i < len; i++){
            Vec3 ppos = this.position().add(nb.scale(-i));

            SpriteParticleOptions spriteParticleOptions = SpriteParticleOptions.builder(BossParticles.EARTH_CRACK)
                    .particleLookDirection(fwd)
                    .verticalRendering()
                    .lifetime(5)
                    .size(1f)
                    .build();

            level().addParticle(spriteParticleOptions, true, ppos.x, ppos.y + 0.1,ppos.z,0,0,0);
        }
    }

    private void gearParticles(){
        Vec3 lastKnown = this.getLastKnownDeltaMovement();
        Vec3 bkwd = lastKnown.multiply(-1,0,-1).normalize();

        float cone = FDMathUtil.FPI / 8;
        for (int i = 0; i < 2; i++){

            Vec3 pv = bkwd.yRot(-cone + cone * 2 * random.nextFloat())
                    .add(0,1 + random.nextFloat() * 0.5,0)
                    .normalize()
                    .scale(0.5 + random.nextFloat() * 0.5f);

            Vec3 ppos = this.position().add(bkwd.reverse().scale(1.75f)).add(0,0.01,0);

            ColoredJumpingParticleOptions options = new ColoredJumpingParticleOptions.Builder()
                    .colorStart(new FDColor(1f, 1f, 1f, 1f))
                    .colorEnd(new FDColor(1f, 0.8f, 0.3f, 1f))
                    .maxPointsInTrail(1)
                    .reflectionStrength(0.33f)
                    .gravity(3f)
                    .lifetime(-1)
                    .maxJumpAmount(0)
                    .size(0.01f)
                    .build();

            level().addParticle(options, true, ppos.x, ppos.y, ppos.z, pv.x, pv.y, pv.z);

            SpriteParticleOptions sprite = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK)
                    .size(0.2f + random.nextFloat() * 0.1f)
                    .xyzRotation(random.nextInt(0,360),0, 0)
                    .xyzRotationSpeed(BossUtil.randomPlusMinus() * 20,0, 0)
                    .lifetime(random.nextInt(7,11))
                    .quadSizeDecreasing()
                    .frictionAffectsRotation()
                    .lightenedUp()
                    .friction(0.8f)
                    .build();

            level().addParticle(sprite, true, this.position().x, this.position().y, this.position().z, pv.x, pv.y - random.nextFloat() * 0.5, pv.z);
        }
    }

    private void fall(){
        var belowResult = this.clipBelow();
        if (belowResult.getType() == HitResult.Type.MISS){
            this.explode();
        }else{
            var location = belowResult.getLocation();
            double dist = location.subtract(this.position()).length();
            if (dist > 0.1){
                this.setPos(location);
            }
        }

        var forwardResult = this.clipForward();
        if (forwardResult.getType() != HitResult.Type.MISS){
            var blockPos = forwardResult.getBlockPos();
            var blockState = level().getBlockState(blockPos);
            var shape = blockState.getCollisionShape(level(), blockPos);
            var bounds = shape.bounds();
            double height = bounds.maxY;
            var location = forwardResult.getLocation();
            this.setPos(new Vec3(
                    location.x,
                    blockPos.getY() + height,
                    location.z
            ));
        }


    }



    private BlockHitResult clipBelow(){
        ClipContext clipContext = new ClipContext(this.position().add(0,this.getBbHeight() / 2,0), this.position().add(0,-1.5,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        var result = level().clip(clipContext);
        return result;
    }

    private BlockHitResult clipForward(){
        Vec3 start = this.position().add(0,this.getBbHeight() / 2,0);
        ClipContext clipContext = new ClipContext(start, start.add(this.getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        var result = level().clip(clipContext);
        return result;
    }

    private void explode(){
        if (!level().isClientSide){
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void onBlockHit(BlockHitResult result) {

    }

    @Override
    public void onEntityHit(EntityHitResult entity) {

    }

}
