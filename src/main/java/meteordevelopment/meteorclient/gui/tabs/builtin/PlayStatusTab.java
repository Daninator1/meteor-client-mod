/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayStatusTab extends Tab {
    public PlayStatusTab() {
        super("Play Status");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new PlayStatusScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof PlayStatusScreen;
    }

    private static class PlayStatusScreen extends WindowTabScreen {
        private final Settings settings = new Settings();

        public PlayStatusScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            SettingGroup sgGeneral = settings.getDefaultGroup();

            sgGeneral.add(
                new BoolSetting.Builder()
                    .name("enabled")
                    .description("Whether to enable sending/receiving play status.")
                    .defaultValue(false)
                    .onChanged(enabled -> PlayStatus.get().enabled = enabled)
                    .onModuleActivated(booleanSetting -> booleanSetting.set(PlayStatus.get().enabled))
                    .build()
            );

            sgGeneral.add(
                new StringSetting.Builder()
                    .name("name")
                    .description("Your name.")
                    .onChanged(name -> PlayStatus.get().name = name)
                    .onModuleActivated(stringSetting -> stringSetting.set(PlayStatus.get().name))
                    .build()
            );

            sgGeneral.add(
                new StringSetting.Builder()
                    .name("server")
                    .description("The server to use for play status.")
                    .onChanged(server -> PlayStatus.get().server = server)
                    .onModuleActivated(stringSetting -> stringSetting.set(PlayStatus.get().server))
                    .build()
            );

            sgGeneral.add(
                new StringSetting.Builder()
                    .name("api-key")
                    .description("The API key to use for the server.")
                    .onChanged(apiKey -> PlayStatus.get().apiKey = apiKey)
                    .onModuleActivated(stringSetting -> stringSetting.set(PlayStatus.get().apiKey))
                    .build()
            );

            settings.onActivated();
        }

        @Override
        public void initWidgets() {
            // Settings
            add(theme.settings(settings)).minWidth(400).expandX();
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(PlayStatus.get());
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(PlayStatus.get());
        }
    }
}
