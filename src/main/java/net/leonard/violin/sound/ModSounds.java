package net.leonard.violin.sound;

import net.leonard.violin.Violin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Violin.MOD_ID);

    public static final RegistryObject<SoundEvent> HEROBRINE_CHARGE = registerSoundEvent("herobrine_charge");

    public static final RegistryObject<SoundEvent> HEROBRINE_LAUGH = registerSoundEvent("herobrine_laugh");

    //public static final SoundEvent HEROBRINE_CHARGE = register("entity.herobrine_charge");




    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(Violin.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}