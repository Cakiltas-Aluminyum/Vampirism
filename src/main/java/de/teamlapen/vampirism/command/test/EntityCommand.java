package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class EntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("entity")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return entity(context.getSource(), context.getSource().getPlayerOrException());
                });
    }

    private static int entity(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        List<Entity> l = asPlayer.getCommandSenderWorld().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity entity : l) {
            if (entity instanceof CreatureEntity) {
                ResourceLocation id = Helper.getIDSafe(entity.getType());
                commandSource.sendSuccess(new StringTextComponent(id.toString()), true);
            } else {
                commandSource.sendSuccess(new TranslationTextComponent("Not biteable %s", entity.getClass().getName()), true);
            }
        }
        return 0;
    }
}
