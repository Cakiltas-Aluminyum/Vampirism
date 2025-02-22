package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class CurrentDimensionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("currentDimension")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return currentDimension(context, context.getSource().getPlayerOrException());
                });
    }

    private static int currentDimension(CommandContext<CommandSource> context, ServerPlayerEntity asPlayer) {
        context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.base.currentdimension.dimension", asPlayer.getCommandSenderWorld().dimension().location() + " (" + asPlayer.getServer().registryAccess().dimensionTypes().getKey(asPlayer.getCommandSenderWorld().dimensionType())), false);
        return 0;
    }
}
