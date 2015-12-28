package com.updg.SCBUNGEE;

import com.updg.SCBUNGEE.commands.*;
import com.updg.SCBUNGEE.commands.banSystem.*;
import com.updg.SCBUNGEE.events.DataSocket;
import com.updg.SCBUNGEE.events.JoinQuit;
import com.updg.SCBUNGEE.events.Player;
import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.threads.Announcer;
import com.updg.SCBUNGEE.utils.L;
import com.updg.SCBUNGEE.utils.Redis;
import com.updg.SCBUNGEE.utils.Utils;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Alex
 * Date: 18.06.13  18:57
 */
public class scbungee extends Plugin {
    private static scbungee instance;
    private static Connection db;

    public static HashMap<String, SCPlayer> loggedIn = new HashMap<String, SCPlayer>();
    private static boolean maintenance = false;
    public Utils utils;
    public ProxyServer proxy;
    public boolean debug = true;
    private List<String> serverToRemove = new ArrayList<String>();
    public boolean onHalt = false;

    public Announcer announcer;
    public static ArrayList<String> messages = new ArrayList<String>();

    public static scbungee getInstance() {
        return instance;
    }

    public static Connection getDB() {
        return db;
    }

    public static String[] playersMsg;

    public void onEnable() {
        instance = this;
        new Redis("localhost", 6379);
        if (Redis.exists("maintenance"))
            maintenance = true;

        try {
            db = DriverManager.getConnection("jdbc:mysql://localhost/crmc?autoReconnect=true&user=sc_main&password=pass&characterEncoding=UTF-8");
        } catch (SQLException e) {
            L.$("Driver loaded, but cannot connect to db: " + e);
            System.exit(0);
        }

        getProxy().registerChannel("StreamBungee");
        getProxy().registerChannel("BungeeCord");
        this.proxy = ProxyServer.getInstance();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new DataSocket());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinQuit());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Player());

        registerCommands();
        this.utils = new Utils(this);

        registerServers();
        registerAnnouncer();
        L.$("Loaded!");
    }

    private void registerCommands() {
        //BAN SYSTEM
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Ban());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new IPBan());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Kick());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TBan());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TIPBan());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Warn());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Mute());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TMute());

        //PROXY SYSTEM
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Maintenance());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandReload());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandEnd());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandAlert());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandLobby());
    }

    private void registerServers() {
        CopyOnWriteArrayList<String> old = new CopyOnWriteArrayList<String>();
        old.addAll(proxy.getServers().keySet());
        try {
            PreparedStatement ps = getDB().prepareStatement("SELECT * from servers where active=1");
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                if (!BungeeCord.getInstance().getServers().containsKey(r.getString("connectUrl")))
                    BungeeCord.getInstance().getConfig().addServer(r.getString("connectUrl"), BungeeCord.getInstance().constructServerInfo(r.getString("connectUrl"), Util.getAddr(r.getString("serverIp") + ":" + r.getString("serverPort")), (r.getString("modt") == null ? r.getString("connectUrl") : Utils.format(r.getString("modt"))), false));
                else old.remove(r.getString("connectUrl"));
                if (r.getInt("id") == 1)
                    playersMsg = Utils.formatWithArray(r.getString("playersMsg"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String entry : old) {
            if (proxy.getServerInfo(entry).getPlayers().size() > 0) {
                serverToRemove.add(entry);
            } else {
                BungeeCord.getInstance().getConfig().removeServer(entry);
            }
        }
    }

    private void registerAnnouncer() {
        ArrayList<String> messages = new ArrayList<String>();
        try {
            PreparedStatement ps = getDB().prepareStatement("SELECT * from bungeeAnnouncer where active=1 order by `order`");
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                messages.add(Utils.format(r.getString("text")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (messages.size() > 0) {
            scbungee.messages = messages;
            if (announcer == null) {
                announcer = new Announcer(60000);
                announcer.start();
            }
        } else {
            L.$("Announcer not started cuz we don't have messages!");
        }
    }

    public static boolean inOnMaintenance() {
        return maintenance;
    }

    public static void setOnMaintenance(boolean b) {
        maintenance = b;
    }

    public static void checkServer(String name) {
        if (getInstance().serverToRemove.contains(name)) {
            if (ProxyServer.getInstance().getServerInfo(name).getPlayers().size() == 0) {
                BungeeCord.getInstance().getConfig().removeServer(name);
            }
        }
    }

    public static SCPlayer getPlayer(String name) {
        if (scbungee.loggedIn.containsKey(name.toLowerCase())) {
            return scbungee.loggedIn.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    public void reload() {
        registerServers();
        registerAnnouncer();
    }
}
