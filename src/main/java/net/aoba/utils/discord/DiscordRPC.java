package net.aoba.utils.discord;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class DiscordRPC extends Structure {
    public String largeImageKey;
    public String largeImageText;
    public String smallImageText;
    public String partyPrivacy;
    public long startTimestamp;
    public String button_label_1;
    public int instance;
    public String partyId;
    public int partySize;
    public long endTimestamp;
    public String details;
    public String joinSecret;
    public String spectateSecret;
    public String smallImageKey;
    public String matchSecret;
    public String button_url_2;
    public String button_label_2;
    public String state;
    public String button_url_1;
    public int partyMax;

    public DiscordRPC() {
        this.setStringEncoding("UTF-8");
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "partyPrivacy", "matchSecret", "joinSecret", "spectateSecret", "button_label_1", "button_url_1", "button_label_2", "button_url_2", "instance");
    }
}