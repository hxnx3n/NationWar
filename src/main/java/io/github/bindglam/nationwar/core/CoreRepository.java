package io.github.bindglam.nationwar.core;

import io.github.bindglam.nationwar.NationWarPlugin;
import io.github.bindglam.nationwar.database.DatabaseManager;
import io.github.bindglam.nationwar.database.Repository;
import io.github.bindglam.nationwar.utils.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class CoreRepository implements Repository<Core> {
    public static final String TABLE_NAME = "cores";

    private final NationWarPlugin plugin;
    private final DatabaseManager databaseManager;

    public CoreRepository(NationWarPlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    @Override
    public @NotNull CompletableFuture<Void> createTable() {
        return CompletableFuture.runAsync(() -> {
            databaseManager.getSqlDatabase().getResource(connection -> {
                try(Statement statement = connection.createStatement()){
                    statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                            "core_name TEXT PRIMARY KEY," +
                            "world_uuid VARCHAR(36)," +
                            "x DOUBLE," +
                            "y DOUBLE," +
                            "z DOUBLE," +
                            "health INTEGER," +
                            "max_health INTEGER," +
                            "owner_nation TEXT NULL" +
                            ")");
                }
            });
        });
    }

    private @NotNull Core load(ResultSet result) throws SQLException {
        Core core = new Core(
                result.getString("core_name"),
                new Location(
                        Bukkit.getWorld(UUID.fromString(result.getString("world_uuid"))),
                        result.getDouble("x"),
                        result.getDouble("y"),
                        result.getDouble("z")
                ),
                result.getInt("health"),
                result.getInt("max_health"),
                ObjectUtil.mapNullable(result.getString("owner_nation"), plugin.getNationManager()::getNation)
        );

        if(core.getOwnerNation() != null)
            core.getOwnerNation().getOwnedCores().add(core);

        return core;
    }

    @Override
    public @NotNull CompletableFuture<Optional<Core>> load(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Core[] core = new Core[] { null }; // 💀
            databaseManager.getSqlDatabase().getResource(connection -> {
                try(PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM " + TABLE_NAME + " WHERE core_name = ?"
                )) {
                    try(ResultSet result = statement.executeQuery()) {
                        if(result.next()) {
                            core[0] = load(result);
                        }
                    }
                }
            });
            return Optional.ofNullable(core[0]);
        });
    }

    @Override
    public @NotNull CompletableFuture<@Unmodifiable Map<String, Core>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Core> cores = new HashMap<>();
            databaseManager.getSqlDatabase().getResource(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM " + TABLE_NAME
                )) {
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            Core core = load(result);
                            cores.put(core.getName(), core);
                        }
                    }
                }
            });
            return cores;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> save(Core core) {
        return CompletableFuture.runAsync(() -> {
            databaseManager.getSqlDatabase().getResource(connection -> {
                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR REPLACE INTO " + TABLE_NAME +  " (core_name, world_uuid, x, y, z, health, max_health, owner_nation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                )) {
                    statement.setString(1, core.getName());
                    statement.setString(2, core.getLocation().getWorld().getUID().toString());
                    statement.setDouble(3, core.getLocation().getX());
                    statement.setDouble(4, core.getLocation().getY());
                    statement.setDouble(5, core.getLocation().getZ());
                    statement.setInt(6, core.getHealth());
                    statement.setInt(7, core.getMaxHealth());
                    if(core.getOwnerNation() == null)
                        statement.setNull(8, Types.VARCHAR);
                    else
                        statement.setString(8, core.getOwnerNation().getName());
                    statement.executeUpdate();
                }
            });
        });
    }
}
