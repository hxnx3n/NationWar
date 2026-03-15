package io.github.bindglam.nationwar.guis.drawing;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ContainerItemGetter {
    @NotNull ItemStack get(int slot);

    static @NotNull ContainerItemGetter plain(@NotNull ItemStack itemStack) {
        return slot -> itemStack;
    }
}
