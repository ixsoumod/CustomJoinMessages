package fr.ixsou.customJoinmessages.tabcompleter;

import fr.ixsou.customJoinmessages.CustomJoinMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomJoinMessagesTabCompleter implements TabCompleter {

    private final CustomJoinMessages plugin;

    public CustomJoinMessagesTabCompleter(CustomJoinMessages plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Propose "add" et "remove"
            completions.add("add");     // Ajouter un message de bienvenue
            completions.add("remove");  // Supprimer un message de bienvenue
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                // Aide : Nom du tag à créer
                completions.add("<nom_du_tag>");
            } else if (args[0].equalsIgnoreCase("remove")) {
                // Liste des tags existants pour suppression
                ConfigurationSection section = plugin.getConfig().getConfigurationSection("join-messages");
                if (section != null) {
                    Set<String> tags = section.getKeys(false);
                    completions.addAll(tags);
                }
            }
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            // Aide : Nom affiché
            completions.add("<display-name>");
        }
        else if (args.length == 4 && args[0].equalsIgnoreCase("add")) {
            // Aide : Message de bienvenue entre guillemets
            completions.add("\"Bienvenue %player% sur Oteria !\"");
        }
        else if (args.length == 5 && args[0].equalsIgnoreCase("add")) {
            // Aide : Permission nécessaire
            completions.add("customjoin.default");
            completions.add("customjoin.vip");
            completions.add("customjoin.staff");
        }

        return completions;
    }
}
