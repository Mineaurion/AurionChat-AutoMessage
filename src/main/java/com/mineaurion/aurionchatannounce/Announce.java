package com.mineaurion.aurionchatannounce;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announce {

    private AurionchatAutoMessage plugin;
    private Config config;

    private int task = -1;

    public Announce(AurionchatAutoMessage plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigFile();
    }

    public void schedule() {
        if(task != -1){
            return;
        }
        Map<String, Integer> nm = new HashMap<String, Integer>();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for(String channel: config.getAnnouncementsChannel().getKeys(false)) {
                nm.putIfAbsent(channel, 0);
                List<String> announcements = config.getAnnouncements(channel);
                int currentNm = nm.get(channel);
                try {
                    plugin.getChatService().send("aurion.automessage." + channel, announcements.get(currentNm));
                } catch (Exception e){
                    System.out.println("Error lors de l'envoie du message vers rabbitmq");
                    System.out.println(e.getStackTrace());
                }
                send(announcements.get(currentNm));
                ++currentNm;
                if(currentNm >= announcements.size()){
                    currentNm = 0;
                }
                nm.replace(channel, currentNm);
            }
        }, config.getDelay(), config.getDelay());
    }

    private void send(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
