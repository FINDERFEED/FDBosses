package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_warrior;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadControllerContainer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.IHasHead;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthWarriorEntity extends FDMob implements IHasHead<MalkuthWarriorEntity> {

    public static final String SIMPLE_HIT = "simple_axe_hit";

    private static FDModel CLIENT_MODEL;

    public AttackChain attackChain;

    public MalkuthWarriorEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        if (level.isClientSide && CLIENT_MODEL == null){
            CLIENT_MODEL = new FDModel(BossModels.MALKUTH_WARRIOR.get());
        }

        this.lookControl = new HeadControllerContainer<>(this)
                .addHeadController(CLIENT_MODEL, "head");

        this.attackChain = new AttackChain(this.level().random)
                .registerAttack(SIMPLE_HIT, this::simpleAxeAttack)
                .addAttack(0, SIMPLE_HIT)
        ;

    }

    public boolean simpleAxeAttack(AttackInstance attackInstance){

        var target = this.getTarget();

        if (target != null){
            Vec3 targetPos = target.position();
            double dist = this.position().distanceTo(targetPos);
            if (dist > 1.5f && level().getGameTime() % 5 == 0){
                this.getNavigation().moveTo(target, 1);
            }


            this.getLookControl().setLookAt(target);

        }

        return false;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){
            this.getHeadControllerContainer().clientTick();
            if (this.getHeadControllerContainer().getControllersMode() != HeadControllerContainer.Mode.LOOK){
                this.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.LOOK);
            }
            if (this.walkAnimation.speed() > 0.05f){
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_WALK)
                                .setToNullTransitionTime(5)
                        .build());
            }else{
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_IDLE).build());
            }
        }else{

            this.attackChain.tick();

            this.setYRot(this.yBodyRot);

        }
    }

    @Override
    public HeadControllerContainer<MalkuthWarriorEntity> getHeadControllerContainer() {
        return (HeadControllerContainer<MalkuthWarriorEntity>) this.lookControl;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

}
