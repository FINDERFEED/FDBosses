package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class HorizontalCircleRandomDirections implements Iterable<Vec3> {

    private RandomSource randomSource;
    private float angle;
    private int directionsCount;
    private float randomDirectionsPercent;

    public HorizontalCircleRandomDirections(RandomSource randomSource, int directionsCount, float randomDirectionsPercent){
        this.directionsCount = directionsCount;
        this.randomDirectionsPercent = randomDirectionsPercent;
        this.angle = FDMathUtil.FPI * 2f / directionsCount;
        this.randomSource = randomSource;
    }

    @NotNull
    @Override
    public Iterator<Vec3> iterator() {
        return new Iterator<Vec3>() {

            private int currentDirection = 0;

            @Override
            public boolean hasNext() {
                return currentDirection < directionsCount;
            }

            @Override
            public Vec3 next() {

                float currentAngle = currentDirection * angle + randomSource.nextFloat() * randomDirectionsPercent * angle;

                Vec3 v = new Vec3(1,0,0).yRot(currentAngle);

                currentDirection++;

                return v;
            }
        };
    }

}
