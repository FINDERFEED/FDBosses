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

import java.util.Optional;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "respawn", at = @At(value = "INVOKE", target = "Ljava/util/Optional;empty()Ljava/util/Optional;"))
    public void respawn(ServerPlayer serverPlayer, boolean p_11238_, CallbackInfoReturnable<ServerPlayer> cir,
                        @Local(ordinal = 0) LocalRef<ServerLevel> serverLevel,
                        @Local(name = "blockpos") LocalRef<BlockPos> blockPosLocalRef){


        var respawnPos = GeburahRespiteBlock.getSpecialRespawnPoint(serverPlayer);
        if (respawnPos != null){

            var respawnData = GeburahRespiteBlock.getRespawnData(serverPlayer);
            if (respawnData.getBoolean("diedToGeburah")){

                serverLevel.set(this.server.getLevel(respawnPos.first));
                blockPosLocalRef.set(respawnPos.second);

            }

        }


    }



    @Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;findRespawnPositionAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;", shift = At.Shift.AFTER))
    public void respawn2(ServerPlayer serverPlayer, boolean p_11238_, CallbackInfoReturnable<ServerPlayer> cir,
                        @Local(ordinal = 0) LocalRef<ServerLevel> serverLevel,
                        @Local(name = "blockpos") LocalRef<BlockPos> blockPosLocalRef){


        var respawnPos = GeburahRespiteBlock.getSpecialRespawnPoint(serverPlayer);
        if (respawnPos != null){


            var respawnData = GeburahRespiteBlock.getRespawnData(serverPlayer);
            if (respawnData.getBoolean("diedToGeburah")){

                serverLevel.set(this.server.getLevel(respawnPos.first));
                blockPosLocalRef.set(respawnPos.second);

            }

        }


    }


    @Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V"))
    public void respawn3(ServerPlayer serverPlayer, boolean p_11238_, CallbackInfoReturnable<ServerPlayer> cir, @Local LocalRef<Optional<Vec3>> optional){


        var respawnPos = GeburahRespiteBlock.getSpecialRespawnPoint(serverPlayer);
        if (respawnPos != null){


            var respawnData = GeburahRespiteBlock.getRespawnData(serverPlayer);
            if (respawnData.getBoolean("diedToGeburah")){
                optional.set(Optional.of(respawnPos.second.getCenter()));
                respawnData.remove("diedToGeburah");

                var respawnDimension = serverPlayer.getRespawnDimension();
                var respawn = serverPlayer.getRespawnPosition();
                float angle = serverPlayer.getRespawnAngle();

                if (respawnDimension != null && respawnPos != null) {
                    respawnData.putString("tempRespawningDimension", respawnDimension.location().toString());
                    respawnData.putInt("tempRespawningPosX", respawn.getX());
                    respawnData.putInt("tempRespawningPosY", respawn.getY());
                    respawnData.putInt("tempRespawningPosZ", respawn.getZ());
                    respawnData.putFloat("tempRespawningAngle", angle);
                }

                respawnData.putBoolean("shouldCancelSpawnSet", true);
            }

        }


    }

}
