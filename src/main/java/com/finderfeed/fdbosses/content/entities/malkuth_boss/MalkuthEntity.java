package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.HeadController;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.init.FDRenderTypes;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.ModelSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.BaseModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.fdmodel.FDModelAttachmentData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class MalkuthEntity extends FDMob {

    public static final UUID FIRE_SWORD_UUID = UUID.fromString("7a6d1a24-599a-4717-baa3-42d9e3293896");
    public static final UUID FIRE_SWORD_EMISSIVE_UUID = UUID.fromString("cd95b81b-4a3f-4ef0-9b46-f0b3503ed7fb");
    public static final UUID ICE_SWORD_UUID = UUID.fromString("a46c06e3-f6af-4295-a296-20fba19ac613");
    public static final UUID ICE_SWORD_EMISSIVE_UUID = UUID.fromString("930a0a3b-5a47-4ebd-b614-7e42e5d0cd92");

    public static final ResourceLocation MALKUTH_SWORD_SOLID = FDBosses.location("textures/item/malkuth_sword_solid.png");
    public static final ResourceLocation MALKUTH_ICE_SWORD = FDBosses.location("textures/item/malkuth_sword_ice_emissive.png");
    public static final ResourceLocation MALKUTH_FIRE_SWORD = FDBosses.location("textures/item/malkuth_sword_fire_emissive.png");

    private HeadController headController;

    public MalkuthEntity(EntityType<? extends FDMob> type, Level level) {
        super(type, level);
        this.headController = new HeadController(this);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && firstTick){
            this.attachSwords();
        }
        super.tick();
        if (level().isClientSide){
            this.headController.tickClient();
        }else{
            this.setYRot(this.yBodyRot);
            Player player = this.level().getNearestPlayer(this, 30);

            if (player != null){
                this.getLookControl().setLookAt(player);
            }
        }
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
        return super.getHeadRotSpeed();
    }

}
