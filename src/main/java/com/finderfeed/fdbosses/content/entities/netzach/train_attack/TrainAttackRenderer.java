package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.AnimationContext;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TrainAttackRenderer extends EntityRenderer<TrainAttack> {

    private static final ResourceLocation TUNNEL_TEXTURE = FDBosses.location("textures/entities/netzach/doom_train_tunnel.png");
    private static final ResourceLocation RAIL_TEXTURE = FDBosses.location("textures/entities/netzach/doom_train_rails.png");

    private static final ComplexEasingFunction RAIL_HEIGHT = ComplexEasingFunction.builder()
            .addArea(0.8f, p->0.8f * FDEasings.easeOut(p))
            .addArea(0.2f, p->0.8f * (1 - FDEasings.easeIn(p)))
            .build();

    public TrainAttackRenderer(EntityRendererProvider.Context context){
        super(context);
    }

    @Override
    public void render(TrainAttack entity, float yaw, float partialTick, PoseStack matrices, MultiBufferSource buffers, int light){
        TrainAttackPattern pattern = entity.getPattern();
        if (pattern == null) return;
        float tick = entity.tickCount + partialTick;
        int tunnelAnimationTime = entity.getTunnelAnimationTime();
        float disappearTick = TrainAttack.RAILS_DISAPPEAR_TIME + tunnelAnimationTime - entity.getDisappearTicksLeft() + partialTick;
        float tunnelProgress = Mth.clamp(tick / tunnelAnimationTime, 0, 1);
        if (entity.isDisappearing()){
            tunnelProgress = 1 - Mth.clamp((disappearTick - TrainAttack.RAILS_DISAPPEAR_TIME) / tunnelAnimationTime, 0, 1);
        }
        this.renderTunnel(pattern.getPoints().getFirst(), pattern.getExitDirection(), tunnelProgress, matrices, buffers, light);
        this.renderTunnel(pattern.getPoints().getLast(), pattern.getEntranceDirection(), tunnelProgress, matrices, buffers, light);

        var rails = pattern.getRails();
        int maxDepth = (rails.size() - 1) / 2;

        float appearDuration = maxDepth == 0 ? entity.getRailsAppearTime() : Math.min(20, entity.getRailsAppearTime() * 0.5f);
        float disappearDuration = Math.min(12, TrainAttack.RAILS_DISAPPEAR_TIME);

        VertexConsumer vertex = buffers.getBuffer(RenderType.entityCutoutNoCull(RAIL_TEXTURE));

        for (int i = 0; i < rails.size(); i++){
            var rail = rails.get(i);
            int depth = Math.min(i, rails.size() - 1 - i);
            float order = maxDepth == 0 ? 0 : (float)depth / maxDepth;
            float delay = order * (entity.getRailsAppearTime() - appearDuration);
            float progress = Mth.clamp((tick - delay) / appearDuration, 0, 1);
            float scale = progress;
            float height = RAIL_HEIGHT.apply(progress);
            if (entity.isDisappearing()){
                float disappearDelay = (1 - order) * (TrainAttack.RAILS_DISAPPEAR_TIME - disappearDuration);
                float disappear = Mth.clamp((disappearTick - disappearDelay) / disappearDuration, 0, 1);
                scale = 1 - FDEasings.easeIn(disappear);
                height = 0;
            }
            if (scale <= 0) continue;
            matrices.pushPose();
            BlockPos pos = rail.position();
            matrices.translate(pos.getX(), pos.getY() + height, pos.getZ());
            matrices.mulPose(Axis.YP.rotationDegrees(rail.yaw()));
            matrices.scale(scale, scale, scale);
            FDModel model = rail.turn() ? TrainAttack.getTurnRailModel() : TrainAttack.getStraightRailModel();
            model.resetTransformations();
            model.render(matrices, vertex, light, OverlayTexture.NO_OVERLAY, 1,1,1,1);
            matrices.popPose();
        }
    }

    private void renderTunnel(BlockPos pos, Direction direction, float progress, PoseStack matrices, MultiBufferSource buffers, int light){
        if (progress <= 0) return;
        matrices.pushPose();
        matrices.translate(pos.getX(), pos.getY(), pos.getZ());
        float yaw = switch (direction){
            case EAST -> 90;
            case WEST -> -90;
            case NORTH -> 180;
            default -> 0;
        };
        matrices.mulPose(Axis.YP.rotationDegrees(yaw));
        FDModel tunnel = TrainAttack.getTunnelModel();
        tunnel.resetTransformations();
        Animation animation = BossAnims.DOOM_TRAIN_TUNNEL_APPEAR.get();
        AnimationContext context = new AnimationContext(animation, Animation.LoopMode.HOLD_ON_LAST_FRAME);
        animation.applyAnimation(context, tunnel, progress * animation.getAnimTime(), 1);
        tunnel.render(matrices, buffers.getBuffer(RenderType.entityTranslucent(TUNNEL_TEXTURE)), light, OverlayTexture.NO_OVERLAY, 1,1,1,1);
        matrices.popPose();
    }

    @Override
    public boolean shouldRender(TrainAttack entity, Frustum frustum, double x, double y, double z){
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(TrainAttack entity){
        return TUNNEL_TEXTURE;
    }
}
