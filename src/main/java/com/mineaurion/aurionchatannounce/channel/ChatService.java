package com.mineaurion.aurionchatannounce.channel;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class ChatService {
    private final Connection connection;
    private final Channel channel;
    private String consumerTag;

    private static String EXCHANGE_NAME = "aurion.chat";

    public ChatService(String uri) throws KeyManagementException, URISyntaxException, NoSuchAlgorithmException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void send(String channelName, Component message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("channel", channelName);
        json.addProperty("type", "automessage");
        json.addProperty("message", GsonComponentSerializer.gson().serialize(message));

        channel.basicPublish(EXCHANGE_NAME, "" , null, json.toString().getBytes());
    }
    public void close() throws TimeoutException, IOException{
        channel.close();
        connection.close();
    }
}
