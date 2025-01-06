package dev.vansen.pancakecore.commands.staff;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.command.CheckType;
import org.bukkit.attribute.Attribute;

import java.util.Objects;

public class HealCommand {

    public HealCommand() {
        CommandUtils.command("heal")
                .aliases("h")
                .permission("pancakecore.heal")
                .defaultExecute(context -> {
                    context.check(CheckType.PLAYER);
                    context.player().setHealth(Objects.requireNonNull(context.player().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                    context.player().setSaturation(20);
                    context.player().setFoodLevel(20);
                    context.player().setArrowsInBody(0);
                    context.response("<#87ff93>You were healed!");
                })
                .argument(CommandArgument.player("who")
                        .defaultExecute(context -> {
                            context.check(CheckType.PLAYER);
                            context.player().setHealth(Objects.requireNonNull(context.player().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                            context.player().setSaturation(20);
                            context.player().setFoodLevel(20);
                            context.player().setArrowsInBody(0);
                            context.response("<#87ff93>" + context.argPlayer("who").getName() + " was healed!");
                        }))
                .register();
    }
}
