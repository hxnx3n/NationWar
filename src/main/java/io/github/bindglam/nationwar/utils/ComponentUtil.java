package io.github.bindglam.nationwar.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public final class ComponentUtil {
    private ComponentUtil() {
    }

    public static @NotNull Component componentNonItalic(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }
}
