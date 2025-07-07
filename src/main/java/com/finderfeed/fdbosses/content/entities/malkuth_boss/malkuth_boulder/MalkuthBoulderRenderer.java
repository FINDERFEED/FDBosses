package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boulder;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;

public class MalkuthBoulderRenderer extends EntityRenderer<MalkuthBoulderEntity> {

    private static final ResourceLocation ICE = FDBosses.location("textures/entities/malkuth/ice_rock.png");
    private static final ResourceLocation FIRE = FDBosses.location("textures/entities/malkuth/fire_rock.png");

    private static FDModel ICE_MODEL;
    private static FDModel FIRE_MODEL;

    public MalkuthBoulderRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        if (ICE_MODEL == null) {
            ICE_MODEL = new FDModel(BossModels.MALKUTH_BOULDER_ICE.get());
        }
        if (FIRE_MODEL == null) {
            FIRE_MODEL = new FDModel(BossModels.MALKUTH_BOULDER_FIRE.get());
        }
    }

    @Override
    public void render(MalkuthBoulderEntity entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        super.render(entity, yaw, pticks, matrices, src, light);
        matrices.pushPose();

        float prepareP = Math.clamp((entity.tickCount + pticks) / entity.getPrepareTime(), 0, 1);


        FDModel model = entity.getMalkuthAttackType().isFire() ? FIRE_MODEL : ICE_MODEL;
        ResourceLocation texture = entity.getMalkuthAttackType().isFire() ? FIRE : ICE;

        float h = entity.getPrepareHeight();


        float yt = -h + h * FDEasings.easeOutBack(prepareP);

        matrices.translate(0,yt + entity.getBbHeight()/2,0);


        float time = (entity.level().getGameTime() + pticks);

        int dir = entity.getId() % 2 == 0 ? -1 : 1;

        matrices.mulPose(Axis.ZP.rotationDegrees(time * 5 * dir));
        matrices.mulPose(Axis.XP.rotationDegrees(time * 5 * dir));

        model.render(matrices, src.getBuffer(RenderType.entityCutout(texture)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,1f, 1f ,1f ,1f);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthBoulderEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
