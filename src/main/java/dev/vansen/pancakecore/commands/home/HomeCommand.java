package dev.vansen.pancakecore.commands.home;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.argument.CommandArgument;
import dev.vansen.commandutils.subcommand.SubCommand;
import dev.vansen.pancakecore.homes.HomeManager;

import java.util.Objects;

public class HomeCommand {

    public HomeCommand() {
        CommandUtils.command("home")
                .subCommand(SubCommand.of("set")
                        .argument(CommandArgument.integer("section")
                                .defaultExecute(context -> {
                                    HomeManager.createHome(context.player(), context.argInt("section"), context.location());
                                    context.response("<#87ff93>Home " + context.argInt("section") + " set!");
                                })))
                .subCommand(SubCommand.of("tp")
                        .argument(CommandArgument.integer("section")
                                .defaultExecute(context -> {
                                    if (!HomeManager.isSet(context.player(), context.argInt("section"))) {
                                        context.response("<#ff6183>Home " + context.argInt("section") + " is not set!");
                                        return;
                                    }
                                    context.teleportAsync(Objects.requireNonNull(HomeManager.getHome(context.player(), context.argInt("section"))).getLocation());
                                    context.response("<#87ff93>You teleported to home " + context.argInt("section"));
                                })))
                .register();
    }
}
