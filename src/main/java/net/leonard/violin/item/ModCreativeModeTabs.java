package net.leonard.violin.item;

import net.leonard.violin.Violin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Violin.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {
    public static CreativeModeTab TUTORIAL_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event)
    {
        TUTORIAL_TAB = event.registerCreativeModeTab(new ResourceLocation(Violin.MOD_ID, "tutorial_tab"), builder -> builder.icon(() -> new ItemStack(ModItems.TIGER_SPAWN_EGG.get()))
                .title(Component.translatable("creativemodetab.tutorial_tab")));
    }
}