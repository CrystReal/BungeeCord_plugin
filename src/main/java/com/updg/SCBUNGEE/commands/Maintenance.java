package com.updg.SCBUNGEE.commands;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.L;
import com.updg.SCBUNGEE.utils.Redis;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alex
 * Date: 15.12.13  18:03
 */
public class Maintenance extends Command {
    public Maintenance() {
        super("maintenance");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            if (scbungee.loggedIn.containsKey(commandSender.getName().toLowerCase())) {
                SCPlayer p = scbungee.loggedIn.get(commandSender.getName().toLowerCase());
                L.$(p.getRang() + "");
                if (p.getRang() == 1) {
                    if (strings.length < 1) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/maintenance <true/false>", true);
                        return;
                    }
                    if (strings[0].equals("true")) {
                        scbungee.setOnMaintenance(true);
                        Redis.set("maintenance", "true");
                        Utils.sendMessage(commandSender, "Теперь мы на обслуживании");
                        for (SCPlayer p1 : scbungee.loggedIn.values()) {
                            if (p1.getRang() != 1 && p1.getRang() != 2) {
                                Utils.disconnect(ProxyServer.getInstance().getPlayer(p1.getName()), "Упс! Мы на обслуживании. Скоро вернемся.");
                            }
                        }
                    } else if (strings[0].equals("false")) {
                        scbungee.setOnMaintenance(false);
                        Redis.del("Maintenance");
                        Utils.sendMessage(commandSender, "Теперь мы работаем");
                    }
                }
            }
        }
    }
}
