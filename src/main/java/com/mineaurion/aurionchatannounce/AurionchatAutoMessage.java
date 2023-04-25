package com.mineaurion.aurionchatannounce;

import com.mineaurion.aurionchatannounce.channel.ChatService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AurionchatAutoMessage extends JavaPlugin {

    private Config config;
    private ChatService chatService;

    // Map to know last announce was sent.
    private final Map<String, Integer> lastAnnounceIndex = new HashMap<>();

    @Override
    public void onEnable() {
        sendConsoleMessage("&8[&eAurionChat AutoMessage&8]&e - Initializing...");
        config = new Config(this);
        try{
            chatService = new ChatService(config.getUri());
        } catch (IOException | TimeoutException | KeyManagementException | URISyntaxException | NoSuchAlgorithmException exception){
            System.out.println("Connection error with rabbitmq");
            System.out.println(exception.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
        schedule();
        sendConsoleMessage("&8[&eAurionChat AutoMessage&8]&e - Enabled Successfully");
    }

    @Override
    public void onDisable() {
        try{
            chatService.close();
        }
        catch(Exception e){
            sendConsoleMessage("&8[&eAurionChat AutoMessage&8]&e - Error when communication closed");
            sendConsoleMessage(e.getMessage());
        }
    }

    private void schedule() {
        for(String channel: config.getAnnouncementsChannel().getKeys(false)){
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ()-> {
                List<List<String>> announcements = config.getAnnouncements(channel);
                lastAnnounceIndex.putIfAbsent(channel, 0);
                int currentIndex = lastAnnounceIndex.get(channel);
                List<String> announcementToProcess = announcements.get(currentIndex);
                for(String lineMessage: announcementToProcess){
                    try {
                        chatService.send("aurion.automessage." + channel, config.getPrefix() + lineMessage);
                    } catch (Exception e){
                        System.out.println("Error when sending message to rabbitmq");
                        System.out.println(e.getStackTrace());
                    }
                    sendConsoleMessage(config.getPrefix() + lineMessage);
                }
                ++currentIndex;
                if(currentIndex >= announcements.size()){
                    currentIndex = 0;
                }
                lastAnnounceIndex.replace(channel, currentIndex);
            }, config.getDelay(), config.getDelay());
        }
    }

    public void sendConsoleMessage(String message){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
