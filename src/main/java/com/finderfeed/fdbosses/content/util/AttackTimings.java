package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.nbt.AutoSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class AttackTimings implements AutoSerializable {

    public static final StreamCodec<FriendlyByteBuf, AttackTimings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), v->v.timings,
            AttackTimings::new
    );

    private List<Integer> timings = new ArrayList<>();

    private AttackTimings(List<Integer> timings){
        this.timings = new ArrayList<>(timings);
    }

    public AttackTimings(int firstAttackTime){
        this.timings.add(firstAttackTime);
    }

    public AttackTimings(){

    }

    public AttackTimings(AttackTimings attackTimings){
        this.timings = new ArrayList<>(attackTimings.timings);
    }

    public boolean isTimeForAttack(int attack, float time){
        int timeBefore = 0;

        for (int i = 0; i < attack; i++){
            timeBefore += timings.get(i);
        }

        int attackTiming = timings.get(attack);

        time -= timeBefore;

        return time >= 0 && time < attackTiming;
    }

    public int getAttackTimingTick(int attack, float time){
        int timeBefore = 0;

        for (int i = 0; i < attack; i++){
            timeBefore += timings.get(i);
        }

        int attackTiming = timings.get(attack);

        time = time - timeBefore;
        if (time < 0 || time > attackTiming){
            time = -1;
        }

        return (int) time;
    }

    public float getAttackTimingPercent(int attack, float time){
        int timeBefore = 0;

        for (int i = 0; i < attack; i++){
            timeBefore += timings.get(i);
        }

        int attackTiming = timings.get(attack);

        time = Mth.clamp(time - timeBefore, 0, attackTiming);


        return time / attackTiming;
    }

    public AttackTimings addAttackTiming(int attackTimingDuration){
        this.timings.add(attackTimingDuration);
        return this;
    }

    public int getAttackTimingAmounts(){
        return timings.size();
    }

    public int getFullTiming(){
        return timings.stream().mapToInt(i->i).sum();
    }


    @Override
    public void autoLoad(CompoundTag tag) {
        this.timings.clear();
        int id = 0;
        while (tag.contains("timing" + id)){
            this.timings.add(tag.getInt("timing" + id));
            id++;
        }
    }

    @Override
    public void autoSave(CompoundTag tag) {
        for (int i = 0; i < timings.size(); i++){
            tag.putInt("timing" + i, timings.get(i));
        }
    }

}
