package me.xemor.foliahacks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.commands.CommandRegistration;
import space.arim.morepaperlib.scheduling.GracefulScheduling;
import space.arim.morepaperlib.scheduling.ScheduledTask;

public final class FoliaHacks implements Listener {

    private MorePaperLib morePaperLib;

    public FoliaHacks(JavaPlugin plugin) {
        morePaperLib = new MorePaperLib(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        getScheduling().entitySpecificScheduler(e.getEntity()).runAtFixedRate((task) -> {
            if (!e.getEntity().isDead()) {
                new PlayerPostRespawnFoliaEvent(e.getEntity()).callEvent();
                task.cancel();
            }
        }, () -> {}, 1L, 1L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isDead()) {
            getScheduling().entitySpecificScheduler(e.getPlayer()).runAtFixedRate((task) -> {
                if (!e.getPlayer().isDead()) {
                    new PlayerPostRespawnFoliaEvent(e.getPlayer()).callEvent();
                    task.cancel();
                }
            }, () -> {}, 1L, 1L);
        }
    }

    public GracefulScheduling getScheduling() {
        return morePaperLib.scheduling();
    }

    public CommandRegistration getCommandRegistration() {
        return morePaperLib.commandRegistration();
    }

    public MorePaperLib getMorePaperLib() {
        return morePaperLib;
    }
}
