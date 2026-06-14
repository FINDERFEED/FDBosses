package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LineBetweenStars {

    private ComplexEasingFunction FUNCTION = ComplexEasingFunction.builder()
            .addArea(0.5f, FDEasings::linear)
            .addArea(0.5f, FDEasings::reversedLinear)
            .build();

    private StarButton starButton1;
    private StarButton starButton2;

    private int flashTime;
    private int time;

    private int delay;
    private int travelTime;
    private int tick;

    public LineBetweenStars(StarButton starButton1, StarButton starButton2, int flashTime, int startTime, int delay, int travelTime){
        this.starButton1 = starButton1;
        this.starButton2 = starButton2;
        this.flashTime = flashTime;
        time = startTime;
        this.tick = 0;
        this.travelTime = travelTime;
        this.delay = delay;
    }

    public void tick(){
        tick = Mth.clamp(tick + 1,0,travelTime + delay);
        time = (time + 1) % flashTime;
    }

    public void render(GuiGraphics graphics){

        float t = time + FDRenderUtil.tryGetPartialTickIgnorePause();
        float p = Mth.clamp(t / flashTime,0,1);

        float val = FUNCTION.apply(p);

        float t2 = tick + FDRenderUtil.tryGetPartialTickIgnorePause();
        float p2 = Mth.clamp((t2 - delay) / travelTime,0,1);

        float xt = FDMathUtil.lerp(starButton1.getX(), starButton2.getX(), p2);
        float yt = FDMathUtil.lerp(starButton1.getY(), starButton2.getY(), p2);

        BossRenderUtil.renderLine(graphics, starButton1.getX(), starButton1.getY(), xt, yt,
                2f,
                1,1,val,1
        );

    }

}
