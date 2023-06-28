package net.leonard.violin;

import com.mojang.logging.LogUtils;
import net.leonard.violin.entity.ModEntities;
import net.leonard.violin.entity.client.HerobrineRenderer;
import net.leonard.violin.entity.client.TigerRenderer;
import net.leonard.violin.item.ModCreativeModeTabs;
import net.leonard.violin.item.ModItems;
import net.leonard.violin.sound.ModSounds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Violin.MOD_ID)
public class Violin
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "violin";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Violin()
    {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        ModSounds.register(modEventBus);

        GeckoLib.initialize();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    public static void playSoundEvent(AdvancementEvent event) {
        /*if (event.getAdvancement() instanceof "advancements.story.enter_the_end.title") {

        }*/
        System.out.println("HERE IS THE EVENT: "+ event.getAdvancement());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
        {
            event.accept(ModItems.TIGER_SPAWN_EGG);
            event.accept(ModItems.HEROBRINE_SPAWN_EGG);
        }

        if(event.getTab() == ModCreativeModeTabs.TUTORIAL_TAB.get())
        {
            event.accept(ModItems.POOP);
            event.accept(ModItems.RAW_BLACK_OPAL);
            event.accept(ModItems.HEROBRINE_SPAWN_EGG);
            event.accept(ModItems.REVENGE_MUSIC_DISC);
            event.accept(ModItems.FALLEN_KINGDOM_MUSIC_DISC);
            event.accept(ModItems.TIGER_SPAWN_EGG);

        }

    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.HEROBRINE.get(), HerobrineRenderer::new);
            EntityRenderers.register(ModEntities.TIGER.get(), TigerRenderer::new);
        }
    }
}
