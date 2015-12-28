package com.updg.SCBUNGEE.events;

import com.updg.SCBUNGEE.models.BanModel;
import com.updg.SCBUNGEE.utils.BanSystem;
import com.updg.SCBUNGEE.utils.L;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Alex
 * Date: 20.01.14  21:50
 */
public class Player implements Listener {
    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer player;
        if (e.getMessage().startsWith("/"))
            return;
        if ((e.getSender() instanceof ProxiedPlayer)) {
            player = (ProxiedPlayer) e.getSender();
            BanModel b = BanSystem.isMuted(player.getName());
            if (b != null) {
                e.setCancelled(true);
                b.sendMessage(player);
            }
        }

    }
}
