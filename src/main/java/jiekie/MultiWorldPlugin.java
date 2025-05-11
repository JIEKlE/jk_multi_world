package jiekie;

import jiekie.api.MultiWorldAPI;
import jiekie.command.MultiWorldCommand;
import jiekie.completer.MultiWorldTabCompleter;
import jiekie.manager.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MultiWorldPlugin extends JavaPlugin {
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        // config
        saveDefaultConfig();
        reloadConfig();

        // manager
        worldManager = new WorldManager(this);
        worldManager.load();

        // command
        getCommand("월드").setExecutor(new MultiWorldCommand(this));

        // tab completer
        getCommand("월드").setTabCompleter(new MultiWorldTabCompleter(this));

        // api
        MultiWorldAPI.initialize(worldManager);

        getLogger().info("월드 설정 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    @Override
    public void onDisable() {
        worldManager.save();
    }
}
