package org.Cry_Legende.sHStaffAlert;

import org.Cry_Legende.sHStaffAlert.network.AlertNetwork;
import org.Cry_Legende.sHStaffAlert.network.ShNetworkAlerts;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SHStaffAlert extends JavaPlugin {

    private volatile Messages messages;
    /** Les autres serveurs du réseau ; null sans SHNetwork. */
    private AlertNetwork network;

    @Override
    public void onEnable() {
        // Charger ou générer le config.yml
        saveDefaultConfig();
        // Crée messages.yml au premier démarrage, avec les textes de l'ancienne section messages: de config.yml
        Messages.prepare(this);
        try {
            messages = Messages.load(this);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().warning("messages.yml illisible, textes du jar utilisés : " + e.getMessage());
            messages = Messages.defaults(this);
        }
        enableNetwork();

        // Enregistrer les commandes : /staffalert envoie l'alerte, /shstaffalert fait de même (ancienne forme) et reload
        StaffAlertCommand executor = new StaffAlertCommand();
        this.getCommand("staffalert").setExecutor(executor);
        this.getCommand("shstaffalert").setExecutor(executor);
        this.getCommand("shstaffalert").setTabCompleter(executor);
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
        if (!getConfig().getBoolean("reseau.actif", true)) return;
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
        if (getConfig().getBoolean("settings.log_to_console", false)) {
            getLogger().info(message);
        }
    }

    /**
     * Relit config.yml et messages.yml. Les deux sont lus à part et ne remplacent l'état en cours que s'ils sont
     * valides ; sinon l'ancienne configuration reste active et le message donne le fichier et l'erreur.
     */
    private void reload(CommandSender sender) {
        String file = "config.yml";
        try {
            new YamlConfiguration().load(new File(getDataFolder(), file));
            file = "messages.yml";
            Messages loaded = Messages.load(this);
            reloadConfig();
            messages = loaded;
            send(sender, "reloaded");
        } catch (IOException | InvalidConfigurationException e) {
            String error = e.getMessage() == null ? e.toString() : e.getMessage().lines().findFirst().orElse("");
            getLogger().warning(file + " illisible, l'ancienne configuration reste active : " + error);
            send(sender, "reload-failed", "file", file, "error", error);
        }
    }

    private void send(CommandSender sender, String key, String... values) {
        String text = messages.get(key, values);
        if (text != null) {
            sender.sendMessage(text);
        }
    }

    /**
     * /staffalert &lt;message&gt; et /shstaffalert &lt;message&gt; envoient l'alerte ; /shstaffalert reload (un seul
     * argument) recharge la configuration pour qui en a le droit.
     */
    public class StaffAlertCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
                return true; // Seuls les joueurs ou la console peuvent exécuter
            }

            boolean root = command.getName().equalsIgnoreCase("shstaffalert");
            if (root && args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("shstaffalert.admin.reload")) {
                reload(sender);
                return true;
            }

            boolean canSend = sender.hasPermission("shstaffalert.use");
            if (args.length == 0) {
                // Une ligne d'usage qui ne liste que ce que l'expéditeur peut faire
                List<String> usage = new ArrayList<>();
                if (canSend) {
                    usage.add("/staffalert <message>");
                }
                if (root && sender.hasPermission("shstaffalert.admin.reload")) {
                    usage.add("/shstaffalert reload");
                }
                if (usage.isEmpty()) {
                    send(sender, "no-permission");
                } else {
                    send(sender, "usage", "commands", String.join(" | ", usage));
                }
                return true;
            }
            if (!canSend) {
                send(sender, "no-permission");
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

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1 && sender.hasPermission("shstaffalert.admin.reload")) {
                StringUtil.copyPartialMatches(args[0], List.of("reload"), completions);
            }
            return completions;
        }
    }
}
