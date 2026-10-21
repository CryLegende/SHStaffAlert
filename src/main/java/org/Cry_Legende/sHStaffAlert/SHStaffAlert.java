package org.Cry_Legende.sHStaffAlert;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SHStaffAlert extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Charger ou générer le config.yml
        saveDefaultConfig();
        config = getConfig();

        // Enregistrer la commande
        this.getCommand("shstaffalert").setExecutor(new StaffAlertCommand());
        getLogger().info("SHStaffAlert est activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("SHStaffAlert est désactivé !");
    }

    public class StaffAlertCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
                return true; // Seuls les joueurs ou la console peuvent exécuter
            }

            if (!sender.hasPermission("shstaffalert.command")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no_permission")));
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.incorrect_usage")));
                return true;
            }

            // Construire le message
            String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

            // Envoyer le message aux joueurs ayant la permission
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("shstaffalert.staff")) {
                    player.sendMessage(message);
                }
            }


            // Vérifier le paramètre pour l'envoi dans la console
            if (config.getBoolean("settings.log_to_console", false)) {
                getLogger().info(message);
            }

            return true;
        }
    }
}
