package fr.ixsou.customJoinmessages;

import fr.ixsou.customJoinmessages.listeners.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomJoinMessages extends JavaPlugin {

    @Override
    public void onEnable() {

        // Registering the event listener
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
