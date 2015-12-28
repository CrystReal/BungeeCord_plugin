package com.updg.SCBUNGEE.models.enums;

/**
 * Created by Alex
 * Date: 16.12.13  12:52
 */
public enum BanType {
    PERM_BAN(1), PERM_IP_BAN(2), TEMP_BAN(3), TEMP_IP_BAN(4), PERM_MUTE(5), TEMP_MUTE(6), WARN(7), KICK(8);

    private final int value;

    private BanType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
