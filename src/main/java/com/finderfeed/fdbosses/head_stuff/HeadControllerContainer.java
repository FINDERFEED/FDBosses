package com.finderfeed.fdbosses.head_stuff;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

public class HeadControllerContainer<T extends Mob & AnimatedObject & IHasHead<T>> extends LookControl {

    private HashMap<String, HeadController<T>> headControllers = new HashMap<>();

    private Mode mode = Mode.LOOK;

    private T entity;

    public HeadControllerContainer(T entity){
        super(entity);
        this.entity = entity;
    }

    public HeadControllerContainer<T> addHeadController(FDModel model, String headBone){
        this.headControllers.put(headBone, new HeadController<>(this, model,headBone,entity));
        return this;
    }

    public void clientTick(){
        for (var entry : headControllers.entrySet()){
            var value = entry.getValue();
            value.clientTick();
        }
    }

    public HeadController<T> getController(String headBone){
        return headControllers.get(headBone);
    }

    public Mode getControllersMode() {
        return mode;
    }

    public void setControllersMode(Mode mode) {
        if (mode != this.mode) {
            if (!entity.level().isClientSide){
                PacketDistributor.sendToPlayersTrackingEntity(entity,new ChangeHeadControllerModePacket<>(entity, mode));
            }
            Mode old = this.mode;
            this.mode = mode;
            for (var entry : headControllers.entrySet()) {
                var value = entry.getValue();
                value.onControllerModeChanged(old, mode);
            }
        }
    }

    public enum Mode {
        LOOK,
        ANIMATION,
        ANIMATION_AND_LOOK
    }

}
