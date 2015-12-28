package com.updg.SCBUNGEE.threads;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 26.01.14  20:01
 */
public class Announcer extends Thread {
    long time;

    int now = 0;

    public Announcer(long time) {
        this.time = time;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (scbungee.messages.size() > 0) {
                if (now == scbungee.messages.size())
                    now = 0;
                String message = scbungee.messages.get(now);
                now++;
                BungeeCord.getInstance().broadcast(message);
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
            }
        }
    }
}
