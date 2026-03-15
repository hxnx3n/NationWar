package io.github.bindglam.nationwar.core;

import io.github.bindglam.nationwar.nation.Nation;
import io.github.bindglam.nationwar.utils.Named;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public final class Core implements Named {
    private final String name;
    private final Location location; // 신상은 서버 시작시에 설정, 고정
    private int health;
    private final int maxHealth;
    private Nation ownerNation = null; // 신상 소유 국가

    public Core(String name, Location location, int maxHealth) {
        this.name = name;
        this.location = location;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
    }

    public Core(String name, Location location, int health, int maxHealth, Nation ownerNation) {
        this.name = name;
        this.location = location;
        this.health = health;
        this.maxHealth = maxHealth;
        this.ownerNation = ownerNation;
    }
}
