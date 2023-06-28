package net.leonard.violin.item;

import net.leonard.violin.Violin;
//import net.leonard.violin.block.ModBlocks;
import net.leonard.violin.entity.ModEntities;
//import net.leonard.violin.item.custom.AmethystArmorItem;
//import net.leonard.violin.item.custom.AnimatedBlockItem;
//import net.leonard.violin.item.custom.AnimatedItem;
//import net.leonard.violin.sound.ModSounds;
import net.leonard.violin.sound.ModSounds;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Violin.MOD_ID);

   /* public static final RegistryObject<Item> BLACK_OPAL = ITEMS.register("black_opal",
            () -> new Item(new Item.Properties()));*/
    public static final RegistryObject<Item> RAW_BLACK_OPAL = ITEMS.register("raw_black_opal",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REVENGE_MUSIC_DISC = ITEMS.register("revenge_music_disc",
            () -> new RecordItem(8, ModSounds.REVENGE, new Item.Properties(), 5280));
    public static final RegistryObject<Item> FALLEN_KINGDOM_MUSIC_DISC = ITEMS.register("fallen_kingdom_music_disc",
            () -> new RecordItem(8, ModSounds.FALLEN_KINGDOM, new Item.Properties(), 5740));
    public static final RegistryObject<Item> POOP = ITEMS.register("poop",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIGER_SPAWN_EGG = ITEMS.register("tiger_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TIGER, 0xD57E36, 0x1D0D00,
                    new Item.Properties()));
    public static final RegistryObject<Item> HEROBRINE_SPAWN_EGG = ITEMS.register("herobrine_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.HEROBRINE, 0x15F7EC, 0xFFC393,
                    new Item.Properties()));





    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}