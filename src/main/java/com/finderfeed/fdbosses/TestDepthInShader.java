package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.mixin.IrisRenderingPipelineAccessor;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class TestDepthInShader {

    private static PostChain postChain;

    @SubscribeEvent
    public static void registerShader(FDPostShaderInitializeEvent event){
        event.registerPostChain(((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChain(textureManager,resourceProvider,renderTarget,FDBosses.location("shaders/post/test_post_depth.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), loaded -> {
            postChain = loaded;
        });
    }

    @SubscribeEvent
    public static void renderShader(RenderLevelStageEvent event){
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER){
            renderShader();
        }
    }

    @SubscribeEvent
    public static void renderShader(FDRenderPostShaderEvent.Level event){

//        renderShader();

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
                depthTexture = Minecraft.getInstance().getMainRenderTarget().getDepthTextureId();
            }

            for (var effect : postChain.passes) {
                if (true) {
                    effect.getEffect().setSampler("DiffuseDepthSampler", () -> depthTexture);
                }
            }

            postChain.process(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        }
    }

}
