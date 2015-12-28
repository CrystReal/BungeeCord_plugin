package com.updg.SCBUNGEE.utils;

import com.updg.SCBUNGEE.scbungee;

/**
 * Created by Alex
 * Date: 14.12.13  23:38
 */
public class L {
    public static void $(String str) {
        if(scbungee.getInstance().debug)
        System.out.println("[SC BUNGEE] " + str);
    }
}
