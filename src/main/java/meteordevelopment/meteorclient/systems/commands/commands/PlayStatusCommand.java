/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.utils.misc.PlayStatusEntry;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PlayStatusCommand extends Command {
    public PlayStatusCommand() {
        super("ps", "Lets you show the play status of your friends.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (!PlayStatus.get().enabled) return 0;

            var playStatusEntries = PlayStatus.get().getPlayStatusEntries();
            if (playStatusEntries == null || playStatusEntries.length == 0) return 0;

            for (PlayStatusEntry playStatusEntry : playStatusEntries) {
                info("%s: %s, %s, %s", playStatusEntry.name, playStatusEntry.posX, playStatusEntry.posY, playStatusEntry.posZ);
            }

            return SINGLE_SUCCESS;
        });
    }
}
