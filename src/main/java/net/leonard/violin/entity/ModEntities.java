package net.leonard.violin.entity;

import net.leonard.violin.Violin;
import net.leonard.violin.entity.custom.TigerEntity;
import net.leonard.violin.entity.custom.HerobrineEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Violin.MOD_ID);

    public static final RegistryObject<EntityType<TigerEntity>> TIGER =
            ENTITY_TYPES.register("tiger",
                    () -> EntityType.Builder.of(TigerEntity::new, MobCategory.CREATURE)
                            .sized(1.5f, 1.75f) //.sized determines hitbox size
                            .build(new ResourceLocation(Violin.MOD_ID, "tiger").toString()));

    public static final RegistryObject<EntityType<HerobrineEntity>> HEROBRINE =
            ENTITY_TYPES.register("herobrine",
                    () -> EntityType.Builder.of(HerobrineEntity::new, MobCategory.CREATURE)
                            .sized(1.0f, 0.5f) //.sized determines hitbox size
                            .build(new ResourceLocation(Violin.MOD_ID, "herobrine").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}