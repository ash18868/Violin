package net.leonard.violin.datagen;

import net.leonard.violin.Violin;
//import net.leonard.violin.block.ModBlocks;
import net.leonard.violin.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Violin.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        /*simpleItem(ModItems.BLACK_OPAL);
        simpleItem(ModItems.RAW_BLACK_OPAL);
        saplingItem(ModBlocks.EBONY_SAPLING);*/
        simpleItem(ModItems.REVENGE_MUSIC_DISC);
        simpleItem(ModItems.FALLEN_KINGDOM_MUSIC_DISC);

        withExistingParent(ModItems.TIGER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HEROBRINE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    /*private ItemModelBuilder saplingItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(TutorialMod.MOD_ID, "block/" + item.getId().getPath()));
    }*/

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Violin.MOD_ID,"item/" + item.getId().getPath()));
    }

    /*private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(TutorialMod.MOD_ID, "item/" + item.getId().getPath()));
    }*/
}
