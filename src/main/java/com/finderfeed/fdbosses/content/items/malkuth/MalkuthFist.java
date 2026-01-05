package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;

public class MalkuthFist extends Item {

    public MalkuthFist(Properties properties) {
        super(properties.component(BossDataComponents.MALKUTH_FIST_COMPONENT, new MalkuthFistDataComponent()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide){

            ItemStack item = player.getItemInHand(hand);

            if (hand == InteractionHand.MAIN_HAND){

                if (this.usesAsChain(player)) {
                    MalkuthFistChain.summon(player, false);
                    player.startUsingItem(hand);
                    return InteractionResultHolder.consume(item);
                } else {

                    level.playSound(null,player.getX(),player.getY(),player.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.PLAYERS, 1f, 1f);
                    SlamParticlesPacket packet = new SlamParticlesPacket(
                            new SlamParticlesPacket.SlamData(player.getOnPos(),player.position().add(0,0.1f,0),new Vec3(1,0,0))
                                    .maxAngle(FDMathUtil.FPI * 2)
                                    .maxSpeed(0.3f)
                                    .collectRadius(2)
                                    .maxParticleLifetime(30)
                                    .count(20)
                                    .maxVerticalSpeedEdges(0.15f)
                                    .maxVerticalSpeedCenter(0.15f)
                    );
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(player.getOnPos()),packet);
                    BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                            .size(7f)
                            .scalingOptions(1,0,1)
                            .color(1f,0.6f,0.2f)
                            .brightness(2)
                            .build();
                    ((ServerLevel)level).sendParticles(ballParticleOptions, player.getX(),player.getY(),player.getZ(),1,0,0,0,0);

                    PositionedScreenShakePacket.send((ServerLevel) level, FDShakeData.builder()
                                    .amplitude(1f)
                                    .frequency(10)
                                    .outTime(10)
                            .build(), player.position(), 20);


                    int stripesCount = 6;
                    for (int i = 0; i < stripesCount; i++) {

                        FDColor startColor;
                        FDColor endColor;
                        if (i % 2 == 0){
                            startColor = new FDColor(1, 0.8f, 0.2f, 1f);
                            endColor = new FDColor(1, 0.2f, 0.1f, 1f);
                        }else{
                            startColor = new FDColor(0.2f, 0.8f, 1f, 1f);
                            endColor = new FDColor(0.1f, 0.2f, 1f, 1f);
                        }

                        var options = StripeParticleOptions.createHorizontalCircling(startColor,endColor, new Vec3(0, 1, 0),
                                i / (float) stripesCount * FDMathUtil.FPI * 2, 0.1f, 5, 20, 0, 2, 0.5f, 0.75f, true, false);
                        ((ServerLevel)level).sendParticles(options, player.getX(),player.getY(),player.getZ(),1,0,0,0,0);
                    }

                    Vec3 lookAngle = player.getLookAngle().multiply(1,0,1);
                    if (!lookAngle.equals(Vec3.ZERO)){
                        lookAngle = lookAngle.normalize();
                    }
                    Vec3 speed = new Vec3(
                            lookAngle.x * 0.1f,
                            1.5,
                            lookAngle.z * 0.1f
                    );
                    FDLibCalls.setServerPlayerSpeed((ServerPlayer) player, speed);
                    player.setIgnoreFallDamageFromCurrentImpulse(true);
                    player.currentImpulseImpactPos = player.position();


                    player.getCooldowns().addCooldown(BossItems.MALKUTH_FIST.get(), 5);
                    return InteractionResultHolder.consume(item);

                }

            }else{
                MalkuthFistChain.summon(player, true);
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(item);
            }

        }
        return super.use(level, player, hand);
    }

    public boolean usesAsChain(Player player){

        var lookAngle = player.getLookAngle();
        double dot = lookAngle.dot(new Vec3(0,1,0));

        if (player.onGround() && dot < -0.6){
            ClipContext clipContext = new ClipContext(player.getEyePosition(), player.getEyePosition().add(lookAngle.scale(3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            var res = player.level().clip(clipContext);
            if (res.getType() != HitResult.Type.MISS){
                return false;
            }
        }

        return true;
    }

    @Override
    public int getUseDuration(ItemStack p_41454_, LivingEntity p_344979_) {
        return 72000;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean p_41408_) {
        super.inventoryTick(stack, level, entity, slot, p_41408_);
        if (entity instanceof ServerPlayer serverPlayer){

            var data = stack.get(BossDataComponents.MALKUTH_FIST_COMPONENT);

            if (data.canSkipCooldown()){
                var cooldowns = serverPlayer.getCooldowns();
                if (cooldowns.isOnCooldown(stack.getItem())){
                    if (slot != Inventory.SLOT_OFFHAND) {
                        data.setCanSkipCooldown(false);
                        stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);
                        cooldowns.removeCooldown(stack.getItem());
                    }
                }else{
                    data.setCanSkipCooldown(false);
                    stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);
                }
            }


        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide){
            var data = stack.get(BossDataComponents.MALKUTH_FIST_COMPONENT);
            data.setCanSkipCooldown(false);
            stack.set(BossDataComponents.MALKUTH_FIST_COMPONENT,data);
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}
