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
public class TBan extends Command {
    public TBan() {
        super("tban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            if (scbungee.loggedIn.containsKey(commandSender.getName().toLowerCase())) {
                SCPlayer p = scbungee.loggedIn.get(commandSender.getName().toLowerCase());
                if (p.canUseBanSystem()) {
                    if (strings.length < 2) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/tban <имя игрока> <срок в днях> <причина>", true);
                        return;
                    }
                    int time = Integer.parseInt(strings[1]);
                    if (time < 1) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/tban <имя игрока> <срок в днях> <причина>", true);
                        return;
                    }
                    if (strings[0].equals(p.getName())) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "Нельзя забанить самого себя", true);
                        return;
                    }
                    SCPlayer v = Utils.getUser(strings[0]);
                    if (v != null) {
                        if (v.getStatus() >= p.getStatus()) {
                            Utils.sendMessage(commandSender, ChatColor.RED + "Игрок является вашего или выше ранга!", true);
                            return;
                        }
                        if (BanSystem.isBanned(strings[0]) == null) {
                            String reason = ruFix.f(StringUtil.combineSplit(2, strings, " "));
                            Utils.sendMessage(commandSender, ChatColor.GREEN + "Игрок " + strings[0] + " заблокирован на " + time + " " + StringUtil.plural(time, "день", "дня", "дней") + "!", true);
                            BanSystem.banUser(p, v, reason, BanType.TEMP_BAN.getValue(), time);
                            ProxiedPlayer vP = ProxyServer.getInstance().getPlayer(v.getName());
                            if (vP != null)
                                Utils.disconnect(vP, "Вы временно заблокированы администратором " + ChatColor.RED + ((ProxiedPlayer) commandSender).getDisplayName() + ChatColor.RESET + "\nПричина: " + reason + "\nСрок блокировки: " + time + " " + StringUtil.plural(time, "день", "дня", "дней") + "\n" + ChatColor.RESET + "Если Вы считаете что это ошибка\nсвяжитесь с администрацией на сайте " + ChatColor.AQUA + "crystreal.net");

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
