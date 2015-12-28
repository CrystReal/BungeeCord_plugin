package com.updg.SCBUNGEE.utils;

import com.updg.SCBUNGEE.models.BanModel;
import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.models.enums.BanType;
import com.updg.SCBUNGEE.scbungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alex
 * Date: 15.12.13  0:07
 */
public class BanSystem {
    public static HashMap<String, BanModel> mutes = new HashMap<String, BanModel>();
    public static ArrayList<String> checkedMutes = new ArrayList<String>();

    public static BanModel isBanned(String username) {
        username = username.toLowerCase();
        String ban;
        ban = Redis.get("ban_" + username);
        if (ban != null) {
            String[] params = ban.split("\t");
            if (params[0].equals("1")) {
                if (Long.parseLong(params[1]) >= (System.currentTimeMillis() / 1000)) {
                    return new BanModel(username, BanType.TEMP_BAN, Long.parseLong(params[1]), params[2]);
                } else {
                    Redis.del("ban_" + username);
                    return null;
                }
            }
            return new BanModel(username, BanType.PERM_BAN, Long.parseLong(params[1]), params[2]);
        } else {
            return null;
        }
    }

    public static BanModel isMuted(String username) {
        username = username.toLowerCase();
        if (checkedMutes.contains(username)) {
            if (mutes.containsKey(username)) {
                if (mutes.get(username).getTill() >= (System.currentTimeMillis() / 1000)) {
                    return mutes.get(username);
                } else {
                    Redis.del("ban_mute_" + username);
                    mutes.remove(username);
                    return null;
                }
            } else
                return null;
        }
        checkedMutes.add(username);
        String ban;
        ban = Redis.get("ban_mute_" + username);
        if (ban != null) {
            String[] params = ban.split("\t");
            if (params[0].equals("1")) {
                if (Long.parseLong(params[1]) >= (System.currentTimeMillis() / 1000)) {
                    BanModel o = new BanModel(username, BanType.TEMP_MUTE, Long.parseLong(params[1]), params[2]);
                    mutes.put(username, o);
                    return o;
                } else {
                    Redis.del("ban_mute_" + username);
                    return null;
                }
            }
            BanModel o = new BanModel(username, BanType.PERM_MUTE, Long.parseLong(params[1]), params[2]);
            mutes.put(username, o);
            return o;
        } else {
            return null;
        }
    }

    public static BanModel isIPBanned(String ip) {
        String ban;
        ban = Redis.get("ban_ip_" + ip);
        if (ban != null) {
            String[] params = ban.split("\t");
            if (params[0].equals("1")) {
                if (Long.parseLong(params[2]) >= (System.currentTimeMillis() / 1000)) {
                    return new BanModel(ip, BanType.TEMP_IP_BAN, Long.parseLong(params[1]), params[2]);
                } else {
                    Redis.del("ban_ip_" + ip);
                    return null;
                }
            }
            return new BanModel(ip, BanType.PERM_IP_BAN, Long.parseLong(params[1]), params[2]);
        } else {
            return null;
        }
    }

