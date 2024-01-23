/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import net.minecraft.command.CommandSource;

import java.util.Arrays;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PlayStatusCommand extends Command {
    public PlayStatusCommand() {
        super("play-status", "Lets you show the play status of your friends on this server.", "ps");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (!PlayStatus.get().enabled) {
                error("Play status is disabled.");
                return 0;
            }

            var playStatusEntries = PlayStatus.get().fetchPlayStatusEntries();

            if (playStatusEntries.length == 0) {
                error("No friends currently playing on this server.");
                return 0;
            }

            Arrays.stream(playStatusEntries).forEach(playStatusEntry ->
                info("%s: %.0f, %.0f, %.0f (%s)",
                    playStatusEntry.name,
                    playStatusEntry.position.x,
                    playStatusEntry.position.y,
                    playStatusEntry.position.z,
                    playStatusEntry.dimension));

            return SINGLE_SUCCESS;
        });
    }
}
