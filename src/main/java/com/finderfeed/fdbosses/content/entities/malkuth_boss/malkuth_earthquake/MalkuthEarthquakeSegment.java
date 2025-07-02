package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class MalkuthEarthquakeSegment {

    private static FDModel BASE_MODEL;

    public static FDModel getBaseModel(){
        if (BASE_MODEL == null){
            BASE_MODEL = new FDModel(BossModels.MALKUTH_EARTHQUAKE_PART.get());
        }
        return BASE_MODEL;
    }

    private float angle;
    private Type type;

    private int currentTime;
    private int maxTime;

    private Vec3 offset;

    public MalkuthEarthquakeSegment(Type type, Vec3 offset, float angle, int time){
        this.angle = angle;
        this.type = type;
        this.maxTime = time;
        this.offset = offset;
    }

    public float getAngle() {
        return angle;
    }

    public Vec3 getOffset() {
        return offset;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public Type getType() {
        return type;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void tick(){
        currentTime++;
    }

    public boolean hasFinished(){
        return currentTime >= maxTime;
    }

    public float getUpPercent(float pticks){
        float time = this.currentTime + pticks;
        float quarter = maxTime * 0.25f;
        if (time <= quarter){
            return FDEasings.easeOut(time / quarter);
        }else{
            time = time - quarter;
            return FDEasings.easeOut(1 - time / (maxTime - quarter));
        }
    }

    public enum Type {

        FIRE_1(FDBosses.location("textures/entities/malkuth/malkuth_earthquake_fire_1.png"), MalkuthEarthquakeSegment::getBaseModel),
        FIRE_2(FDBosses.location("textures/entities/malkuth/malkuth_earthquake_fire_2.png"), MalkuthEarthquakeSegment::getBaseModel),
        ICE_1(FDBosses.location("textures/entities/malkuth/malkuth_earthquake_ice_1.png"), MalkuthEarthquakeSegment::getBaseModel),
        ICE_2(FDBosses.location("textures/entities/malkuth/malkuth_earthquake_ice_2.png"), MalkuthEarthquakeSegment::getBaseModel),
        ICE_SPIKE(FDBosses.location("textures/entities/malkuth/malkuth_big_ice_spike.png"), null)
        ;

        private ResourceLocation texture;
        private Supplier<FDModel> model;

        Type(ResourceLocation texture, Supplier<FDModel> model){
            this.texture = texture;
            this.model = model;
        }

        public Supplier<FDModel> getModel() {
            return model;
        }

        public boolean isModel(){
            return model != null;
        }

        public ResourceLocation getTexture() {
            return texture;
        }
    }

}
