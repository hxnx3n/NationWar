package io.github.bindglam.nationwar.guis.drawing;

import io.github.bindglam.nationwar.utils.InventoryUtil;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class Canvas {
    private final Inventory inventory;

    public Canvas(Inventory inventory) {
        this.inventory = inventory;
    }

    public void pixel(int idx, ItemStack itemStack) {
        inventory.setItem(idx, itemStack);
    }

    public void pixel(int x, int y, ItemStack itemStack) {
        pixel(InventoryUtil.toSlotIndex(x, y), itemStack);
    }

    public void container(int x1, int y1, int x2, int y2, int page, List<ContainerItemGetter> items) {
        int w = x2-x1+1;
        int h = y2-y1+1;

        int offset = page*w*h;

        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                int idx = (y-y1)*w+(x-x1) + offset;
                if(idx >= items.size())
                    break;
                ItemStack itemStack = items.get(idx).get(InventoryUtil.toSlotIndex(x, y));

                pixel(x, y, itemStack);
            }
        }
    }

    public void boxOutline(int x1, int y1, int x2, int y2, ItemStack itemStack) {
        for (int x = x1; x <= x2; x++) {
            pixel(x, y1, itemStack);
            pixel(x, y2, itemStack);
        }
        for (int y = y1; y <= y2; y++) {
            pixel(x1, y, itemStack);
            pixel(x2, y, itemStack);
        }
    }

    public void fill(ItemStack itemStack) {
        for(int i = 0; i < inventory.getSize(); i++)
            pixel(i, itemStack);
    }
}
