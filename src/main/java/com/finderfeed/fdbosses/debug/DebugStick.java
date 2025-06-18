package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashProjectile;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.init.FDRenderTypes;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.ModelSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.BaseModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.fdmodel.FDModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.item_stack.ItemStackAttachmentData;
import com.finderfeed.fdlib.systems.render_types.FDRenderType;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class DebugStick extends Item {
    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){

            float speed = 2f;

            MalkuthSlashProjectile malkuthSlashProjectile = MalkuthSlashProjectile.summon(level, player.position(), player.getLookAngle().multiply(speed,speed,speed),

                    level.random.nextFloat() > 0.5 ? MalkuthAttackType.FIRE : MalkuthAttackType.ICE
                    , 0, 2,-30);


//            UUID uuid1 = UUID.fromString("87b5dfdc-ed5b-4c04-9bcd-394cb7e47851");
//            UUID uuid2 = UUID.fromString("5389128f-b6a0-4029-b1cf-e6141e24db97");
//            UUID uuid3 = UUID.fromString("6606e335-52c1-4e46-8898-07c519d22e55");
//            UUID uuid4 = UUID.fromString("063b3de6-a3c6-4ba0-b4d2-de651bdacbff");
//            UUID uuid5 = UUID.fromString("659b684d-f36e-4f94-8310-11e8a32139d1");
//            UUID uuid6 = UUID.fromString("5e35c81e-c4da-40bf-ace3-4818320cbfd5");
//
//            Vec3 basePos = player.position().add(0,player.getEyeHeight(),0);
//            List<Entity> entities = FDHelpers.traceEntities(level, basePos,basePos.add(player.getLookAngle().multiply(3,3,3)),0,Entity::isAlive);
//            for (Entity entity : entities){
//                if (entity instanceof ChesedEntity chesedEntity){
//                    ModelSystem modelSystem = chesedEntity.getModelSystem();
//                    if (player.isCrouching()) {
//                        modelSystem.removeAttachment(uuid1);
//                        modelSystem.removeAttachment(uuid2);
//                        modelSystem.removeAttachment(uuid3);
//                    }else{
//                        modelSystem.attachToLayer(0, "r16", uuid1, FDModelAttachmentData.create(new BaseModelAttachmentData()
//                                .translation(0,0,0)
//                                .scale(0.5f,0.5f,0.5f)
//                                .rotation(0,0,FDMathUtil.FPI + 2 * FDMathUtil.FPI/4), BossModels.CHESED.get())
//                                .color(1f,1f,0f,1f)
//                                .renderType(FDRenderTypes.ENTITY_CUTOUT_NO_CULL.get())
//                                .texture(FDBosses.location("textures/entities/chesed.png")));
//
//                        modelSystem.attachToLayer(0, "r11", uuid2, new ItemStackAttachmentData( new BaseModelAttachmentData()
//                                .translation(-1,0,0)
//                                .scale(2,2,2)
//                                .rotation(FDMathUtil.FPI,0,-FDMathUtil.FPI/4), Items.DIAMOND_AXE.getDefaultInstance()));
//                        modelSystem.attachToLayer(0, "t", uuid3, new ItemStackAttachmentData(new BaseModelAttachmentData(), Items.DIAMOND_AXE.getDefaultInstance()));
//                    }
//                }
//            }


//            BlockPos base = player.getOnPos();
//
//
//            for (int x = 0; x < 200;x++){
//                for (int y = 0; y < 200;y++){
//                    boolean wasInterrupted = false;
//                    for (int z = 0; z < 200;z++){
//                        BlockPos setPos = base.offset(x,y,z);
//                        BlockState state = level.getBlockState(setPos);
//                        if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)){
//                            level.setBlock(setPos,Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
//                        }else{
//                            wasInterrupted = true;
//                            break;
//                        }
//                    }
//                    if (wasInterrupted) {
//                        for (int z = 200; z > 0; z--) {
//                            BlockPos setPos = base.offset(x, y, z);
//                            BlockState state = level.getBlockState(setPos);
//                            if (state.isAir()  || state.is(Blocks.STRUCTURE_VOID)) {
//                                level.setBlock(setPos, Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
//                            } else {
//                                break;
//                            }
//                        }
//                    }
//                }
//            }



        }

        return super.use(level, player, hand);
    }
}
