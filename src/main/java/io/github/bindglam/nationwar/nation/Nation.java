package io.github.bindglam.nationwar.nation;

import io.github.bindglam.nationwar.NationWar;
import io.github.bindglam.nationwar.core.Core;
import io.github.bindglam.nationwar.utils.Named;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public final class Nation implements Named {
    private final String name;
    private final UUID owner;
    private final Set<Core> ownedCores = new HashSet<>(); // getOwnedCores()로 직접 수정 X (데이터 로드시에만 직접 수정 가능)
    private final Set<UUID> members = new LinkedHashSet<>(); // getMembers()로 직접 수정 X (데이터 로드시에만 직접 수정 가능)

    public Nation(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members.add(owner);
    }

    public boolean occupyCore(Player whoOccupied, Core core) {
        if(core.getOwnerNation() != null && core.getOwnerNation().getName().equals(name))
            return false; // 이미 점령함

        core.setOwnerNation(this);
        ownedCores.add(core);

        getOnlineMembers().forEach(member -> {
            // TODO : 좀 더 화려한 이펙트
            // TODO : 디스코드 알림
            member.showTitle(Title.title(
                    Component.text("<< ").color(NamedTextColor.AQUA)
                            .append(Component.text(whoOccupied.getName() + "님께서 " + core.getName() + " 신상을 점령하셨습니다 >>").color(NamedTextColor.WHITE))
                            .append(Component.text(" >>").color(NamedTextColor.AQUA)),
                    Component.empty()
            ));
        });

        return true;
    }

    public boolean addMember(UUID memberUUID) {
        if(members.contains(memberUUID))
            return false;
        if(NationWar.get().getNationManager().getNations().values().stream().anyMatch(other -> other.getMembers().contains(memberUUID)))
            return false;

        // TODO : 멤버 추가 이펙트
        members.add(memberUUID);
        return true;
    }

    public boolean removeMember(UUID memberUUID) {
        if(!members.contains(memberUUID))
            return false;

        // TODO : 멤버 삭제 이펙트
        members.remove(memberUUID);
        return true;
    }

    public @Unmodifiable Set<Player> getOnlineMembers() {
        return members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
