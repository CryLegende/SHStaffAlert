package org.Cry_Legende.sHStaffAlert;

import org.Cry_Legende.sHStaffAlert.network.AlertNetwork;
import org.Cry_Legende.sHStaffAlert.network.ShNetworkAlerts;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SHStaffAlert extends JavaPlugin {

    private FileConfiguration config;
    private Messages messages;
    /** Les autres serveurs du réseau ; null sans SHNetwork. */
    private AlertNetwork network;

    @Override
    public void onEnable() {
        // Charger ou générer le config.yml
        saveDefaultConfig();
        config = getConfig();
        messages = new Messages(this);
        enableNetwork();

        // Enregistrer la commande
        this.getCommand("shstaffalert").setExecutor(new StaffAlertCommand());
        getLogger().info("SHStaffAlert est activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("SHStaffAlert est désactivé !");
    }

    /**
     * Passe en mode réseau si SHNetwork est installé et 'reseau.actif' est vrai. La classe qui touche à SHNetwork
     * n'est résolue qu'ici : sans lui, rien n'est chargé et l'alerte reste locale.
     */
    private void enableNetwork() {
        if (!config.getBoolean("reseau.actif", true)) return;
        if (!Bukkit.getPluginManager().isPluginEnabled("SHNetwork")) return;
        try {
            AlertNetwork net = ShNetworkAlerts.open();
            net.onAlert(this::deliver);
            network = net;
            getLogger().info("SHNetwork détecté : les alertes atteignent le staff de tout le réseau.");
        } catch (RuntimeException | LinkageError e) {
            network = null;
            getLogger().log(Level.SEVERE, "SHNetwork présent mais inutilisable, alertes locales à ce serveur : " + e);
        }
    }

    /** Envoie le message aux staffs de ce serveur et, si le réglage est actif, l'écrit dans la console. */
    private void deliver(String message) {
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
    }

    public class StaffAlertCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
                return true; // Seuls les joueurs ou la console peuvent exécuter
            }

            if (!sender.hasPermission("shstaffalert.command")) {
                sender.sendMessage(messages.get("no-permission"));
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(messages.get("incorrect-usage"));
                return true;
            }

            // Construire le message
            String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

            deliver(message);
            if (network != null) {
                network.publish(message);
            }

            return true;
        }
    }
}
