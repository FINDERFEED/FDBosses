package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.content.entities.geburah.respawn_point_setter_block.GeburahRespiteBlock;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;", shift = At.Shift.AFTER))
    public void respawn(ServerPlayer serverPlayer, boolean p_11238_, CallbackInfoReturnable<ServerPlayer> cir,
                        @Local(name = "serverLevel") LocalRef<ServerLevel> serverLevel,
                        @Local(name = "blockpos") LocalRef<BlockPos> blockPosLocalRef){


        var respawnPos = GeburahRespiteBlock.getSpecialRespawnPoint(serverPlayer);
        if (respawnPos != null){

            var respawnData = GeburahRespiteBlock.getRespawnData(serverPlayer);
            if (respawnData.getBoolean("diedToGeburah")){

                serverLevel.set(this.server.getLevel(respawnPos.first));
                blockPosLocalRef.set(respawnPos.second);

                respawnData.remove("diedToGeburah");
            }

        }


    }

}
