package io.github.bindglam.nationwar.command;

import io.github.bindglam.nationwar.Context;
import io.github.bindglam.nationwar.core.Core;
import io.github.bindglam.nationwar.nation.Nation;
import io.github.bindglam.nationwar.utils.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.concurrent.CompletableFuture;

public final class NationCommand implements CommandRegistrar {
    @Override
    public void register(Context context, BukkitCommandManager<CommandSender> commands) {
        commands.command(commands.commandBuilder("국가")
                .permission(Permission.of("nationwar.admin"))
                .literal("생성")
                .required("이름", StringParser.quotedStringParser())
                .required("소유자", StringParser.stringParser(), SuggestionProvider.blocking((ctx, input) -> Bukkit.getOnlinePlayers().stream().map(it -> Suggestion.suggestion(it.getName())).toList()))
                .handler(ctx -> {
                    String name = ctx.get("이름");
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(ctx.<String>get("소유자"));

                    Nation nation = new Nation(name, owner.getUniqueId());
                    if(!context.plugin().getNationManager().registerNation(nation)) {
                        ctx.sender().sendMessage(Component.text("국가를 등록하지 못했습니다.").color(NamedTextColor.RED));
                        ctx.sender().sendMessage(Component.text("이미 등록된 국가이거나 해당 국가 소유자가 다른 국가에 소속되어있는지 확인해주세요!").color(NamedTextColor.RED));
                    } else {
                        ctx.sender().sendMessage(Component.text("성공적으로 '" + name + "' 국가를 등록했습니다.").color(NamedTextColor.GREEN));
                    }
                }));
        commands.command(commands.commandBuilder("국가")
                .permission(Permission.of("nationwar.admin"))
                .literal("목록")
                .handler(ctx -> {
                    String header = "---=== 국가 목록 ===---";

                    ctx.sender().sendMessage(Component.empty());
                    ctx.sender().sendMessage(Component.text(header).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
                    ctx.sender().sendMessage(Component.empty());

                    CompletableFuture.allOf(context.plugin().getNationManager().getNations().values().stream().map(nation ->
                            PlayerUtil.getUsername(nation.getOwner()).thenAccept(ownerName -> {
                                ctx.sender().sendMessage(Component.text(nation.getName()).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
                                        .append(Component.text(" - 소유자: " + ownerName).color(NamedTextColor.WHITE)));
                            })).toList().toArray(new CompletableFuture[0])).thenRun(() -> {
                                ctx.sender().sendMessage(Component.empty());
                                ctx.sender().sendMessage(Component.text("-".repeat(header.length())).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
                                ctx.sender().sendMessage(Component.empty());
                            });
                }));

        member(context, commands);
        core(context, commands);
    }

    private void member(Context context, BukkitCommandManager<CommandSender> commands) {
        commands.command(commands.commandBuilder("국가")
                .permission(Permission.of("nationwar.admin"))
                .literal("멤버")
                .literal("목록")
                .required("국가", StringParser.quotedStringParser())
                .handler(ctx -> {
                    Nation nation = context.plugin().getNationManager().getNation(ctx.get("국가"));

                    if(nation == null) {
                        ctx.sender().sendMessage(Component.text("알 수 없는 국가입니다.").color(NamedTextColor.RED));
                        return;
                    }

                    PlayerUtil.getUsernames(nation.getMembers()).thenAccept(usernames -> {
                        String header = "---== '" + nation.getName() + "' 국가 멤버 목록 ==---";

                        ctx.sender().sendMessage(Component.empty());
                        ctx.sender().sendMessage(Component.text(header).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
                        ctx.sender().sendMessage(Component.empty());
                        usernames.forEach(memberName -> {
                            ctx.sender().sendMessage(Component.text(memberName).color(NamedTextColor.AQUA));
                        });
                        ctx.sender().sendMessage(Component.empty());
                        ctx.sender().sendMessage(Component.text("-".repeat(header.length())).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
                        ctx.sender().sendMessage(Component.empty());
                    });
                }));
        commands.command(commands.commandBuilder("국가")
                .permission(Permission.of("nationwar.admin"))
                .literal("멤버")
                .literal("추가")
                .required("국가", StringParser.quotedStringParser())
                .required("플레이어", StringParser.stringParser(), SuggestionProvider.blocking((ctx, input) -> Bukkit.getOnlinePlayers().stream().map(it -> Suggestion.suggestion(it.getName())).toList()))
                .handler(ctx -> {
                    Nation nation = context.plugin().getNationManager().getNation(ctx.get("국가"));
                    OfflinePlayer target = Bukkit.getOfflinePlayer(ctx.<String>get("플레이어"));

                    if(nation == null) {
                        ctx.sender().sendMessage(Component.text("알 수 없는 국가입니다.").color(NamedTextColor.RED));
                        return;
                    }

                    if(!nation.addMember(target.getUniqueId())) {
                        ctx.sender().sendMessage(Component.text("새로운 멤버를 추가하지 못했습니다.").color(NamedTextColor.RED));
                        ctx.sender().sendMessage(Component.text("이미 해당 국가에 소속됐거나 다른 국가에 소속된 멤버인지 확인해주세요!").color(NamedTextColor.RED));
                    } else {
                        ctx.sender().sendMessage(Component.text("성공적으로 '" + target.getName() + "'님을 '" + nation.getName() + "' 국가에 소속시켰습니다.").color(NamedTextColor.GREEN));
                    }
                }));
        commands.command(commands.commandBuilder("국가")
                .permission(Permission.of("nationwar.admin"))
                .literal("멤버")
                .literal("삭제")
                .required("국가", StringParser.quotedStringParser())
                .required("플레이어", StringParser.stringParser(), SuggestionProvider.blocking((ctx, input) -> Bukkit.getOnlinePlayers().stream().map(it -> Suggestion.suggestion(it.getName())).toList()))
                .handler(ctx -> {
                    Nation nation = context.plugin().getNationManager().getNation(ctx.get("국가"));
                    OfflinePlayer target = Bukkit.getOfflinePlayer(ctx.<String>get("플레이어"));

                    if(nation == null) {
                        ctx.sender().sendMessage(Component.text("알 수 없는 국가입니다.").color(NamedTextColor.RED));
                        return;
                    }

                    if(!nation.removeMember(target.getUniqueId())) {
                        ctx.sender().sendMessage(Component.text("해당 멤버를 삭제하지 못했습니다.").color(NamedTextColor.RED));
                        ctx.sender().sendMessage(Component.text("해당 국가에 소속된 멤버인지 확인해주세요!").color(NamedTextColor.RED));
                    } else {
                        ctx.sender().sendMessage(Component.text("성공적으로 '" + target.getName() + "'님을 '" + nation.getName() + "' 국가에서 삭제시켰습니다.").color(NamedTextColor.GREEN));
                    }
                }));
    }

    private void core(Context context, BukkitCommandManager<CommandSender> commands) {
        commands.command(commands.commandBuilder("국가")
                .permission(Permission.of("nationwar.admin"))
                .literal("신상")
                .literal("점령")
                .required("플레이어", PlayerParser.playerParser())
                .required("신상", StringParser.quotedStringParser())
                .handler(ctx -> {
                    Player whoOccupied = ctx.get("플레이어");
                    Core core = context.plugin().getCoreManager().getCore(ctx.get("신상"));
                    if(core == null) {
                        ctx.sender().sendMessage(Component.text("알 수 없는 신상입니다.").color(NamedTextColor.RED));
                        return;
                    }

                    Nation nation = context.plugin().getNationManager().getNationByMember(whoOccupied.getUniqueId());
                    if(nation == null) {
                        ctx.sender().sendMessage(Component.text("해당 플레이어는 국가에 소속되지 않았습니다.").color(NamedTextColor.RED));
                        return;
                    }

                    if(!nation.occupyCore(whoOccupied, core)) {
                        ctx.sender().sendMessage(Component.text("해당 신상을 점령하는 데 실패했습니다.").color(NamedTextColor.RED));
                    } else {
                        ctx.sender().sendMessage(Component.text("성공적으로 '" + whoOccupied.getName() + "'님이 '" + core.getName() + "' 신상을 점령하게 했습니다.").color(NamedTextColor.GREEN));
                    }
                }));
    }
}
