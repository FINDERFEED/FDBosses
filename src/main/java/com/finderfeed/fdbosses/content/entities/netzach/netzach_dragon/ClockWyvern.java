package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class ClockWyvern extends FDMob implements AutoSerializable {

    public static final String MAIN = "main";

    public static final String DO_NOTHING = "do_nothing";
    public static final String FLY_UP_AND_SLAM = "fly_up_and_slam";

    public static FDModel animatedModel;
    public static FDModel model;

    private AttackChain attackChain;

    public ClockWyvern(EntityType<? extends FDMob> type, Level level) {
        super(type, level);
        if (animatedModel == null){
            animatedModel = new FDModel(BossModels.CLOCK_WYVERN.get());
            model = new FDModel(BossModels.CLOCK_WYVERN.get());
        }
        attackChain = new AttackChain(level.random);
        attackChain.registerAttack(DO_NOTHING, (e) -> this.doNothing(e, 60));
        attackChain.registerAttack(FLY_UP_AND_SLAM, this::flyUpAndSlamAttack);
        attackChain.addAttack(0, DO_NOTHING);
        attackChain.addAttack(0, FLY_UP_AND_SLAM);
        this.getAnimationSystem().setAnimationsApplyListener(this::onAnimationsApply);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.setTarget(this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 100, false));
            attackChain.tick();
            this.setYRot(this.yBodyRot);
        }
    }

    @SerializableField
    private ProjectileMovementPath flyUpAndSlamPath;

    private boolean flyUpAndSlamAttack(AttackInstance attackInstance){
        if (this.getTarget() == null){
            this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE.get())
                    .setTransitionTime(5)
                    .build());
            this.noPhysics = false;
            return true;
        }

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_FLY_AND_SLAM.get())
                            .setTransitionTime(5)
                            .nextAnimation(AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE.get())
                                    .setTransitionTime(5)
                                    .build())
                    .build());
            this.getLookControl().setLookAt(this.getTarget());
            if (tick > 7){
                this.noPhysics = true;
                flyUpAndSlamPath = new ProjectileMovementPath(20, false)
                        .addPos(this.position())
                        .addPos(this.position().add(0,20,0))
                ;
                flyUpAndSlamPath.tick(this);
                attackInstance.nextStage();
            }
        }else if (stage == 1){
            this.getLookControl().setLookAt(this.getTarget());
            this.noPhysics = true;
            if (!flyUpAndSlamPath.isFinished()){
                flyUpAndSlamPath.tick(this);
            }else{
                attackInstance.nextStage();
            }
        }else if (stage == 2){
            if (tick >= 10){
                attackInstance.nextStage();
            }
        }else if (stage == 3){
            var target = this.getTarget();
            if (this.distanceTo(target) < 100){
                Vec3 groundPos = BossUtil.targetGroundPos(level(), target.position(), 40);
                ClipContext clipContext = new ClipContext(this.position(), groundPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                BlockHitResult result = level().clip(clipContext);
                Vec3 location = result.getLocation();
                flyUpAndSlamPath = new ProjectileMovementPath(8, false)
                        .addPos(this.position())
                        .addPos(location);
                attackInstance.nextStage();
            }else{
                this.noPhysics = false;
                return true;
            }
        }else if (stage == 4){
            Vec3 pos = this.flyUpAndSlamPath.getPositions().getLast();
            this.getLookControl().setLookAt(pos);
            this.noPhysics = true;
            if (!flyUpAndSlamPath.isFinished()){
                flyUpAndSlamPath.tick(this);
            }else{
                this.noPhysics = false;
                this.teleportTo(pos.x, pos.y, pos.z);
                attackInstance.nextStage();
            }
        }else{
            this.noPhysics = false;
            if (tick > 20){
                return true;
            }else if (tick == 0){
                this.slam();
            }
        }
        return false;
    }

    private void onAnimationsApply(FDModel fdModel, Float pticks) {
        var ticker = this.getAnimationSystem().getTicker(MAIN);
        if (ticker != null && ticker.getAnimation() == BossAnims.CLOCK_WYVERN_FLY_AND_SLAM.get()){
            float time = ticker.getTime(pticks);
            float startTime = 2.0f * 20;
            float endTime = 3f * 20;
            float p = Mth.clamp((time - startTime) / (endTime - startTime), 0, 1);
            if (p >= 0){
                float sqHill = FDEasings.squareHill(p);
                float xrot = this.getViewXRot(pticks);
                this.getAnimationSystem().setVariable("variable.body_rotation", sqHill * xrot);
            }
        }else{
            this.getAnimationSystem().setVariable("variable.body_rotation", 0);
        }
    }

    private boolean doNothing(AttackInstance inst, int time){
        return inst.tick > time;
    }

    private void slam(){
            BossUtil.clockWyvernSlam((ServerLevel) level(), this.position());
            level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 4f, 1f);
            PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                    .frequency(10)
                    .amplitude(10f)
                    .inTime(0)
                    .stayTime(0)
                    .outTime(10)
                    .build(),this.position(),40);


    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

    @Override
    public int getHeadRotSpeed() {
        return 20;
    }

}
