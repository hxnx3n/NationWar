package io.github.bindglam.nationwar.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public final class ItemBuilder {
    private final ItemStack itemStack;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder displayName(Component name) {
        itemStack.editMeta(meta -> meta.displayName(name));
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        itemStack.editMeta(meta -> meta.lore(lore));
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        return lore(Arrays.stream(lore).toList());
    }

    public <P, C> ItemBuilder persistentData(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        itemStack.editMeta(meta -> meta.getPersistentDataContainer().set(key, type, value));
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

    public static ItemBuilder of(ItemStack base) {
        return new ItemBuilder(base);
    }

    public static ItemBuilder of(Material material) {
        return of(new ItemStack(material));
    }
}
