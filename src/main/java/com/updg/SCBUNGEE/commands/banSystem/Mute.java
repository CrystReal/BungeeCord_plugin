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
public class Mute extends Command {
    public Mute() {
        super("mute");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            if (scbungee.loggedIn.containsKey(commandSender.getName().toLowerCase())) {
                SCPlayer p = scbungee.loggedIn.get(commandSender.getName().toLowerCase());
                if (p.canUseBanSystem()) {
                    if (strings.length < 2) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "/mute <имя игрока> <причина>", true);
                        return;
                    }
                    if (strings[0].equals(p.getName())) {
                        Utils.sendMessage(commandSender, ChatColor.RED + "Нельзя мютить самого себя", true);
                        return;
                    }
                    SCPlayer v = Utils.getUser(strings[0]);
                    if (v != null) {
                        if (v.getStatus() >= p.getStatus()) {
                            Utils.sendMessage(commandSender, ChatColor.RED + "Игрок является вашего или выше ранга!", true);
                            return;
                        }
                        String reason = ruFix.f(StringUtil.combineSplit(1, strings, " "));
                        Utils.sendMessage(commandSender, ChatColor.GREEN + "Игрок " + strings[0] + " замючен!", true);
                        ProxiedPlayer vP = ProxyServer.getInstance().getPlayer(v.getName());
                        if (vP != null)
                            Utils.sendMessage(vP, ChatColor.RED + "Вы больше не можете писать в чат. Причина: " + reason, true);
                        BanSystem.banUser(p, v, reason, BanType.PERM_MUTE.getValue(), 0);
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
