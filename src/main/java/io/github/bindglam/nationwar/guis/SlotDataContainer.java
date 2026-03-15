package io.github.bindglam.nationwar.guis;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SlotDataContainer {
    private final int size;

    // AsyncScheduler로 tick task가 작동하니 ConcurrentHashMap이 필수일 수 밖에 없음.
    // TODO : 메모리 테스트
    private final Map<String, Object>[] slotData;
    //private final Map<Integer, Map<String, Object>> slotData = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    SlotDataContainer(int size) {
        this.size = size;
        this.slotData = new ConcurrentHashMap[size];
        for (int i = 0; i < size; i++) {
            this.slotData[i] = new ConcurrentHashMap<>();
        }
    }

    public void put(int slot, String key, Object value) {
        validateSlot(slot);
        slotData[slot].put(key, value);
    }

    public @Nullable Object get(int slot, String key) {
        validateSlot(slot);
        return slotData[slot].get(key);
    }

    public void remove(int slot, String key) {
        validateSlot(slot);
        slotData[slot].remove(key);
    }

    private void validateSlot(int slot) {
        if(slot < 0 || slot >= size)
            throw new IllegalArgumentException("Invalid slot index");
    }
}
