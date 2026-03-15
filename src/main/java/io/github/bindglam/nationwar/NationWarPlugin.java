package io.github.bindglam.nationwar;

import io.github.bindglam.nationwar.command.CommandManager;
import io.github.bindglam.nationwar.database.DatabaseManager;
import io.github.bindglam.nationwar.nation.NationManager;
import io.github.bindglam.nationwar.core.CoreManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NationWarPlugin extends JavaPlugin {
    @Getter
    @Accessors(fluent = true)
    private NationWarConfig config;

    @Getter
    private final DatabaseManager databaseManager = new DatabaseManager();
    @Getter
    private final NationManager nationManager = new NationManager();
    @Getter
    private final CoreManager coreManager = new CoreManager();
    private final CommandManager commandManager = new CommandManager();

    @Override
    public void onEnable() {
        NationWar.register(this);

        config = new NationWarConfig(new File(getDataFolder(), "config.yml"));
        config.load();

        Context context = createContext();

        databaseManager.start(context);
        nationManager.start(context);
        coreManager.start(context);
        commandManager.start(context);
    }

    @Override
    public void onDisable() {
        NationWar.unregister();

        Context context = createContext();

        coreManager.end(context);
        nationManager.end(context);
        databaseManager.end(context);
        commandManager.end(context);
    }

    private Context createContext() {
        return new Context(getLogger(), this, config);
    }
}
