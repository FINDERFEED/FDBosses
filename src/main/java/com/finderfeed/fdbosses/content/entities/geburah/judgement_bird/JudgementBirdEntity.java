package com.finderfeed.fdbosses.content.entities.geburah.judgement_bird;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class JudgementBirdEntity extends FDMob implements AutoSerializable {

    private static FDModel MODEL;

    private static float ATTACK_RADIUS = 6.5f;

    @SerializableField
    private Vec3 targetPos;

    private AABB roostingBox;


    public static JudgementBirdEntity summon(Level level, Vec3 pos, AABB roostingBox){

        JudgementBirdEntity entity = new JudgementBirdEntity(BossEntities.JUDGEMENT_BIRD.get(), level);
        entity.setPos(pos);
        entity.roostingBox = roostingBox;
        level.addFreshEntity(entity);

        return entity;
    }

    public static JudgementBirdEntity summon(Level level, Vec3 pos){
        return summon(level, pos,null);
    }

    public JudgementBirdEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        if (level.isClientSide && MODEL == null){
            MODEL = new FDModel(BossModels.JUDGEMENT_BIRD.get());
        }

        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(0,new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        if (!level().isClientSide){
            this.checkTargetInRoostingBox();
            this.flyAndAttack();
        }else{
        }
        super.tick();
    }

    private void flyAndAttack(){

        Animation animation = BossAnims.JUDGEMENT_BIRD_FLY.get();

        if (this.getTarget() == null) {

            if (this.roostingBox != null){
                if (!this.roostingBox.contains(this.position())){
                    this.setMoveTargetPos(this.roostingBox.getCenter());
                }
            }

            if (this.targetPos != null) {
                this.flyToPos(targetPos);
            }

        }else{
            var target = this.getTarget();
            Vec3 pos = this.getFlyToTargetPos(target);
            this.flyToPos(pos);
            this.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        }

        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(animation).build());

    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    private void checkTargetInRoostingBox(){
        if (roostingBox != null) {
            if (this.getTarget() != null) {
                Vec3 targetPos = this.getFlyToTargetPos(this.getTarget());
                if (!roostingBox.inflate(ATTACK_RADIUS).contains(targetPos)){
                    this.setMoveTargetPos(this.roostingBox.getCenter());
                    this.setTarget(null);
                }
            }
        }
    }

    public void setMoveTargetPos(Vec3 targetPos) {
        if (roostingBox == null) {
            this.targetPos = targetPos;
        }else{
            if (roostingBox.contains(targetPos)){
                this.targetPos = targetPos;
            }else{
                Vec3 pos = this.clipRoostingBox(targetPos);
                if (pos == null){
                    this.targetPos = roostingBox.getCenter();
                }else{
                    this.targetPos = pos;
                }
            }
        }
    }

    private Vec3 clipRoostingBox(Vec3 target){

        var cl = roostingBox.clip(target,this.position());
        if (cl.isEmpty()){
            cl = roostingBox.clip(this.position(),target);
            if (cl.isPresent()){
                return cl.get();
            }
        }else{
            return cl.get();
        }

        return null;
    }



    private Vec3 getFlyToTargetPos(LivingEntity target){

        Vec3 targetPos = target.position().add(0,target.getBbHeight() / 2,0);
        Vec3 b = targetPos.subtract(this.position());
        double dist = b.length();

        Vec3 resultPos;

        if (dist > ATTACK_RADIUS){
            resultPos =  targetPos;
        }else{
            resultPos = this.position();
        }

        if (roostingBox != null){

            double y = Math.max(this.roostingBox.minY, Math.min(resultPos.y, this.roostingBox.maxY));

            return new Vec3(
                    resultPos.x,
                    y,
                    resultPos.z
            );

        }else{
            return resultPos;
        }
    }

    private void flyToPos(Vec3 pos){

        Vec3 between = pos.subtract(this.position());

        double speed = this.getAttributeValue(Attributes.MOVEMENT_SPEED);

        double b = between.length();
        speed = FDMathUtil.lerp(0,speed, Mth.clamp(FDEasings.easeIn((float) (b / Math.max(3f,speed))), 0, 1));

        Vec3 deltaMovement = between.normalize().scale(speed);
        this.setDeltaMovement(deltaMovement);
        this.lookAt(EntityAnchorArgument.Anchor.FEET, pos);

    }




    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (tag.contains("roostingBoxMinX")) {
            this.roostingBox = new AABB(
                    tag.getDouble("roostingBoxMinX"),
                    tag.getDouble("roostingBoxMinY"),
                    tag.getDouble("roostingBoxMinZ"),
                    tag.getDouble("roostingBoxMaxX"),
                    tag.getDouble("roostingBoxMaxY"),
                    tag.getDouble("roostingBoxMaxZ")
            );
        }
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        if (this.roostingBox != null) {
            tag.putDouble("roostingBoxMinX", this.roostingBox.minX);
            tag.putDouble("roostingBoxMinY", this.roostingBox.minY);
            tag.putDouble("roostingBoxMinZ", this.roostingBox.minZ);
            tag.putDouble("roostingBoxMaxX", this.roostingBox.maxX);
            tag.putDouble("roostingBoxMaxY", this.roostingBox.maxY);
            tag.putDouble("roostingBoxMaxZ", this.roostingBox.maxZ);
        }
    }

}
