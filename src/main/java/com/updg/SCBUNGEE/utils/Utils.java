package com.updg.SCBUNGEE.utils;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 18.06.13  19:13
 */
public class Utils {
    private final scbungee plugin;

    public Utils(scbungee p) {
        this.plugin = p;
    }

    public void isLogged(String player, Connection c) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("isLoggedBack");
            out.writeUTF(player);
            if (scbungee.loggedIn.containsKey(player.toLowerCase())) {
                out.writeBoolean(true);
                out.writeInt(scbungee.loggedIn.get(player.toLowerCase()).getId());
                out.writeInt(scbungee.loggedIn.get(player.toLowerCase()).getRang());
                out.writeInt(scbungee.loggedIn.get(player.toLowerCase()).getVip());
            } else {
                out.writeBoolean(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scbungee.getInstance().getProxy()
                .getScheduler()
                .runAsync(
                        scbungee.getInstance(),
                        new SendPluginMessage("StreamBungee",
                                getServerByConnection(c), b)
                );

    }

    public ServerInfo getServerByConnection(Connection c) {
        for (ServerInfo item : ProxyServer.getInstance().getServers().values()) {
            if (item.getAddress() == c.getAddress()) {
                return item;
            }
        }
        return null;
    }

    public void addPlayer(int id, String player, int status, int vip, int active, int project) {
        scbungee.loggedIn.put(player.toLowerCase(), new SCPlayer(id, player.toLowerCase(), status, vip, active, project));
    }

    public void removePlayer(String player) {
        if (scbungee.loggedIn.containsKey(player.toLowerCase()))
            scbungee.loggedIn.remove(player.toLowerCase());
    }

    public Server getPlayersServer(String name) {
        return ProxyServer.getInstance().getPlayer(name).getServer();
    }

    public void kickPlayer(String player, String message) {
        ProxiedPlayer pp = getClosestPlayer(player);
        if (pp == null) {
            return;
        }
        if (message.equals("")) {
            pp.disconnect(TextComponent.fromLegacyText("Упс. Что то не так."));
            return;
        }
        pp.disconnect(TextComponent.fromLegacyText(message));
    }

    public ProxiedPlayer getClosestPlayer(String player) {
        if (this.plugin.proxy.getPlayer(player) != null) {
            return this.plugin.proxy.getPlayer(player);
        }
        for (ProxiedPlayer data : this.plugin.proxy.getPlayers()) {
            if (data.getName().toLowerCase().contains(player.toLowerCase())) {
                return data;
            }
        }
        return null;
    }

    public static boolean userExists(String string) {
        try {
            PreparedStatement ps = scbungee.getDB().prepareStatement("SELECT id FROM users WHERE playername=?");
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void sendMessage(ProxiedPlayer p, String text) {
        p.sendMessage(TextComponent.fromLegacyText(text));
    }

    public static void sendMessage(CommandSender p, String text) {
        Utils.sendMessage(p, text, false);
    }

    public static void sendMessage(CommandSender p, String text, boolean prefix) {
        if (prefix)
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "[BanSystem] " + ChatColor.RESET + text));
        else
            p.sendMessage(TextComponent.fromLegacyText(text));
    }

    public static void disconnect(ProxiedPlayer p, String text) {
        disconnect(p, text, true);
    }

    public static void disconnect(ProxiedPlayer p, String text, boolean ban) {
        if (ban)
            p.disconnect(TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "[BanSystem] " + ChatColor.RESET + text));
        else
            p.disconnect(TextComponent.fromLegacyText(text));
    }

    public static void disconnect(PendingConnection connection, String text) {
        connection.disconnect(TextComponent.fromLegacyText(text));
    }

    public static String tabTextCentered(String s) {
        int i = 14 - s.length();
        if (i > 1) {
            i = (int) i / 2;
            if (i >= 1)
                i++;
            for (int a = 1; a <= i; a++) {
                s = " " + s;
            }
            return s;
        } else if (i == 1)
            return " " + s;
        else
            return s;
    }

    public static SCPlayer getUser(String string) {
        if (scbungee.loggedIn.containsKey(string.toLowerCase()))
            return scbungee.loggedIn.get(string.toLowerCase());
        try {
            PreparedStatement ps = scbungee.getDB().prepareStatement("SELECT id, rang, vip, active FROM users WHERE playername=?");
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new SCPlayer(rs.getInt("id"), string.toLowerCase(), rs.getInt("rang"), rs.getInt("vip"), rs.getInt("active"), rs.getInt("project"));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<ProxiedPlayer> getPlayersByIP(String ip) {
        ArrayList<ProxiedPlayer> out = new ArrayList<ProxiedPlayer>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.getAddress().getAddress().getHostAddress().equals(ip)) {
                out.add(p);
            }
        }
        return out;
    }

    public static String format(String string) {
        String s = string;
        for (ChatColor color : ChatColor.values()) {
            s = s.replaceAll("(?i)<" + color.name() + ">", "" + color);
        }
        s = s.replaceAll("(?i)<newLine>", "\n");
        return s;
    }

    public static String[] formatWithArray(String string) {
        String s = string;
        for (ChatColor color : ChatColor.values()) {
            s = s.replaceAll("(?i)<" + color.name() + ">", "" + color);
        }
        return s.split("(?i)<newLine>");
    }

    public static void toLobby(UserConnection sender) {
        sender.connect(BungeeCord.getInstance().getServerInfo("lobby"));
    }

    public static void toLobby(ProxiedPlayer sender) {
        sender.connect(BungeeCord.getInstance().getServerInfo("lobby"));
    }
}
