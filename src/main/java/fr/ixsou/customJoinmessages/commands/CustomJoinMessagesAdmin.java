package fr.ixsou.customJoinmessages.commands;

import fr.ixsou.customJoinmessages.CustomJoinMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

public class CustomJoinMessagesAdmin implements CommandExecutor {

    private final CustomJoinMessages plugin;

    public CustomJoinMessagesAdmin(CustomJoinMessages plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        // Vérification si la commande est exécutée par un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        Player player = (Player) sender;

        // Vérification des arguments
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("unknown-command"));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 5) {
                player.sendMessage(plugin.getMessage("invalid-arguments"));
                return true;
            }

            String tag = args[1];
            String displayName = args[2];
            String permission = args[args.length - 1];

            // Gestion des guillemets pour le message de bienvenue
            String joinMessage;
            if (args[3].startsWith("\"") && args[args.length - 2].endsWith("\"")) {
                joinMessage = String.join(" ", Arrays.copyOfRange(args, 3, args.length - 1));
                joinMessage = joinMessage.substring(1, joinMessage.length() - 1); // Suppression des guillemets
            } else {
                joinMessage = args[3];
            }

            // Remplacement de %player% par [PLAYER]
            joinMessage = joinMessage.replace("%player%", "[PLAYER]");

            // Enregistrement dans la configuration
            plugin.getConfig().set("join-messages." + tag + ".display-name", displayName);
            plugin.getConfig().set("join-messages." + tag + ".join-message", joinMessage);
            plugin.getConfig().set("join-messages." + tag + ".lore", "[]");
            plugin.getConfig().set("join-messages." + tag + ".permission", permission);
            plugin.saveConfig();

            player.sendMessage(plugin.getMessage("tag-created").replace("%tag%", tag));
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                player.sendMessage(plugin.getMessage("invalid-arguments"));
                return true;
            }

            if (plugin.getConfig().getConfigurationSection("join-messages." + args[1]) == null) {
                player.sendMessage(plugin.getMessage("tag-not-found").replace("%tag%", args[1]));
                return true;
            }

            plugin.getConfig().set("join-messages." + args[1], null);
            plugin.saveConfig();
            player.sendMessage(plugin.getMessage("tag-removed").replace("%tag%", args[1]));
            return true;
        }

        return true;
    }
}
