package fr.ixsou.customJoinmessages.inventorys;

import fr.ixsou.customJoinmessages.CustomJoinMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class JoinMessageInventory implements Listener {
    private final CustomJoinMessages plugin;

    public JoinMessageInventory(CustomJoinMessages plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Messages de Connexion");

        ConfigurationSection messagesSection = plugin.getConfig().getConfigurationSection("join-messages");

        if (messagesSection == null) {
            player.sendMessage(ChatColor.RED + "Aucun message de connexion trouvé !");
            return;
        }

        int slot = 0;
        for (String key : messagesSection.getKeys(false)) {
            if (slot >= 27) break;

            ConfigurationSection messageConfig = messagesSection.getConfigurationSection(key);
            if (messageConfig == null) continue;

            // Vérification de la permission
            String permission = messageConfig.getString("permission", "");
            if (!permission.isEmpty() && !player.hasPermission(permission)) {
                continue;
            }

            // Création du Name Tag
            ItemStack nameTag = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = nameTag.getItemMeta();
            if (meta != null) {
                String displayName = messageConfig.getString("display-name", "&fName Tag");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

                List<String> lore = new ArrayList<>(messageConfig.getStringList("lore"));
                lore.add(ChatColor.YELLOW + "▶ Cliquez pour sélectionner !");
                meta.setLore(lore);

                nameTag.setItemMeta(meta);
            }

            inventory.setItem(slot, nameTag);
            slot++;
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(ChatColor.GREEN + "Messages de Connexion")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() != Material.NAME_TAG) return;

            String selectedTag = getTagFromItem(clickedItem);
            if (selectedTag == null) {
                player.sendMessage(ChatColor.RED + "Erreur: Impossible de récupérer le tag !");
                return;
            }

            ConfigurationSection messageConfig = plugin.getConfig().getConfigurationSection("join-messages." + selectedTag);
            if (messageConfig == null) {
                player.sendMessage(ChatColor.RED + "Erreur: Ce tag n'existe pas dans la config !");
                return;
            }

            String permission = messageConfig.getString("permission", "");
            if (!permission.isEmpty() && !player.hasPermission(permission)) {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission pour ce message !");
                return;
            }

            // Mise à jour du config.yml avec le bon tag
            plugin.getConfig().set("players." + player.getUniqueId().toString(), selectedTag);
            plugin.saveConfig();

            player.sendMessage(ChatColor.GREEN + "Votre message de connexion a été mis à jour avec: " + ChatColor.YELLOW + selectedTag);
            player.closeInventory();
        }
    }


    private String getTagFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return null;

        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        ConfigurationSection messagesSection = plugin.getConfig().getConfigurationSection("join-messages");

        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                ConfigurationSection messageConfig = messagesSection.getConfigurationSection(key);
                if (messageConfig != null) {
                    String configName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', messageConfig.getString("display-name", "&fName Tag")));
                    if (configName.equals(displayName)) {
                        return key;
                    }
                }
            }
        }
        return null;
    }

}
