package org.Cry_Legende.sHStaffAlert;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Textes de messages.yml, codes couleur &amp;. Une clé absente du fichier prend le texte du jar, une clé vide
 * désactive le message.
 */
final class Messages {

    private final Map<String, String> texts;

    private Messages(Map<String, String> texts) {
        this.texts = texts;
    }

    /**
     * Crée messages.yml s'il n'existe pas : le fichier du jar, dont les textes sont remplacés par ceux de l'ancienne
     * section messages: de config.yml quand elle existe (clés no_permission et incorrect_usage).
     */
    static void prepare(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (file.exists()) {
            return;
        }
        plugin.saveResource("messages.yml", false);
        ConfigurationSection legacy = plugin.getConfig().getConfigurationSection("messages");
        if (legacy == null) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (copyLegacy(legacy, yaml)) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Écriture de messages.yml impossible : " + e.getMessage());
            }
        }
    }

    /**
     * Reprend dans {@code target} le texte de chaque ancienne clé à tiret bas (no_permission) ; une clé dont la
     * forme a changé n'a plus d'équivalent dans le jar (incorrect_usage) et n'est pas reprise. Vrai si quelque chose
     * a changé.
     */
    static boolean copyLegacy(ConfigurationSection legacy, YamlConfiguration target) {
        boolean changed = false;
        for (String key : target.getKeys(false)) {
            String old = key.replace('-', '_');
            if (target.isString(key) && legacy.isString(old)) {
                target.set(key, legacy.getString(old));
                changed = true;
            }
        }
        return changed;
    }

    /** Lit messages.yml ; lève l'erreur YAML telle quelle pour que l'appelant garde l'ancien état. */
    static Messages load(JavaPlugin plugin) throws IOException, InvalidConfigurationException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(new File(plugin.getDataFolder(), "messages.yml"));
        return merge(yaml, jarDefaults(plugin));
    }

    /** Textes du jar seuls, quand messages.yml est illisible au démarrage. */
    static Messages defaults(JavaPlugin plugin) {
        return merge(new YamlConfiguration(), jarDefaults(plugin));
    }

    private static Messages merge(YamlConfiguration file, YamlConfiguration defaults) {
        Set<String> keys = new LinkedHashSet<>(defaults.getKeys(false));
        keys.addAll(file.getKeys(false));
        Map<String, String> texts = new HashMap<>();
        for (String key : keys) {
            String value = file.isString(key) ? file.getString(key) : defaults.getString(key);
            if (value != null && !value.isEmpty()) {
                texts.put(key, value);
            }
        }
        return new Messages(texts);
    }

    private static YamlConfiguration jarDefaults(JavaPlugin plugin) {
        try (InputStream in = plugin.getResource("messages.yml")) {
            return in == null ? new YamlConfiguration()
                    : YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new YamlConfiguration();
        }
    }

    /** Message de la clé ; chaque paire de {@code values} est un {nom} à remplacer par sa valeur. Null si vide. */
    String get(String key, String... values) {
        String text = texts.get(key);
        if (text == null) {
            return null;
        }
        for (int i = 0; i + 1 < values.length; i += 2) {
            text = text.replace("{" + values[i] + "}", values[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
