package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.base.IFirstSetPosListener;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashProjectile;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.init.FDRenderTypes;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadControllerContainer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.IHasHead;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.ModelSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.BaseModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.fdmodel.FDModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackAction;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MalkuthEntity extends FDMob implements IHasHead<MalkuthEntity>, MalkuthBossBuddy, IFirstSetPosListener {

    public static final String MAIN_LAYER = "MAIN";

    public static final float ENRAGE_RADIUS = 20;

    public static final String SLASH_ATTACK = "slash";
    public static final String JUMP_CRUSH = "jump_crush";

    private static FDModel SERVER_MODEL;
    private static FDModel CLIENT_MODEL;

    public static final UUID FIRE_SWORD_UUID = UUID.fromString("7a6d1a24-599a-4717-baa3-42d9e3293896");
    public static final UUID FIRE_SWORD_EMISSIVE_UUID = UUID.fromString("cd95b81b-4a3f-4ef0-9b46-f0b3503ed7fb");
    public static final UUID ICE_SWORD_UUID = UUID.fromString("a46c06e3-f6af-4295-a296-20fba19ac613");
    public static final UUID ICE_SWORD_EMISSIVE_UUID = UUID.fromString("930a0a3b-5a47-4ebd-b614-7e42e5d0cd92");

    public static final ResourceLocation MALKUTH_SWORD_SOLID = FDBosses.location("textures/item/malkuth_sword_solid.png");
    public static final ResourceLocation MALKUTH_ICE_SWORD = FDBosses.location("textures/item/malkuth_sword_ice_emissive.png");
    public static final ResourceLocation MALKUTH_FIRE_SWORD = FDBosses.location("textures/item/malkuth_sword_fire_emissive.png");

    private HeadControllerContainer<MalkuthEntity> headControllerContainer;

    private AttackChain attackChain;

    private Vec3 spawnPosition;

    public MalkuthEntity(EntityType<? extends FDMob> type, Level level) {
        super(type, level);
        if (level.isClientSide){
            CLIENT_MODEL = new FDModel(BossModels.MALKUTH.get());
        }else{
            SERVER_MODEL = new FDModel(BossModels.MALKUTH.get());
        }

        this.headControllerContainer = new HeadControllerContainer<>(this)
                .addHeadController(CLIENT_MODEL, "head");
        this.lookControl = this.headControllerContainer;

        this.attackChain = new AttackChain(this.getRandom())
                .registerAttack(SLASH_ATTACK,this::aerialSlashAttack)
                .registerAttack(JUMP_CRUSH,this::jumpCrushAttack)
                .addAttack(0, SLASH_ATTACK)
                .addAttack(1, JUMP_CRUSH)
                .attackListener(this::attackListener)
        ;

    }

    @Override
    public void tick() {
        if (!level().isClientSide && firstTick){
            this.attachSwords();
        }

        super.tick();
        if (level().isClientSide){
            this.headControllerContainer.clientTick();
        }else{

            AnimationSystem animationSystem = this.getAnimationSystem();
            if (animationSystem.getTicker(MAIN_LAYER) == null){
                animationSystem.startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
            }

            this.attackChain.tick();

            if (this.getTarget() != null) {

                var target = this.getTarget();

                this.checkTarget(target);

                this.getLookControl().setLookAt(target);

            }else{

                this.changeTarget();

                if (this.getTarget() == null){
                    this.getLookControl().setLookAt(
                            this.position().add(this.getForward().multiply(100,0,100))
                    );
                }

            }



            this.setYRot(this.yBodyRot);

        }
    }

    private AttackAction attackListener(String attack){
        if (this.getTarget() == null){
            return AttackAction.WAIT;
        }
        return AttackAction.PROCEED;
    }

    //============================================================================ATTACKS==============================================================================================

    private MalkuthAttackType slashAttackType = MalkuthAttackType.FIRE;

    private Animation getSlashAttackAnimation(MalkuthAttackType malkuthAttackType){
        if (malkuthAttackType.isFire()){
            return BossAnims.MALKUTH_SLASH_FIRE.get();
        }else{
            return BossAnims.MALKUTH_SLASH_ICE.get();
        }
    }

    private boolean aerialSlashAttack(AttackInstance inst){

        int stage = inst.stage;

        int tick = inst.tick;

        int localStage = stage % 3;


        if (localStage == 0){
            this.slashAttackType = MalkuthAttackType.getRandom(this.getRandom());

            Animation animation = this.getSlashAttackAnimation(this.slashAttackType);

            AnimationTicker ticker = AnimationTicker.builder(animation)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                    .important()
                    .build();

            this.getAnimationSystem().startAnimation(MAIN_LAYER, ticker);
            inst.nextStage();
        }else if (localStage == 1){

            if (tick == 24){

                Vec3 spawnPos = this.position().add(0,1.5,0);
                Vec3 targetPos;
                float speedMod = 1;
                if (this.getTarget() != null){
                    LivingEntity t = this.getTarget();
                    if (t.hasEffect(MobEffects.MOVEMENT_SPEED)){
                        speedMod = t.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;
                    }
                    targetPos = t.position().add(0,t.getBbHeight()/2,0);
                }else{
                    targetPos = this.position().add(this.getForward().multiply(100,0,100));
                }


                float spawnForwardOffset = -speedMod;

                spawnPos = spawnPos.add(this.getForward().multiply(1,0,1).multiply(spawnForwardOffset,spawnForwardOffset,spawnForwardOffset));


                Vec3 speedv = targetPos.subtract(spawnPos);
                double speed = Math.min(Math.max(2,speedv.length() * 0.15f) * speedMod,5);
                speedv = speedv.normalize().multiply(speed,speed,speed);

                float rotation = this.slashAttackType.isFire() ? 25 : -25;

                MalkuthSlashProjectile malkuthSlashProjectile = MalkuthSlashProjectile.summon(level(),spawnPos,speedv,this.slashAttackType, 1, 2, rotation);

            }else if (tick >= 40){
                inst.nextStage();
            }

        }else if (localStage == 2){
            if (this.getTarget() == null){
                return false;
            }
            if (stage >= 23){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
                return true;
            }else{
                inst.nextStage();
            }
        }


        return false;
    }

    private ProjectileMovementPath jumpCrushAttackMovementPath;

    private boolean jumpCrushAttack(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_CRUSH_ATTACK_START)
                            .setLoopMode(Animation.LoopMode.ONCE)
                            .setToNullTransitionTime(20)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_JUMP_CRUSH_ATTACK_MIDAIR).build())
                    .build());
            if (tick == 5){
                this.jumpCrushAttackMovementPath = this.createJumpCrushAttackMovementPath(15);
                this.lookAt(EntityAnchorArgument.Anchor.FEET, this.jumpCrushAttackMovementPath.getPositions().getLast());

                inst.nextStage();
            }
        }else if (stage == 1){

            if (tick == 1){
                SlamParticlesPacket packet = new SlamParticlesPacket(
                        new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,-2.5,0),new Vec3(1,0,0))
                                .maxAngle(FDMathUtil.FPI * 2)
                                .maxSpeed(0.3f)
                                .collectRadius(2)
                                .maxParticleLifetime(30)
                                .count(20)
                                .maxVerticalSpeedEdges(0.15f)
                                .maxVerticalSpeedCenter(0.15f)
                );
                PacketDistributor.sendToPlayersTrackingEntity(this,packet);
            }

            this.setNoGravity(true);
            this.noPhysics = true;
            this.jumpCrushAttackMovementPath.tick(this);
            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);
            if (this.jumpCrushAttackMovementPath.isFinished()){

                Vec3 lastPos = this.jumpCrushAttackMovementPath.getPositions().getLast();

                Vec3 between = lastPos.subtract(this.position());

                double dist = between.length();

                if (dist < 0.5) {

                    inst.nextStage();
                }else{
                    this.setDeltaMovement(between.multiply(0.5,0.5,0.5));
                }
            }else{
                if (tick < 20 && this.getTarget() != null){

                    this.jumpCrushAttackMovementPath.getPositions().set(this.jumpCrushAttackMovementPath.getPositions().size() - 1, this.getTarget().position());

                }
            }
        }else if (stage == 2){

            this.setNoGravity(false);
            this.noPhysics = false;

            if (tick == 2) {

                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_CRUSH_ATTACK_END)
                                .setToNullTransitionTime(20)
                        .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                        .build());

            }else if (tick >= 5){
                MalkuthCrushAttack malkuthCrushAttack = MalkuthCrushAttack.summon(level(), this.position().add(this.getForward().multiply(1,0,1).normalize()), 1);
                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5)
                        .amplitude(5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),malkuthCrushAttack.position(),120);

                return true;
            }

        }

        return false;
    }

    private ProjectileMovementPath createJumpCrushAttackMovementPath(int flyTime){

        Vec3 target;

        if (this.getTarget() != null){
            target = this.getTarget().position();
        }else{
            target = this.position().add(this.getForward().multiply(1,0,1).normalize().multiply(10,0,10));
        }

        Vec3 begin = this.position();

        Vec3 between = target.subtract(begin);

        Vec3 pos1 = this.position().add(between.multiply(0.25f,0.25f,0.25f)).add(0,10,0);
        Vec3 pos2 = this.position().add(between.multiply(0.33f,0.33f,0.33f)).add(0,15,0);
        Vec3 pos3 = this.position().add(between.multiply(0.5f,0.5f,0.5f)).add(0,10,0);

        ProjectileMovementPath path = new ProjectileMovementPath(begin, flyTime, false)
                .addPos(pos1)
                .addPos(pos2)
                .addPos(pos3)
                .addPos(target)
                ;

        return path;
    }




    //=============================================================================OTHER================================================================================================


    @Nullable
    @Override
    public LivingEntity getTarget() {
        return super.getTarget();
    }

    @Override
    public void setTarget(@Nullable LivingEntity living) {
        super.setTarget(living);
    }

    private void changeTarget(){
        List<Player> combatants = this.getCombatants(true);
        if (combatants.isEmpty()){
            this.setTarget(null);
        }else{
            this.setTarget(combatants.get(random.nextInt(combatants.size())));
        }
    }


    private void checkTarget(LivingEntity target){

        if (target.isDeadOrDying()){
            this.changeTarget();
            return;
        }else if (target.position().distanceTo(this.position()) > ENRAGE_RADIUS){
            this.changeTarget();
            return;
        }


        if (target instanceof Player player){
            if (player.isCreative() || player.isSpectator()){
                this.changeTarget();
                return;
            }
        }


    }


    private List<Player> getCombatants(boolean includeCreativeAndSpectator){
        return this.level().getEntitiesOfClass(Player.class, this.createEnrageRadiusAABB(this.position()),player->{
            return includeCreativeAndSpectator || (!player.isCreative() && !player.isSpectator());
        });
    }

    private AABB createEnrageRadiusAABB(Vec3 offset){
        return new AABB(
                -ENRAGE_RADIUS,
                -ENRAGE_RADIUS,
                -ENRAGE_RADIUS,
                ENRAGE_RADIUS,
                ENRAGE_RADIUS,
                ENRAGE_RADIUS
        ).move(offset);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

    }

    private void attachSwords(){
        this.attachFireSword();
        this.attachIceSword();
    }

    private void attachIceSword(){
        ModelSystem modelSystem = this.getModelSystem();
        BaseModelAttachmentData iceSword = new BaseModelAttachmentData();
        modelSystem.attachToLayer(0, "ice_sword_place", ICE_SWORD_UUID, FDModelAttachmentData.create(iceSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_SWORD_SOLID)
                .renderType(FDRenderTypes.ENTITY_CUTOUT_NO_CULL.get())
        );
        modelSystem.attachToLayer(0, "ice_sword_place", ICE_SWORD_EMISSIVE_UUID, FDModelAttachmentData.create(iceSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_ICE_SWORD)
                .renderType(FDRenderTypes.EYES.get())
        );
    }

    private void attachFireSword(){
        ModelSystem modelSystem = this.getModelSystem();
        BaseModelAttachmentData fireSword = new BaseModelAttachmentData();
        modelSystem.attachToLayer(0, "fire_sword_place", FIRE_SWORD_UUID, FDModelAttachmentData.create(fireSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_SWORD_SOLID)
                .renderType(FDRenderTypes.ENTITY_CUTOUT_NO_CULL.get())
        );
        modelSystem.attachToLayer(0, "fire_sword_place", FIRE_SWORD_EMISSIVE_UUID, FDModelAttachmentData.create(fireSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_FIRE_SWORD)
                .renderType(FDRenderTypes.EYES.get())
        );
    }

    private void deattachFireSword(){
        ModelSystem modelSystem = this.getModelSystem();
        modelSystem.removeAttachment(FIRE_SWORD_UUID);
        modelSystem.removeAttachment(FIRE_SWORD_EMISSIVE_UUID);
    }

    private void deattachIceSword(){
        ModelSystem modelSystem = this.getModelSystem();
        modelSystem.removeAttachment(ICE_SWORD_UUID);
        modelSystem.removeAttachment(ICE_SWORD_EMISSIVE_UUID);
    }

    private void deattachSwords(){
        this.deattachIceSword();
        this.deattachFireSword();
    }

    @Override
    public int getHeadRotSpeed() {
        return 10;
    }

    @Override
    public int getMaxHeadXRot() {
        return super.getMaxHeadXRot();
    }

    @Override
    public int getMaxHeadYRot() {
        return 80;
    }

    @Override
    public HeadControllerContainer<MalkuthEntity> getHeadControllerContainer() {
        return headControllerContainer;
    }

    @Override
    public Vec3 setFirstPosition(Vec3 pos) {
        return this.spawnPosition = pos;
    }
}
