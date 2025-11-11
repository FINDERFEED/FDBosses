package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.mixin.IrisRenderingPipelineAccessor;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class TestDepthInShader {

    public static PostChainWithTarget postChain;

    @SubscribeEvent
    public static void registerShader(FDPostShaderInitializeEvent event){
        event.registerPostChain(((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChainWithTarget(textureManager,resourceProvider,renderTarget,FDBosses.location("shaders/post/test_post_depth.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), loaded -> {
            postChain = (PostChainWithTarget) loaded;
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderShader(RenderLevelStageEvent event){
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER && postChain != null) {
            if (!Iris.isPackInUseQuick()) {
                postChain.target.clear(Minecraft.ON_OSX);
                var mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
                postChain.target.copyDepthFrom(mainRenderTarget);


            }
        }
    }


    @SubscribeEvent
    public static void renderShader(FDRenderPostShaderEvent.Level event){

        renderShader();

    }

    public static void renderShader(){
        if (Minecraft.getInstance().level != null && postChain != null) {
            int depthTexture;

            PipelineManager pipelineManager = Iris.getPipelineManager();
            WorldRenderingPipeline pipeline = pipelineManager.getPipelineNullable();

            if (pipeline instanceof IrisRenderingPipeline irisRenderingPipeline){
                IrisRenderingPipelineAccessor accessor = (IrisRenderingPipelineAccessor) irisRenderingPipeline;
                depthTexture = accessor.getRenderTargets().getDepthTexture();
            }else{
                depthTexture = postChain.target.getDepthTextureId();
            }

            for (var effect : postChain.passes) {
                effect.getEffect().setSampler("DiffuseDepthSampler", () -> depthTexture);
            }

            postChain.process(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        }
    }


    public static class PostChainWithTarget extends PostChain{

        public RenderTarget target;

        public PostChainWithTarget(TextureManager p_110018_, ResourceProvider p_330592_, RenderTarget renderTarget, ResourceLocation p_110021_) throws IOException, JsonSyntaxException {
            super(p_110018_, p_330592_, renderTarget, p_110021_);
            this.addTempTarget("TargetForDepth", renderTarget.viewWidth, renderTarget.viewHeight);
            this.target = this.getTempTarget("TargetForDepth");
        }

    }


}
