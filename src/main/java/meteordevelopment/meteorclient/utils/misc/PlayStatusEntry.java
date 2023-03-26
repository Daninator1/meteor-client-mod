/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

public class PlayStatusEntry {
    public String name;
    public String playerName;
    public String server;
    public double posX;
    public double posY;
    public double posZ;

    public PlayStatusEntry(String name, String playerName, String server, double posX, double posY, double posZ) {

        this.name = name;
        this.playerName = playerName;
        this.server = server;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }
}
