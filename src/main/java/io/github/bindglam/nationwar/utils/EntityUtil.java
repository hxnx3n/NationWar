package io.github.bindglam.nationwar.utils;

import io.github.bindglam.nationwar.NationWar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public final class EntityUtil {
    private EntityUtil() {
    }

    public static void teleportTimed(Player player, Location location, int seconds, Consumer<Integer> onTick, Runnable onCancelled, Runnable onDone) {
        Location initialLocation = player.getLocation();
        boolean[] isCancelled = new boolean[] { false };

        for(int i = 0; i < seconds; i++) {
            int finalI = i;
            Bukkit.getAsyncScheduler().runDelayed(NationWar.get(), task -> {
                if(isCancelled[0]) return;
                if(!initialLocation.getWorld().getUID().equals(player.getLocation().getWorld().getUID())
                        || Double.compare(initialLocation.getX(), player.getLocation().getX()) != 0 || Double.compare(initialLocation.getY(), player.getLocation().getY()) != 0 || Double.compare(initialLocation.getZ(), player.getLocation().getZ()) != 0) {
                    isCancelled[0] = true;
                    onCancelled.run();
                    return;
                }
                onTick.accept(seconds - finalI);
            }, i, TimeUnit.SECONDS);
        }
        Bukkit.getAsyncScheduler().runDelayed(NationWar.get(), task -> {
            if(isCancelled[0]) return;
            player.teleportAsync(location);
            onDone.run();
        }, seconds, TimeUnit.SECONDS);
    }
}
