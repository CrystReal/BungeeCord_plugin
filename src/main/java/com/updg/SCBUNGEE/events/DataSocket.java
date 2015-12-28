package com.updg.SCBUNGEE.events;

import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.L;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Alex
 * Date: 14.12.13  23:21
 */
public class DataSocket implements Listener {

    @EventHandler
    public void receivePluginMessage(PluginMessageEvent event) throws IOException, SQLException {
        if (!event.getTag().equalsIgnoreCase("BungeeCord") && !event.getTag().equalsIgnoreCase("StreamBungee")) {
            return;
        }


        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (channel.equalsIgnoreCase("toLobby")) {
            scbungee.getInstance().utils.toLobby((UserConnection) event.getReceiver());
            return;
        }
        if (channel.equalsIgnoreCase("isLogged")) {
            String player = in.readUTF();
            scbungee.getInstance().utils.isLogged(player, event.getSender());
            return;
        }
        if (channel.equalsIgnoreCase("setLoggedIn")) {
            int id = in.readInt();
            String player = in.readUTF();
            int rang = in.readInt();
            int vip = in.readInt();
            int status = in.readInt();
            //int project = in.readInt();
            scbungee.getInstance().utils.addPlayer(id, player, rang, vip, status, 0);
            return;
        }
        if (channel.equalsIgnoreCase("setLoggedOut")) {
            String player = in.readUTF();
            scbungee.getInstance().utils.removePlayer(player);
            return;
        }
        if (channel.equalsIgnoreCase("kick")) {
            String player = in.readUTF();
            String msg = in.readUTF();
            scbungee.getInstance().utils.kickPlayer(player, msg);
            return;
        }
    }

}
