package com.mineaurion.aurionchatannounce.channel;

import com.google.gson.JsonObject;
import com.mineaurion.aurionchatannounce.AurionchatAutoMessage;
import com.rabbitmq.client.*;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class ChatService {

    private AurionchatAutoMessage plugin;

    private Connection connection;
    private Channel channel;
    private String consumerTag;

    private static String EXCHANGE_NAME = "aurion.chat";

    public ChatService(AurionchatAutoMessage plugin){
        this.plugin = plugin;
        ConnectionFactory factory = new ConnectionFactory();
        try{
            factory.setUri(plugin.getConfigFile().getUri());
        }
        catch (KeyManagementException|URISyntaxException|NoSuchAlgorithmException UriKeyException){
            deactivatePlugin();
            System.out.println("Uri Syntax Exception, please check the config or the documentation of rabbitmq");
        }
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        try{
            connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch (IOException|TimeoutException exception){
            System.out.println("Connection error with rabbitmq");
            System.out.println(exception.getMessage());
        }
    }

    public void send(String routingKey,String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("message", message);

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, json.toString().getBytes());
    }
    public void close() throws TimeoutException, IOException{
        channel.close();
        connection.close();
    }

    public void deactivatePlugin() {
        Bukkit.getPluginManager().disablePlugin(plugin);
    };

}
