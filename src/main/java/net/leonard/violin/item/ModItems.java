package net.leonard.violin.item;

import net.leonard.violin.Violin;
//import net.leonard.violin.block.ModBlocks;
import net.leonard.violin.entity.ModEntities;
//import net.leonard.violin.item.custom.AmethystArmorItem;
//import net.leonard.violin.item.custom.AnimatedBlockItem;
//import net.leonard.violin.item.custom.AnimatedItem;
//import net.leonard.violin.sound.ModSounds;
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
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_BLACK_OPAL = ITEMS.register("raw_black_opal",
            () -> new Item(new Item.Properties()));
*/
    public static final RegistryObject<Item> TIGER_SPAWN_EGG = ITEMS.register("tiger_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TIGER, 0xD57E36, 0x1D0D00,
                    new Item.Properties()));

    /*public static final RegistryObject<Item> ANIMATED_ITEM = ITEMS.register("animated_item",
            () -> new AnimatedItem(new Item.Properties()));
    public static final RegistryObject<Item> ANIMATED_BLOCK_ITEM = ITEMS.register("animated_block",
            () -> new AnimatedBlockItem(ModBlocks.ANIMATED_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> AMETHYST_HELMET = ITEMS.register("amethyst_helmet",
            () -> new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> AMETHYST_CHESTPLATE = ITEMS.register("amethyst_chestplate",
            () -> new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> AMETHYST_LEGGINGS = ITEMS.register("amethyst_leggings",
            () -> new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> AMETHYST_BOOTS = ITEMS.register("amethyst_boots",
            () -> new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> MUSIC_BOX_MUSIC_DISC = ITEMS.register("music_box_music_disc",
            () -> new RecordItem(8, ModSounds.MUSIC_BOX, new Item.Properties().stacksTo(1), 320));
*/

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}