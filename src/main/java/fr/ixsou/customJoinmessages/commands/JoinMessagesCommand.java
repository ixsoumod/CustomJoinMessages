package fr.ixsou.customJoinmessages.commands;

import fr.ixsou.customJoinmessages.inventorys.JoinMessageInventory;
import fr.ixsou.customJoinmessages.CustomJoinMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinMessagesCommand implements CommandExecutor {
    private final JoinMessageInventory inventory;

    public JoinMessagesCommand(CustomJoinMessages plugin) {
        this.inventory = new JoinMessageInventory(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }
        Player player = (Player) sender;
        inventory.openInventory(player);
        return true;
    }
}
