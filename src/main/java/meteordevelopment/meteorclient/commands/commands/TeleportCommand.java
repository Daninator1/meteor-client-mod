/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;

public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("teleport", "Teleports you to a given position.", "tp");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("pos", Vec3ArgumentType.vec3()).executes(context -> {
            mc.player.setPosition(context.getArgument("pos", PosArgument.class).getPos(mc.player.getCommandSource(mc.getServer().getOverworld())));
            return SINGLE_SUCCESS;
        }));
    }
}
