/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.accounts;

public class ProfileResponse {
    public Property[] properties;

    public String getPropertyValue(String name) {
        for (Property property : properties) {
            if (property.name.equals(name)) return property.id;
        }

        return null;
    }

    public static class Property {
        public String name;
        public String id;
    }
}
