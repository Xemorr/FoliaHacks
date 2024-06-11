package me.xemor.foliahacks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPostRespawnFoliaEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;

    public PlayerPostRespawnFoliaEvent(Player player) {
        this.player = player;
    }

    public void callEvent() {
        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
