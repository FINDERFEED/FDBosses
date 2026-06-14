package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Random;
import java.util.function.Function;

public class FlashyTexturedScreenParticle extends FDTexturedSParticle {

    private float flashFrequency;

    private float flashOffset;

    public FlashyTexturedScreenParticle(Function<ResourceLocation, ? extends ParticleRenderType> factory, ResourceLocation location) {
        super(factory, location);
        flashOffset = new Random().nextFloat() * FDMathUtil.FPI * 2;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float getA() {
        var pticks = FDRenderUtil.tryGetPartialTickIgnorePause();

        float time = this.getAge() + pticks;
        float p = Mth.clamp((time) / this.getLifetime(), 0, 1);

        float flash = (float) (Math.sin(flashFrequency * (flashOffset + time)) * 0.5f + 0.5f) * 0.5f + 0.5f;

        return FDMathUtil.lerp(0,super.getA(),1 - p) * flash;
    }

    public FlashyTexturedScreenParticle setFlashFrequency(float flashFrequency){
        this.flashFrequency = flashFrequency;
        return this;
    }

    public FlashyTexturedScreenParticle setFlashOffset(float flashOffset){
        this.flashOffset = flashOffset;
        return this;
    }

}
