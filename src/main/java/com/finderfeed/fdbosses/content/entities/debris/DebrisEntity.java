package com.finderfeed.fdbosses.content.entities.debris;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DebrisEntity extends Entity {

    private static final EntityDataAccessor<Integer> DEBRIS_TYPE = SynchedEntityData.defineId(DebrisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(DebrisEntity.class, EntityDataSerializers.FLOAT);

    private static final float DEFAULT_GRAVITY = (float) Player.DEFAULT_BASE_GRAVITY;
    private static final double PARTICLE_DISTANCE = 0.25;
    static final int SETTLE_TICKS = 14;

    public Vec3 initialRotation = Vec3.ZERO;
    public Vec3 lastMovement = Vec3.ZERO;
    public float rotation;
    public float rotationOld;
    public int groundTicks;

    public DebrisEntity(EntityType<? extends DebrisEntity> type, Level level){
        super(type, level);
        if (level.isClientSide){
            this.initialRotation = new Vec3(this.random.nextFloat() * 360, this.random.nextFloat() * 360, this.random.nextFloat() * 360);
        }
    }

    public DebrisEntity(Level level, Vec3 position, Vec3 velocity, Type type, float gravity){
        this(BossEntities.DEBRIS.get(), level);
        this.moveTo(position.x, position.y, position.z);
        this.setDeltaMovement(velocity);
        this.entityData.set(DEBRIS_TYPE, type.ordinal());
        this.entityData.set(GRAVITY, gravity);
    }

    public static DebrisEntity summon(Level level, Vec3 position, Vec3 velocity, Type type, float gravity){
        var debris = new DebrisEntity(level, position, velocity, type, gravity);
        level.addFreshEntity(debris);
        return debris;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data){
        data.define(DEBRIS_TYPE, Type.GEAR_DEBRIS_1.ordinal());
        data.define(GRAVITY, DEFAULT_GRAVITY);
    }

    public Type getDebrisType(){
        return Type.values()[this.entityData.get(DEBRIS_TYPE)];
    }

    @Override
    protected double getDefaultGravity(){
        return this.entityData.get(GRAVITY);
    }

    @Override
    public void tick(){
        super.tick();
        Vec3 velocity = Vec3.ZERO;
        boolean wasAirborne = !this.onGround();
        if (wasAirborne){
            this.applyGravity();
            velocity = this.getDeltaMovement();
            this.move(MoverType.SELF, velocity);
            if (this.onGround()){
                this.setDeltaMovement(Vec3.ZERO);
            }
        }
        if (this.level().isClientSide){
            if (wasAirborne){
                this.spawnSmokeTrail();
            }
            this.rotationOld = this.rotation;
            if (!velocity.equals(Vec3.ZERO)){
                this.lastMovement = velocity;
            }
            if (this.onGround()){
                this.groundTicks = Math.min(this.groundTicks + 1, SETTLE_TICKS + 1);
            }else{
                this.groundTicks = 0;
                this.rotation += 65;
            }
        }else{
            if (this.tickCount > 200){
                this.discard();
            }
        }
    }

    private void spawnSmokeTrail(){
        Vec3 current = this.position();
        double dx = this.xo - current.x;
        double dy = this.yo - current.y;
        double dz = this.zo - current.z;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        this.level().addParticle(ParticleTypes.SMOKE, current.x, current.y, current.z, 0, 0, 0);
        for (double distance = PARTICLE_DISTANCE; distance <= length; distance += PARTICLE_DISTANCE){
            double fraction = distance / length;
            this.level().addParticle(ParticleTypes.SMOKE, current.x + dx * fraction,
                    current.y + dy * fraction, current.z + dz * fraction, 0, 0, 0);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag){
        tag.putString("debris_type", this.getDebrisType().name());
        tag.putFloat("gravity", this.entityData.get(GRAVITY));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag){
        Type type = Type.GEAR_DEBRIS_1;
        for (Type candidate : Type.values()){
            if (candidate.name().equals(tag.getString("debris_type"))){
                type = candidate;
                break;
            }
        }
        this.entityData.set(DEBRIS_TYPE, type.ordinal());
        float gravity = tag.contains("gravity", Tag.TAG_ANY_NUMERIC) ? tag.getFloat("gravity") : DEFAULT_GRAVITY;
        this.entityData.set(GRAVITY, Float.isFinite(gravity) ? gravity : DEFAULT_GRAVITY);
    }

    public enum Type {
        GEAR_DEBRIS_1(FDBosses.location("textures/entities/netzach/gear_debris_1.png"), 10 / 16f),
        GEAR_DEBRIS_2(FDBosses.location("textures/entities/netzach/gear_debris_2.png"), 20 / 16f),
        GEAR_DEBRIS_3(FDBosses.location("textures/entities/netzach/gear_debris_3.png"), 12 / 16f);

        public final ResourceLocation texture;
        public final float quadSize;

        Type(ResourceLocation texture, float quadSize){
            this.texture = texture;
            this.quadSize = quadSize;
        }
    }
}
