package io.github.bindglam.nationwar.guis;

import io.github.bindglam.nationwar.guis.drawing.Canvas;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public abstract class InventoryGui implements InventoryHolder, Listener {
    private final JavaPlugin plugin;

    protected final Inventory inventory;
    protected final Canvas canvas;
    protected final SlotDataContainer dataContainer;

    private final ScheduledTask tickTask;

    private final Set<UUID> viewers = new HashSet<>();

    public InventoryGui(JavaPlugin plugin, int size, Component title, int tickInterval) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, size, title);
        this.canvas = new Canvas(this.inventory);
        this.dataContainer = new SlotDataContainer(size);

        if(tickInterval > 0)
            this.tickTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> this.onTick(), 0L, tickInterval*50L, TimeUnit.MILLISECONDS);
        else
            this.tickTask = null;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public InventoryGui(JavaPlugin plugin, int size, Component title) {
        this(plugin, size, title, 0);
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onTick() {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    @EventHandler
    public final void onInventoryOpenEvent(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if(!isMe(inv)) return;

        viewers.add(player.getUniqueId());

        onOpen(event);
    }

    @EventHandler
    public final void onInventoryClickEvent(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if(!isMe(inv)) return;
        onClick(event);
    }

    @EventHandler
    public final void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if(!isMe(inv)) return;

        onClose(event);

        viewers.remove(player.getUniqueId());

        // 관측하는 플레이어가 없을 시, dispose
        if(viewers.isEmpty()) {
            if (tickTask != null)
                tickTask.cancel();

            // 아이템 쌔비기 버그 방지
            player.getScheduler().runDelayed(plugin, (task) -> player.updateInventory(), null, 1L);
            //Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::updateInventory, 1L);

            HandlerList.unregisterAll(this);
        }
    }

    private boolean isMe(Inventory other) {
        return other.getHolder(false) == this;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
