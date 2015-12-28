package com.updg.SCBUNGEE.commands;

import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Command to terminate the proxy instance. May only be used by the console.
 */
public class CommandEnd extends Command {

    public CommandEnd() {
        super("proxystop");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            SCPlayer p = scbungee.getPlayer(sender.getName());
            if (p == null || !p.isAdmin())
                return;
        }
        if (scbungee.getInstance().onHalt)
            return;
        scbungee.getInstance().onHalt = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.MAGIC + "===========================================================");
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.BOLD + "Внимание! Через минуту будет перезагружен центральный узел!");
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.MAGIC + "===========================================================");
                    }
                    Thread.sleep(30000);
                    for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.MAGIC + "===========================================================");
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.BOLD + "Внимание! Через 30 секунд будет перезагружен центральный узел!");
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.MAGIC + "===========================================================");
                    }
                    Thread.sleep(20000);
                    for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.MAGIC + "===========================================================");
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.BOLD + "Внимание! Через 10 секунд будет перезагружен центральный узел!");
                        Utils.sendMessage(p, ChatColor.RED + "" + ChatColor.MAGIC + "===========================================================");
                    }
                    BungeeCord.getInstance().stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
