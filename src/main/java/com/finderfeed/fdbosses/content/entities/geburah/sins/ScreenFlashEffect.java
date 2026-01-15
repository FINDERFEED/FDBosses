package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdlib.systems.screen.screen_effect.ScreenEffect;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public class ScreenFlashEffect extends ScreenEffect<ScreenFlashEffectData> {

    public ComplexEasingFunction complexEasingFunction;

    public ScreenFlashEffect(ScreenFlashEffectData data, int inTime, int stayTime, int outTime) {
        super(data, inTime, stayTime, outTime);
        this.complexEasingFunction = ComplexEasingFunction.builder()
                .addArea(inTime, FDEasings::linear)
                .addArea(stayTime, FDEasings::one)
                .addArea(outTime, FDEasings::reversedLinear)
                .build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, float pticks, int currentTick, float screenWidth, float screenHeight) {

        float p = complexEasingFunction.apply(currentTick + pticks);

        var data = this.getData();
        var color = data.color;

        float percent = data.flashSizePercent;

        float width = screenWidth / 2 * percent * p;

        PoseStack matrices = guiGraphics.pose();

        float otherSideStart = screenWidth - width;

        FDRenderUtil.fill(matrices, 0, 0, width, screenHeight,
                color.r,color.g,color.b,color.a,
                color.r,color.g,color.b,0,
                color.r,color.g,color.b,0,
                color.r,color.g,color.b,color.a
        );

        FDRenderUtil.fill(matrices, otherSideStart, 0, width, screenHeight,
                color.r,color.g,color.b,0,
                color.r,color.g,color.b,color.a,
                color.r,color.g,color.b,color.a,
                color.r,color.g,color.b,0
        );


    }

}
