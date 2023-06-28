package net.leonard.violin.event;

import net.leonard.violin.Violin;
import net.minecraft.client.sounds.MusicManager;
import net.leonard.violin.entity.ModEntities;
import net.leonard.violin.entity.custom.Tiger;
import net.leonard.violin.entity.custom.Herobrine;
import net.leonard.violin.sound.ModSounds;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.data.advancements.packs.VanillaTheEndAdvancements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.leonard.violin.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Violin.MOD_ID)
public class ModEvents {

    /*private Level level;
    private Player player;

    public Level level() {
        return this.level;
    }
    public void playSoundFromEvent() {
        //this.player.level().playLocalSound(this.player.getX(), this.player.getEyeY(), this.player.getZ(), ModSounds.REVENGE.get(), this.player.getSoundSource(), 5.0F, 1.0F, false);
        SoundManager soundmanager = Minecraft.getInstance().getSoundManager();
        soundmanager.play((SoundInstance) ModSounds.REVENGE.get());
    }*/


    //I don't believe this will work without creating a whole extra class
    @SubscribeEvent
    public static void fallenKingdom(AdvancementEvent.AdvancementProgressEvent event) {
        String name = event.getCriterionName();
        if (name.equals("entered_end")){
            System.out.println("IT WORKED!!!!");
            //event.getEntity().playSound(ModSounds.REVENGE.get());
            //Player.playSound(ModSounds.REVENGE.get());
            //startPlaying(ModSounds.REVENGE.get());

            //This is in AdvancementRewards.java
            //p_9990_.level().playSound((Player)null, p_9990_.getX(), p_9990_.getY(), p_9990_.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((p_9990_.getRandom().nextFloat() - p_9990_.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            //player.playSoundFromEvent();
        }

    }
}

