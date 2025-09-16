package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDModelItemRenderer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDModelItemRendererOptions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class MalkuthTrophyItem extends BlockItem {

    public MalkuthTrophyItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(FDModelItemRenderer.createExtensions(FDModelItemRendererOptions.create()
                .addModel(BossModels.MALKUTH_SCREEN, RenderType.entityTranslucent(FDBosses.location("textures/entities/malkuth/malkuth_screen.png")))
                .addModel(BossModels.MALKUTH_SCREEN,RenderType.text(FDBosses.location("textures/entities/malkuth/malkuth_screen_emissive.png")))
                .setScale((ctx)->{
                    if (ctx == ItemDisplayContext.GROUND){
                        return 0.15f;
                    }
                    return 0.25f;
                })
                .addRotation((itemDisplayContext -> {
                    if (itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                        return 180f;
                    }else if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
                        return 40f;
                    }else if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND){
                        return -40f;
                    }
                    return 0f;
                }))
                .addTranslation((ctx)->{
                    if (ctx == ItemDisplayContext.GUI){
                        return new Vector3f(-0.2f,-0.1f,0);
                    }else if (ctx == ItemDisplayContext.GROUND){
                        return new Vector3f();
                    }
                    return new Vector3f(0.1f,0f,0f);
                })
        ));
    }
}
