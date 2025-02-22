package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class DebugGenCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("debugGen")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return debugGen(context.getSource());
                });
    }

    private static int debugGen(CommandSource commandSource) {
        if (VampirismWorldGen.debug) {
            VampirismWorldGen.debug = false;
            commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.gen_debug.false"), true);
        } else {
            VampirismWorldGen.debug = true;
            commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.gen_debug.true"), true);
        }
        return 0;
    }
}
