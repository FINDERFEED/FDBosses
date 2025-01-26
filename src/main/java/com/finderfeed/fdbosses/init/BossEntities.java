package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.entities.chesed_boss.chesed_crystal.ChesedCrystalEntity;
import com.finderfeed.fdbosses.entities.chesed_boss.earthshatter_entity.EarthShatterEntity;
import com.finderfeed.fdbosses.entities.chesed_boss.electric_sphere.ChesedElectricSphereEntity;
import com.finderfeed.fdbosses.entities.chesed_boss.falling_block.ChesedFallingBlock;
import com.finderfeed.fdbosses.entities.chesed_boss.flying_block_entity.FlyingBlockEntity;
import com.finderfeed.fdbosses.entities.chesed_boss.radial_earthquake.RadialEarthquakeEntity;
import com.finderfeed.fdbosses.projectiles.ChesedBlockProjectile;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


@EventBusSubscriber(modid = FDBosses.MOD_ID,bus = EventBusSubscriber.Bus.MOD)
public class BossEntities {


    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FDBosses.MOD_ID);

    public static final Supplier<EntityType<ChesedEntity>> CHESED = ENTITIES.register("chesed",()->EntityType.Builder.<ChesedEntity>of(
            ChesedEntity::new, MobCategory.CREATURE
    )
            .sized(1f,1f)
            .build("chesed"));

    public static final Supplier<EntityType<EarthShatterEntity>> EARTH_SHATTER = ENTITIES.register("earth_shatter",()->EntityType.Builder.<EarthShatterEntity>of(
            EarthShatterEntity::new, MobCategory.MISC
    )
            .sized(1f,1f)
            .build("earth_shatter"));

    public static final Supplier<EntityType<ChesedBlockProjectile>> BLOCK_PROJECTILE = ENTITIES.register("block_projectile",()->EntityType.Builder.<ChesedBlockProjectile>of(
            ChesedBlockProjectile::new, MobCategory.MISC
    )
            .sized(1f,1f)
            .updateInterval(1)
            .build("block_projectile"));

    public static final Supplier<EntityType<ChesedFallingBlock>> CHESED_FALLING_BLOCK = ENTITIES.register("chesed_falling_block",()->EntityType.Builder.<ChesedFallingBlock>of(
            ChesedFallingBlock::new, MobCategory.MISC
    )
            .sized(0.2f,0.2f)
            .build("chesed_falling_block"));

    public static final Supplier<EntityType<FlyingBlockEntity>> FLYING_BLOCK = ENTITIES.register("flying_block",()->EntityType.Builder.of(
            FlyingBlockEntity::new, MobCategory.MISC
    )
            .sized(1f,1f)
            .build("flying_block"));

    public static final Supplier<EntityType<ChesedElectricSphereEntity>> CHESED_ELECTRIC_SPHERE = ENTITIES.register("chesed_electric_sphere",()->EntityType.Builder.of(
            ChesedElectricSphereEntity::new, MobCategory.MISC
    )
            .sized(1f,1f)
            .build("electric_sphere"));

    public static final Supplier<EntityType<RadialEarthquakeEntity>> RADIAL_EARTHQUAKE = ENTITIES.register("radial_earthquake",()->EntityType.Builder.of(
            RadialEarthquakeEntity::new, MobCategory.MISC
    )
            .sized(0.2f,0.2f)
            .build("radial_earthquake"));

    public static final Supplier<EntityType<ChesedCrystalEntity>> CHESED_CRYSTAL = ENTITIES.register("chesed_crystal",()->EntityType.Builder.of(
            ChesedCrystalEntity::new, MobCategory.MISC
    )
            .sized(2.5f,4f)
            .build("chesed_crystal"));


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(CHESED.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH,10).build());
        event.put(CHESED_ELECTRIC_SPHERE.get(), LivingEntity.createLivingAttributes().build());
    }



}
