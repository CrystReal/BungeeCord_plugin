package com.updg.SCBUNGEE.models;

import net.md_5.bungee.api.ProxyServer;

/**
 * Created by Alex
 * Date: 29.10.13  23:34
 */
public class SCPlayer {
    private boolean active = false;
    private int id;
    private String name;
    /*
     * 0 - Regular
     * 1 - Moder
     * 2 - Admin
     */
    private int rang;
    private int vip;
    private int project;


    public SCPlayer(int id, String name, int status, int vip, int active, int project) {
        this.id = id;
        this.name = name;
        this.rang = status;
        this.vip = vip;
        this.active = active == 1;
        this.project = project;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean canUseBanSystem() {
        return this.rang == 1 || this.rang == 2;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getStatus() {
        return rang;
    }

    public String getServer() {
        if (ProxyServer.getInstance().getPlayer(this.getName()) != null)
            return ProxyServer.getInstance().getPlayer(this.getName()).getServer().getInfo().getName();
        else
            return null;
    }

    public int getRang() {
        return rang;
    }

    public int getVip() {
        return vip;
    }

    public boolean isAdminOrModer() {
        return getRang() == 1 || getRang() == 2;
    }

    public boolean isAdmin() {
        return getRang() == 1;
    }

    public int getProject() {
        return project;
    }
}
