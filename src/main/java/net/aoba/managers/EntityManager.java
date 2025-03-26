package net.aoba.managers;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.aoba.AobaClient.MC;

public class EntityManager implements TickListener
{
    private volatile List<PlayerEntity> players;
    private volatile List<Entity> entities;
    private volatile List<BlockEntity> blockEntities;

    public EntityManager() {
        players = Collections.emptyList();
        entities = Collections.emptyList();
        blockEntities = Collections.emptyList();

        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onTick(TickEvent.Pre event)
    {
        update();
    }

    @Override
    public void onTick(TickEvent.Post event)
    {
        update();
    }

    private void update() {
        if (MC.world != null)
        {
            setLists(
                    new ArrayList<>(getEntities(false)),
                    new ArrayList<>(getPlayers(false)),
                    new ArrayList<>(getBlockEntities(false)));
        }
        else
        {
            setLists(Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());
        }
    }

    private void setLists(List<Entity> loadedEntities,
                          List<PlayerEntity> playerEntities,
                          List<BlockEntity> loadedBlockEntities)
    {
        entities = loadedEntities;
        players = playerEntities;
        blockEntities = loadedBlockEntities;
    }

    public List<Entity> getEntities()
    {
        return entities;
    }

    public List<PlayerEntity> getPlayers()
    {
        return players;
    }

    public List<BlockEntity> getBlockEntities()
    {
        return blockEntities;
    }

    public List<Entity> getEntitiesAsync()
    {
        return getEntities(!MC.isOnThread());
    }

    public List<PlayerEntity> getPlayersAsync()
    {
        return getPlayers(!MC.isOnThread());
    }

    public List<BlockEntity> getBlockEntitiesAsync()
    {
        return getBlockEntities(!MC.isOnThread());
    }

    public List<Entity> getEntities(boolean async)
    {
        if (async)
        {
            return entities;
        }
        List<Entity> entityList = new ArrayList<>();
        MC.world.getEntities().forEach(entityList::add);
        return entityList;
    }

    public List<PlayerEntity> getPlayers(boolean async)
    {
        return async ? players : new ArrayList<>(MC.world.getPlayers());
    }

    public List<BlockEntity> getBlockEntities(boolean async)
    {
        if (async)
        {
            return blockEntities;
        }

        List<BlockEntity> list = new ArrayList<>();
        int chunkDistance = Math.min(MC.options.getViewDistance().getValue(), 4);

        for (int x = -chunkDistance; x <= chunkDistance; x++)
        {
            for (int z = -chunkDistance; z <= chunkDistance; z++)
            {
                WorldChunk chunk = MC.world.getChunkManager().getWorldChunk((int) MC.player.getX() / 16 + x, (int) MC.player.getZ() / 16 + z);

                if (chunk != null)
                {
                    list.addAll(chunk.getBlockEntities().values());
                }
            }
        }
        return list;
    }

    public Entity getEntity(int id)
    {
        List<Entity> entities = getEntitiesAsync();
        if (entities != null)
        {
            return entities.stream()
                    .filter(e -> e != null && e.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
