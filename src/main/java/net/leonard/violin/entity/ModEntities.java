package net.leonard.violin.entity;

import net.leonard.violin.Violin;
//import net.leonard.violin.entity.custom.Poop;
import net.leonard.violin.entity.custom.Tiger;
import net.leonard.violin.entity.custom.Herobrine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    private Level level;

    public Level level() {
        return this.level;
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Violin.MOD_ID);

    public static final RegistryObject<EntityType<Tiger>> TIGER =
            ENTITY_TYPES.register("tiger",
                    () -> EntityType.Builder.of(Tiger::new, MobCategory.CREATURE)
                            .sized(1.5f, 1.75f) //.sized determines hitbox size
                            .build(new ResourceLocation(Violin.MOD_ID, "tiger").toString()));

    public static final RegistryObject<EntityType<Herobrine>> HEROBRINE =
            ENTITY_TYPES.register("herobrine",
                    () -> EntityType.Builder.of(Herobrine::new, MobCategory.MONSTER)
                            .sized(1.0f, 0.5f) //.sized determines hitbox size
                            .build(new ResourceLocation(Violin.MOD_ID, "herobrine").toString()));


    //public static final EntityType<Snowball> SNOWBALL = register("snowball", EntityType.Builder.<Snowball>of(Snowball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    //public static final EntityType<Poop> POOP = register("poop", EntityType.Builder.<Poop>of(Poop::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    /*private static <T extends Entity> EntityType<T> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, p_20635_, p_20636_.build(p_20635_));
    }*/
}
