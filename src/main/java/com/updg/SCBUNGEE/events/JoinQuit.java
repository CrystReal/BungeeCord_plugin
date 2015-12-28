package com.updg.SCBUNGEE.events;

import com.updg.SCBUNGEE.TabList.CTabListHandler;
import com.updg.SCBUNGEE.models.BanModel;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.BanSystem;
import com.updg.SCBUNGEE.utils.L;
import com.updg.SCBUNGEE.utils.Redis;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.Protocol;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 14.12.13  23:19
 */
public class JoinQuit implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        if (scbungee.inOnMaintenance()) {
            ServerPing.Protocol prot = new ServerPing.Protocol("Обслуживание", -1);
            ServerPing p = new ServerPing(prot, e.getResponse().getPlayers(), e.getResponse().getDescription(), e.getResponse().getFavicon());
            e.setResponse(p);
        } else {
            ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[scbungee.playersMsg.length];
            for (int i = 0; i < scbungee.playersMsg.length; i++) {
                sample[i] = new ServerPing.PlayerInfo(scbungee.playersMsg[i], "");
                e.getResponse().getPlayers().setSample(sample);
            }
        }
    }

    @EventHandler
    public void onJoin(LoginEvent e) {
        if (scbungee.inOnMaintenance()) {
            String s = Redis.get("rang_" + e.getConnection().getName().toLowerCase());
            if (s == null || (!s.equals("1") && !s.equals("2"))) {
                Utils.disconnect(e.getConnection(), "Мы на обслуживании");
                return;
            }
        }
        String reg = Redis.get("allUsers_" + e.getConnection().getName().toLowerCase());
        if (reg == null) {
            ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();
            TextComponent component = new TextComponent();
            component.setText("Ты не зарегистрирован на сайте ");
            components.add(component);
            component = new TextComponent();
            component.setText("crystreal.net");
            component.setColor(ChatColor.AQUA);
            component.setBold(true);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                    "http://crystreal.net"));
            components.add(component);
            component = new TextComponent();
            component.setText("!");
            components.add(component);
            e.getConnection().disconnect(components.toArray(new BaseComponent[components.size()]));
            return;
        } else if (reg.equals("needAct")) {
            Utils.disconnect(e.getConnection(), "Ты не активировал профиль!");
            return;
        }
        BanModel b = BanSystem.isBanned(e.getConnection().getName());
        if (b != null) {
            b.kickWithMessage(e.getConnection());
            return;
        }
        b = BanSystem.isIPBanned(e.getConnection().getAddress().getAddress().getHostAddress());
        if (b != null) {
            b.kickWithMessage(e.getConnection());
            return;
        }
    }

    @EventHandler
    public void onJoin(ServerConnectedEvent e) {

        //TODO: FRIENDS
        /*e.getPlayer().setTabList(new CTabListHandler(e.getPlayer()));
        ((CTabListHandler) e.getPlayer().getTabList()).clear();
        ((CTabListHandler) e.getPlayer().getTabList()).setSlot(1, 2, ChatColor.BLUE + Utils.tabTextCentered("Crystal"), true);
        ((CTabListHandler) e.getPlayer().getTabList()).setSlot(2, 2, ChatColor.BLUE + Utils.tabTextCentered("Reality"), true);
        ((CTabListHandler) e.getPlayer().getTabList()).setSlot(9, 2, Utils.tabTextCentered("Войди для"), true);
        ((CTabListHandler) e.getPlayer().getTabList()).setSlot(10, 2, Utils.tabTextCentered("начала игры"), true);
        ((CTabListHandler) e.getPlayer().getTabList()).setSlot(20, 2, Utils.tabTextCentered("crystreal.net"), true);   */
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        Redis.del("online_" + e.getPlayer().getName().toLowerCase());
        if (e.getPlayer().getServer() != null)
            Redis.set("offline_" + e.getPlayer().getName().toLowerCase(), System.currentTimeMillis() / 1000 + "|" + e.getPlayer().getServer().getInfo().getName());
        scbungee.getInstance().utils.removePlayer(e.getPlayer().getName());
        BanSystem.removeFromMuteCache(e.getPlayer().getName().toLowerCase());
        if (e.getPlayer().getServer() != null)
            scbungee.checkServer(e.getPlayer().getServer().getInfo().getName());
    }

    @EventHandler
    public void serverChange(ServerSwitchEvent e) {
        Redis.set("online_" + e.getPlayer().getName().toLowerCase(), e.getPlayer().getServer().getInfo().getName());
    }

    @EventHandler
    public void onKick(ServerKickEvent e) {
        if (e.getKickReasonComponent()[0].toPlainText().contains("servclosed")) {
            e.getPlayer().connect(ProxyServer.getInstance().getServers().get("lobby"));
            e.setCancelled(true);
        }
    }
}
