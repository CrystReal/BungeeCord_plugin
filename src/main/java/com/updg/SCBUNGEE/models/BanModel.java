package com.updg.SCBUNGEE.models;

import com.updg.SCBUNGEE.models.enums.BanType;
import com.updg.SCBUNGEE.utils.StringUtil;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Alex
 * Date: 20.01.14  21:27
 */
public class BanModel {
    private String userName;
    private BanType type;
    private long till = 0;
    private String reason;


    public BanModel(String userName, BanType type, long till, String reason) {
        this.userName = userName;
        this.type = type;
        this.till = till;
        this.reason = reason;
    }

    public void sendMessage(ProxiedPlayer p) {
        // MUTE
        if (type == BanType.PERM_MUTE)
            Utils.sendMessage(p, "Вы не можете писать в чат. Никогда. Вообще.");
        if (type == BanType.TEMP_MUTE) {
            long tmp = (till - System.currentTimeMillis() / 1000) / 60 / 60;
            Utils.sendMessage(p, "Вы временно не можете писать в чат. До конца действия блокировки " + tmp + " " + StringUtil.plural((int) tmp, "час", "часа", "часов") + ".");
        }
    }

    public void kickWithMessage(ProxiedPlayer p) {
        // BANS
        if (type == BanType.PERM_BAN)
            Utils.disconnect(p, "Ваш профиль заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        if (type == BanType.TEMP_BAN) {
            long tmp = (till - System.currentTimeMillis() / 1000) / 60 / 60;
            Utils.disconnect(p, "Ваш профиль временно заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "До конца действия блокировки " + tmp + " " + StringUtil.plural((int) tmp, "час", "часа", "часов") + "." +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        }
        // IP BANS
        if (type == BanType.PERM_IP_BAN)
            Utils.disconnect(p, "Ваш IP заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        if (type == BanType.TEMP_IP_BAN) {
            long tmp = (till - System.currentTimeMillis() / 1000) / 60 / 60;
            Utils.disconnect(p, "Ваш IP временно заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "До конца действия блокировки " + tmp + " " + StringUtil.plural((int) tmp, "час", "часа", "часов") + "." +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        }
    }

    public void kickWithMessage(PendingConnection p) {
        // BANS
        if (type == BanType.PERM_BAN)
            Utils.disconnect(p, "Ваш профиль заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        if (type == BanType.TEMP_BAN) {
            long tmp = (till - System.currentTimeMillis() / 1000) / 60 / 60;
            Utils.disconnect(p, "Ваш профиль временно заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "До конца действия блокировки " + tmp + " " + StringUtil.plural((int) tmp, "час", "часа", "часов") + "." +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        }
        // IP BANS
        if (type == BanType.PERM_IP_BAN)
            Utils.disconnect(p, "Ваш IP заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        if (type == BanType.TEMP_IP_BAN) {
            long tmp = (till - System.currentTimeMillis() / 1000) / 60 / 60;
            Utils.disconnect(p, "Ваш IP временно заблокирован на данном сервере.\n" +
                    "Причина: " + reason + "\n" +
                    "До конца действия блокировки " + tmp + " " + StringUtil.plural((int) tmp, "час", "часа", "часов") + "." +
                    "Если Вы считаете что это ошибка\n" +
                    "свяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
        }
    }

    public long getTill() {
        return till;
    }
}
