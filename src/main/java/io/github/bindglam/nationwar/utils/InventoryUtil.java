package io.github.bindglam.nationwar.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.joml.Vector2i;

import java.util.UUID;

public final class InventoryUtil {
    private InventoryUtil() {
    }

    public static int toSlotIndex(int x, int y) {
        return y * 9 + x;
    }

    public static Vector2i toSlotPos(int index) {
        return new Vector2i(index % 9, index / 9);
    }

    public static ItemStack createPlayerHead(OfflinePlayer owner) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(owner);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createPlayerHead(String textures) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        itemStack.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", textures));
            meta.setPlayerProfile(profile);
        });
        return itemStack;
    }
}
