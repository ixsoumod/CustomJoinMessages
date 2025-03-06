package fr.ixsou.customJoinmessages;

import fr.ixsou.customJoinmessages.commands.JoinMessagesCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomJoinMessages extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("joinmessages").setExecutor(new JoinMessagesCommand(this));

        getLogger().info(getMessage("plugin-enabled"));
    }

    @Override
    public void onDisable() {
        getLogger().info(getMessage("plugin-disabled"));
        saveDefaultConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = getConfig();

        String uuid = player.getUniqueId().toString();
        String selectedTag = config.getString("players." + uuid, null);

        if (selectedTag == null) return; // Aucune sélection trouvée

        ConfigurationSection messageConfig = config.getConfigurationSection("join-messages." + selectedTag);
        if (messageConfig == null) return;

        // Vérification de la permission
        String permission = messageConfig.getString("permission", "");
        if (!permission.isEmpty() && !player.hasPermission(permission)) {
            return;
        }

        // Récupération du message
        String joinMessage = messageConfig.getString("join-message", "&e[PLAYER] a rejoint le serveur !");
        joinMessage = ChatColor.translateAlternateColorCodes('&', joinMessage.replace("[PLAYER]", player.getName()));

        // Appliquer le message
        event.setJoinMessage(joinMessage);

        // Debug dans la console
        getLogger().info("Message de connexion appliqué à " + player.getName() + ": " + joinMessage);
    }


    public String getMessage(String path) {
        if (path.equals("no-found-message")) {
            return ChatColor.translateAlternateColorCodes('&', "&cMessage introuvable !");
        }

        Object messageObject = getConfig().get("messages." + path);

        if (messageObject instanceof String) {
            return ChatColor.translateAlternateColorCodes('&', (String) messageObject);
        }

        return getMessage("no-found-message");
    }
}
