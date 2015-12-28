package com.updg.SCBUNGEE.commands.banSystem;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.models.enums.BanType;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.BanSystem;
import com.updg.SCBUNGEE.utils.StringUtil;
import com.updg.SCBUNGEE.utils.Utils;
import com.updg.SCBUNGEE.utils.ruFix.ruFix;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alex
 * Date: 14.12.13  23:29
 */
public class IPBan extends Command {
    public IPBan() {
        super("ipban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            if (scbungee.loggedIn.containsKey(commandSender.getName().toLowerCase())) {
                SCPlayer p = scbungee.loggedIn.get(commandSender.getName().toLowerCase());
                if (p.canUseBanSystem()) {
                    if (strings.length < 2) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/ipban <имя игрока> <причина>", true);
                        return;
                    }
                    if (strings[0].equals(p.getName())) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "Нельзя забанить самого себя", true);
                        return;
                    }
                    SCPlayer v = Utils.getUser(strings[0]);
                    if (v != null) {
                        ProxiedPlayer vP = ProxyServer.getInstance().getPlayer(v.getName());
                        if (vP.getAddress().getAddress().getHostAddress().equals(((ProxiedPlayer) commandSender).getAddress().getAddress().getHostAddress())) {
                            Utils.sendMessage(commandSender, ChatColor.RED + "Нельзя забанить самого себя", true);
                            return;
                        }
                        if (BanSystem.isIPBanned(strings[0]) == null) {
                            String reason = ruFix.f(StringUtil.combineSplit(1, strings, " "));
                            BanSystem.banUser(p, v, reason, BanType.PERM_IP_BAN.getValue(), 0);
                            Utils.sendMessage(commandSender, ChatColor.GREEN + "Все игроки с IP как у " + strings[0] + " заблокированы!", true);
                            for (ProxiedPlayer p1 : Utils.getPlayersByIP(vP.getAddress().getAddress().getHostAddress())) {
                                Utils.disconnect(p1, "Ваш IP заблокирован администратором " + ChatColor.RED + ((ProxiedPlayer) commandSender).getDisplayName() + "\nПричина: " + reason + "\n" + ChatColor.RESET + "Если Вы считаете что это ошибка\nсвяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
                            }
                        } else {
                            Utils.sendMessage(commandSender, ChatColor.RED + "Игрок уже заблокирован!", true);
                        }
                    } else {
                        Utils.sendMessage(commandSender, ChatColor.RED + "Игрок не найден!", true);
                    }
                } else {
                    Utils.sendMessage(commandSender, ChatColor.RED + "Недостаточно прав!", true);
                }
            } else
                Utils.sendMessage(commandSender, ChatColor.RED + "Сначала авторизируйся!", true);
        } else {
            Utils.sendMessage(commandSender, "Welcome, console!", true);
        }
    }
}
