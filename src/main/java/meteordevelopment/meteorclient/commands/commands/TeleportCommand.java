/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("teleport", "Teleports you to a given position.", "tp");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .then(argument("posX", IntegerArgumentType.integer())
                .then(argument("posY", IntegerArgumentType.integer())
                    .then(argument("posZ", IntegerArgumentType.integer())
                        .executes(context -> {
                            mc.player.setPosition(context.getArgument("posX", Integer.class), context.getArgument("posY", Integer.class), context.getArgument("posZ", Integer.class));
                            return SINGLE_SUCCESS;
                        }))));
    }
}
