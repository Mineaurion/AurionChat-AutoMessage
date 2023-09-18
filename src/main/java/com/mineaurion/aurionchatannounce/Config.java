package com.mineaurion.aurionchatannounce;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {

    public FileConfiguration config;

    public Config(AurionchatAutoMessage plugin) {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public String getUri() {
        return config.getString("rabbitmq.uri");
    }

    /**
     * Return the delay in second
     * @return int
     */
    public int getDelay() {
        return config.getInt("delay") * 20;
    }


    public String getPrefix() {
        return config.getString("prefix");
    }

    public ConfigurationSection getAnnouncementsChannel() {
        return config.getConfigurationSection("announcements");
    }

    @SuppressWarnings("unchecked")
    public List<String> getAnnouncements(String key){
        return (List<String>) config.getList("announcements." + key);
    }


}
