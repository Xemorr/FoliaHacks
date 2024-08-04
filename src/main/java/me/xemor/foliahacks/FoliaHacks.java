package me.xemor.foliahacks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.commands.CommandRegistration;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class FoliaHacks implements Listener {

    private MorePaperLib morePaperLib;
    private Method entityIsOwnedByCurrentRegion;
    private Method locationIsOwnedByCurrentRegion;

    public FoliaHacks(JavaPlugin plugin) {
        morePaperLib = new MorePaperLib(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        try {
            entityIsOwnedByCurrentRegion = Bukkit.class.getMethod("isOwnedByCurrentRegion", Entity.class);
            locationIsOwnedByCurrentRegion = Bukkit.class.getMethod("isOwnedByCurrentRegion", Entity.class);
        } catch (NoSuchMethodException e) {
            entityIsOwnedByCurrentRegion = null;
            locationIsOwnedByCurrentRegion = null;
        }
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

    /**
     * This method runs the code immediately if it is able to do so safely,
     * @param entity - the entity to check ownership of
     * @param r - the code to run
     */
    public <T> CompletableFuture<T> runASAP(Entity entity, Supplier<T> r) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (entityIsOwnedByCurrentRegion != null) {
            try {
                if ((boolean) entityIsOwnedByCurrentRegion.invoke(entity)) {
                    future.complete(r.get());
                }
                else {
                    getScheduling().entitySpecificScheduler(entity).run(() -> {
                        future.complete(r.get());
                    }, () -> {});
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            future.complete(r.get());
        }
        return future;
    }

    public CompletableFuture<Boolean> runASAP(Entity entity, CompletableFutureSupplier r) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (locationIsOwnedByCurrentRegion != null) {
            try {
                if ((boolean) locationIsOwnedByCurrentRegion.invoke(entity)) {
                    return r.get();
                }
                else {
                    getScheduling().entitySpecificScheduler(entity).run(() -> {
                        r.get().thenAccept(future::complete);
                    }, () -> {});
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            r.get().thenAccept(future::complete);
        }
        return future;
    }

    public CompletableFuture<Boolean> runASAP(Location location, CompletableFutureSupplier r) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (locationIsOwnedByCurrentRegion != null) {
            try {
                if ((boolean) locationIsOwnedByCurrentRegion.invoke(location)) {
                    return r.get();
                }
                else {
                    getScheduling().regionSpecificScheduler(location).run(() -> {
                        r.get().thenAccept(future::complete);
                    });
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            r.get().thenAccept(future::complete);
        }
        return future;
    }


    /**
     * This method runs the code immediately if it is able to do so safely,
     * @param location - the location to check ownership of
     * @param r - the code to run
     */
    public <T> CompletableFuture<T> runASAP(Location location, Supplier<T> r) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (locationIsOwnedByCurrentRegion != null) {
            try {
                if ((boolean) locationIsOwnedByCurrentRegion.invoke(location)) {
                    future.complete(r.get());
                }
                else {
                    getScheduling().regionSpecificScheduler(location).run(() -> {
                        future.complete(r.get());
                    });
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            future.complete(r.get());
        }
        return future;
    }

    /**
     * This method runs the code immediately if it is able to do so safely,
     * @param entity - the entity to check ownership of
     * @param r - the code to run
     */
    public CompletableFuture<Boolean> runASAP(Entity entity, Runnable r) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (entityIsOwnedByCurrentRegion != null) {
            try {
                if ((boolean) entityIsOwnedByCurrentRegion.invoke(entity)) {
                    r.run();
                    future.complete(true);
                }
                else {
                    getScheduling().entitySpecificScheduler(entity).run(() -> {
                        r.run();
                        future.complete(false);
                    }, () -> {});
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            r.run();
            future.complete(true);
        }
        return future;
    }

    /**
     * This method runs the code immediately if it is able to do so safely,
     * @param location - the location to check ownership of
     * @param r - the code to run
     */
    public CompletableFuture<Boolean> runASAP(Location location, Runnable r) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (locationIsOwnedByCurrentRegion != null) {
            try {
                if ((boolean) locationIsOwnedByCurrentRegion.invoke(location)) {
                    r.run();
                    future.complete(true);
                }
                else {
                    getScheduling().regionSpecificScheduler(location).run(r);
                    future.complete(false);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            r.run();
            future.complete(true);
        }
        return future;
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

    public interface CompletableFutureSupplier {
        CompletableFuture<Boolean> get();
    }
}
