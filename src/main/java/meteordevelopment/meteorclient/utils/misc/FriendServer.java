/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

public class FriendServer {
    public String friendName;
    public String playerName;
    public String serverHost;
    public Integer serverPort;

    public String getServerAddress() {
        return this.serverHost + ":" + this.serverPort;
    }
}
