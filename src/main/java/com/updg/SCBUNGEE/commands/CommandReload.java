package com.updg.SCBUNGEE.commands;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandReload extends Command {

    public CommandReload() {
        super("proxyreload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            SCPlayer p = scbungee.getPlayer(sender.getName());
            if (p == null || !p.isAdmin())
                return;
        }
        scbungee.getInstance().reload();
        Utils.sendMessage(sender, ChatColor.RED + "" + ChatColor.BOLD + "Сервера обновлены.");
    }
}
