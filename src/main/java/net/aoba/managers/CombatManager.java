/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers;

import net.aoba.Aoba;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.events.TotemPopEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.event.listeners.TickListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static net.aoba.AobaClient.MC;

public class CombatManager implements TickListener, ReceivePacketListener {
    public HashMap<String, Integer> popList = new HashMap<>();

    public CombatManager() {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
        Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
    }

    @Override
    public void onReceivePacket(ReceivePacketEvent event) {
        if (event.GetPacket() instanceof EntityStatusS2CPacket entityStatusS2CPacket) {
            if (entityStatusS2CPacket.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
                Entity entity = entityStatusS2CPacket.getEntity(MC.world);

                if (!(entity instanceof PlayerEntity)) return;

                if (popList == null) {
                    popList = new HashMap<>();
                }

                if (popList.get(entity.getName().getString()) == null) {
                    popList.put(entity.getName().getString(), 1);
                } else if (popList.get(entity.getName().getString()) != null) {
                    popList.put(entity.getName().getString(), popList.get(entity.getName().getString()) + 1);
                }

                Aoba.getInstance().eventManager.Fire(new TotemPopEvent((PlayerEntity) entity, popList.get(entity.getName().getString())));
            }
        }
    }

    @Override
    public void onTick(TickEvent.Pre event) {

    }
    
    @Override
    public void onTick(TickEvent.Post event) {
        for (PlayerEntity player : MC.world.getPlayers()) {
            if (player.getHealth() <= 0 && popList.containsKey(player.getName().getString()))
                popList.remove(player.getName().getString(), popList.get(player.getName().getString()));
        }
    }

    public int getPops(@NotNull PlayerEntity entity) {
        if (popList.get(entity.getName().getString()) == null) return 0;
        return popList.get(entity.getName().getString());
    }
}