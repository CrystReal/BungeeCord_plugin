package com.updg.SCBUNGEE.utils;

/**
 * Created by Alex
 * Date: 18.06.13  19:18
 */

import java.io.ByteArrayOutputStream;

import net.md_5.bungee.api.config.ServerInfo;

public class SendPluginMessage
        implements Runnable {
    private final String channel;
    private final ByteArrayOutputStream bytes;
    private final ServerInfo server;

    public SendPluginMessage(String channel, ServerInfo server, ByteArrayOutputStream bytes) {
        this.channel = channel;
        this.bytes = bytes;
        this.server = server;
    }

    public void run() {
        this.server.sendData(this.channel, this.bytes.toByteArray());
    }
}