package io.github.bindglam.nationwar.command;

import io.github.bindglam.nationwar.Context;
import io.github.bindglam.nationwar.core.Core;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;

public final class CoreCommand implements CommandRegistrar {
    @Override
    public void register(Context context, BukkitCommandManager<CommandSender> commands) {
        commands.command(commands.commandBuilder("신상")
                .permission(Permission.of("nationwar.admin"))
                .literal("생성")
                .required("이름", StringParser.quotedStringParser())
                .required("위치", LocationParser.locationParser())
                .optional("최대체력", IntegerParser.integerParser(1))
                .handler(ctx -> {
                    String name = ctx.get("이름");
                    Location location = ctx.get("위치");
                    int maxHealth = ctx.getOrDefault("최대체력", context.config().core.defaultMaxHealth.value());

                    Core core = new Core(name ,location, maxHealth);
                    if(!context.plugin().getCoreManager().registerCore(core)) {
                        ctx.sender().sendMessage(Component.text("신상을 등록하지 못했습니다.").color(NamedTextColor.RED));
                        ctx.sender().sendMessage(Component.text("이미 등록된 신상인지 확인해주세요!").color(NamedTextColor.RED));
                    } else {
                        ctx.sender().sendMessage(Component.text("성공적으로 '" + name + "' 신상을 등록했습니다.").color(NamedTextColor.GREEN));
                    }
                }));
    }
}
