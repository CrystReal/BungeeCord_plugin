package com.updg.SCBUNGEE.commands;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandAlert extends Command {

    public CommandAlert() {
        super("proxyalert");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            SCPlayer p = scbungee.getPlayer(sender.getName());
            if (p == null || !p.isAdmin())
                return;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Вы должны указать сообщение.");
        } else {
            StringBuilder builder = new StringBuilder();

            for (String s : args) {
                builder.append(ChatColor.translateAlternateColorCodes('&', s));
                builder.append(" ");
            }

            String message = builder.substring(0, builder.length() - 1);

            ProxyServer.getInstance().broadcast(ChatColor.RED + "" + ChatColor.BOLD + "[Глобальное оповещение] " + ChatColor.RESET + message);
        }
    }
}
