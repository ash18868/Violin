/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.leonardforge.event.entity.living;

import net.leonard.violin.entity.custom.Herobrine;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the forge bus before an Enderman detects that a player is looking at them.
 * It will not be fired if the detection is already prevented by {@link IForgeItem#isEnderMask}
 * <p>
 * This event is {@link Cancelable}.
 * If this event is canceled, the Enderman will not target the player.
 * <p>
 * This event does not have a {@link Event.Result}.
 */
@Cancelable
public class HerobrineAngerEvent extends LivingEvent
{
    private final Player player;

    public HerobrineAngerEvent(Herobrine herobrine, Player player)
    {
        super(herobrine);
        this.player = player;
    }

    /**
     * The player that is being checked.
     */
    public Player getPlayer()
    {
        return player;
    }

    @Override
    public EnderMan getEntity()
    {
        return (EnderMan) super.getEntity();
    }
}
