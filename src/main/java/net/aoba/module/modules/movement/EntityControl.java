/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.interfaces.IHorseBaseEntity;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;

public class EntityControl extends Module implements TickListener
{
    public EntityControl()
    {
        super("EntityControl");
        setDescription("Allows you to control entities without needing a saddle.");
        setCategory(Category.of("Movement"));
    }

    @Override
    public void onDisable()
    {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);

        if (MC.world != null)
        {
            for (Entity entity : Aoba.getInstance().entityManager.getEntities())
            {
                if (entity instanceof AbstractHorseEntity)
                    ((IHorseBaseEntity) entity).setSaddled(false);
            }
        }
    }

    @Override
    public void onEnable()
    {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle()
    {

    }

    @Override
    public void onTick(Pre event)
    {

    }

    @Override
    public void onTick(Post event)
    {
        if (MC.world != null)
        {
            for (Entity entity : Aoba.getInstance().entityManager.getEntities())
            {
                if (entity instanceof AbstractHorseEntity)
                    ((IHorseBaseEntity) entity).setSaddled(true);
            }
        }
    }
}
