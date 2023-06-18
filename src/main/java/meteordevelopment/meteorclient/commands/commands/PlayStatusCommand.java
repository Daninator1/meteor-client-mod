/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.friends.PlayStatus;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.command.CommandSource;

import java.util.Arrays;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

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
            if (playStatusEntries == null || playStatusEntries.length == 0) {
                return 0;
            }

            var filteredEntries = Arrays.stream(playStatusEntries).filter(playStatusEntry ->
                !playStatusEntry.playerName.equals(mc.player.getName().getString()) &&
                    playStatusEntry.server.equals(Utils.getWorldName())).toList();

            if (filteredEntries.size() == 0) {
                error("No friends currently playing on this server.");
                return 0;
            }

            filteredEntries.forEach(playStatusEntry ->
                info("%s: %s, %s, %s (%s)",
                    playStatusEntry.name,
                    playStatusEntry.position.x,
                    playStatusEntry.position.y,
                    playStatusEntry.position.z,
                    playStatusEntry.dimension));

            return SINGLE_SUCCESS;
        });
    }
}
