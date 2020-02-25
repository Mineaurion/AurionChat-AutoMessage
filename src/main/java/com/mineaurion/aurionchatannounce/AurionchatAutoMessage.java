package com.mineaurion.aurionchatannounce;

import com.mineaurion.aurionchatannounce.channel.ChatService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class AurionchatAutoMessage extends JavaPlugin {

    private Config config;
    private ChatService chatService;

    @Override
    public void onEnable() {
        sendConsoleMessage("&8[&eAurionChat AutoMessage&8]&e - Initializing...");
        config = new Config(this);
        chatService = new ChatService(this);
        Announce announce = new Announce(this);
        announce.schedule();
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

    public Config getConfigFile() {
        return config;
    }

    public ChatService getChatService() { return chatService; }

    public void sendConsoleMessage(String message){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