    public static void banUser(SCPlayer admin, SCPlayer victim, String reason, int type, long time) {
        if (time > 0) {
            time = System.currentTimeMillis() / 1000 + (time * 60 * 60 * 24);
        }
        if (type != BanType.TEMP_MUTE.getValue() && type != BanType.PERM_MUTE.getValue()) {
            if (isBanned(victim.getName()) == null && isIPBanned(ProxyServer.getInstance().getPlayer(victim.getName()).getAddress().getAddress().getHostAddress()) == null) {
                PreparedStatement ps;
                if (type == BanType.PERM_BAN.getValue() || type == BanType.TEMP_BAN.getValue()) {
                    try {
                        // TO BANS STATS TABLE
                        ps = scbungee.getDB().prepareStatement("INSERT INTO banLog (`when`, `admin_id`, `admin_server`, `victim_id`, `victim_server`, `type`, `term`, `reason`) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, admin.getId());
                        ps.setString(2, admin.getServer());
                        ps.setInt(3, victim.getId());
                        ps.setString(4, victim.getServer());
                        ps.setInt(5, type);
                        ps.setLong(6, time);
                        ps.setString(7, reason);
                        int logId = ps.executeUpdate();

                        // TO BANS TABLE
                        ps = scbungee.getDB().prepareStatement("INSERT INTO bans (`log_id`, `user_id`, `user_nick`, `type`, `term`, `reason`) VALUES (?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, logId);
                        ps.setInt(2, victim.getId());
                        ps.setString(3, victim.getName());
                        ps.setInt(4, type);
                        ps.setLong(5, time);
                        ps.setString(6, reason);
                        ps.executeUpdate();

                        // REDIS
                        if (type == BanType.TEMP_BAN.getValue())
                            Redis.set("ban_" + victim.getName(), "1\t" + time + "\t" + reason);
                        else
                            Redis.set("ban_" + victim.getName(), "0\t0" + "\t" + reason);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (type == BanType.PERM_IP_BAN.getValue() || type == BanType.TEMP_IP_BAN.getValue()) {
                    String ip = ProxyServer.getInstance().getPlayer(victim.getName()).getAddress().getAddress().getHostAddress();
                    try {
                        // TO BANS STATS TABLE
                        ps = scbungee.getDB().prepareStatement("INSERT INTO banLog (`when`, `admin_id`, `admin_server`, `victim_id`, `victim_server`, `type`, `term`, `reason`, `notes`) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, admin.getId());
                        ps.setString(2, admin.getServer());
                        ps.setInt(3, victim.getId());
                        ps.setString(4, victim.getServer());
                        ps.setInt(5, type);
                        ps.setLong(6, time);
                        ps.setString(7, reason);
                        ps.setString(8, "IP: " + ip);
                        int logId = ps.executeUpdate();

                        // TO IP BANS TABLE
                        ps = scbungee.getDB().prepareStatement("INSERT INTO ipBans (`log_id`, `ip`, `type`, `term`, `reason`) VALUES (?, ?, ?, ?, ?)");
                        ps.setInt(1, logId);
                        ps.setString(2, ip);
                        ps.setInt(3, type);
                        ps.setLong(4, time);
                        ps.setString(5, reason);
                        ps.executeUpdate();

                        // REDIS
                        if (type == BanType.TEMP_IP_BAN.getValue())
                            Redis.set("ban_ip_" + ip, "1\t" + time + "\t" + reason);
                        else
                            Redis.set("ban_ip_" + ip, "0\t0" + "\t" + reason);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (isMuted(victim.getName()) == null) {
                PreparedStatement ps;
                try {
                    // TO BANS STATS TABLE
                    ps = scbungee.getDB().prepareStatement("INSERT INTO banLog (`when`, `admin_id`, `admin_server`, `victim_id`, `victim_server`, `type`, `term`, `reason`) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, admin.getId());
                    ps.setString(2, admin.getServer());
                    ps.setInt(3, victim.getId());
                    ps.setString(4, victim.getServer());
                    ps.setInt(5, type);
                    ps.setLong(6, time);
                    ps.setString(7, reason);
                    int logId = ps.executeUpdate();

                    // TO BANS TABLE
                    ps = scbungee.getDB().prepareStatement("INSERT INTO bans (`log_id`, `user_id`, `user_nick`, `type`, `term`, `reason`) VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, logId);
                    ps.setInt(2, victim.getId());
                    ps.setString(3, victim.getName());
                    ps.setInt(4, type);
                    ps.setLong(5, time);
                    ps.setString(6, reason);
                    ps.executeUpdate();

                    // REDIS
                    if (checkedMutes.contains(victim.getName()))
                        checkedMutes.remove(victim.getName());
                    if (mutes.containsKey(victim.getName()))
                        mutes.remove(victim.getName());
                    if (type == BanType.TEMP_MUTE.getValue())
                        Redis.set("ban_mute_" + victim.getName(), "1\t" + time + "\t" + reason);
                    else
                        Redis.set("ban_mute_" + victim.getName(), "0\t0" + "\t" + reason);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void warnUser(SCPlayer admin, SCPlayer victim, String reason) {
        // TO BANS STATS TABLE
        PreparedStatement ps;
        try {
            ps = scbungee.getDB().prepareStatement("INSERT INTO banLog (`when`, `admin_id`, `admin_server`, `victim_id`, `victim_server`, `type`, `term`, `reason`) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, admin.getId());
            ps.setString(2, admin.getServer());
            ps.setInt(3, victim.getId());
            ps.setString(4, victim.getServer());
            ps.setInt(5, BanType.WARN.getValue());
            ps.setLong(6, 0);
            ps.setString(7, reason);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void kickUser(SCPlayer admin, SCPlayer victim, String reason) {
        // TO BANS STATS TABLE
        PreparedStatement ps;
        try {
            ps = scbungee.getDB().prepareStatement("INSERT INTO banLog (`when`, `admin_id`, `admin_server`, `victim_id`, `victim_server`, `type`, `term`, `reason`VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, admin.getId());
            ps.setString(2, admin.getServer());
            ps.setInt(3, victim.getId());
            ps.setString(4, victim.getServer());
            ps.setInt(5, BanType.KICK.getValue());
            ps.setLong(6, 0);
            ps.setString(7, reason);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeFromMuteCache(String s) {
        if (mutes.containsKey(s))
            mutes.remove(s);
        if (checkedMutes.contains(s))
            checkedMutes.remove(s);
    }
}
