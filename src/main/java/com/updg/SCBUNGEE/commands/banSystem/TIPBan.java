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
public class TIPBan extends Command {
    public TIPBan() {
        super("tipban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            if (scbungee.loggedIn.containsKey(commandSender.getName().toLowerCase())) {
                SCPlayer p = scbungee.loggedIn.get(commandSender.getName().toLowerCase());
                if (p.canUseBanSystem()) {
                    if (strings.length < 3) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/tipban <имя игрока> <срок в днях> <причина>", true);
                        return;
                    }
                    int time = Integer.parseInt(strings[1]);
                    if (time < 1) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/tipban <имя игрока> <срок в днях> <причина>", true);
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
                            String reason = ruFix.f(StringUtil.combineSplit(2, strings, " "));
                            Utils.sendMessage(commandSender, ChatColor.GREEN + "Все игроки с IP как у " + strings[0] + " заблокированы на " + time + " " + StringUtil.plural(time, "день", "дня", "дней") + "!", true);
                            for (ProxiedPlayer p1 : Utils.getPlayersByIP(vP.getAddress().getAddress().getHostAddress())) {
                                Utils.disconnect(p1, "Ваш IP временно заблокирован администратором " + ChatColor.RED + ((ProxiedPlayer) commandSender).getDisplayName() + ChatColor.RESET + "\nПричина: " + reason + "\nСрок блокировки: " + time + " " + StringUtil.plural(time, "день", "дня", "дней") + "\n" + ChatColor.RESET + "Если Вы считаете что это ошибка\nсвяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");
                            }
                            BanSystem.banUser(p, v, reason, BanType.TEMP_IP_BAN.getValue(), time);
                        } else {
                            Utils.sendMessage(commandSender, ChatColor.RED + "IP уже заблокирован!", true);
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
