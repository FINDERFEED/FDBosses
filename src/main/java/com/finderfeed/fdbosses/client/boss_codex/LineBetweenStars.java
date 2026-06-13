package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class LineBetweenStars {

    private ComplexEasingFunction FUNCTION = ComplexEasingFunction.builder()
            .addArea(0.5f, FDEasings::linear)
            .addArea(0.5f, FDEasings::reversedLinear)
            .build();

    private StarButton starButton1;
    private StarButton starButton2;

    private int flashTime;
    private int time;
    private int tick;

    public LineBetweenStars(StarButton starButton1, StarButton starButton2, int flashTime, int startTime){
        this.starButton1 = starButton1;
        this.starButton2 = starButton2;
        this.flashTime = flashTime;
        time = startTime;
    }

    public void tick(){
        tick++;
        time = (time + 1) % flashTime;
    }

    public void render(GuiGraphics graphics){

        float t = time + FDRenderUtil.tryGetPartialTickIgnorePause();
        float p = Mth.clamp(t / flashTime,0,1);

        float val = FUNCTION.apply(p);

        BossRenderUtil.renderLine(graphics, starButton1.getX(), starButton1.getY(), starButton2.getX(), starButton2.getY(),
                2f,
                1,1,val,1
        );

    }

}
