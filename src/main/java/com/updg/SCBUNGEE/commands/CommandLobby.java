package com.updg.SCBUNGEE.commands;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.updg.SCBUNGEE.models.SCPlayer;
import com.updg.SCBUNGEE.scbungee;
import com.updg.SCBUNGEE.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.Map;

/**
 * Command to list and switch a player between available servers.
 */
public class CommandLobby extends Command implements TabExecutor {

    public CommandLobby() {
        super("lobby", null, "hub");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        SCPlayer p = scbungee.getPlayer(player.getName());
        if (p == null)
            return;
        Utils.toLobby(player);
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, String[] args) {
        return (args.length != 0) ? Collections.EMPTY_LIST : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getServers().values(), new Predicate<ServerInfo>() {
            @Override
            public boolean apply(ServerInfo input) {
                return input.canAccess(sender);
            }
        }), new Function<ServerInfo, String>() {
            @Override
            public String apply(ServerInfo input) {
                return input.getName();
            }
        });
    }
}
