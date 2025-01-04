package dev.vansen.pancakecore.commands.home;

import dev.vansen.commandutils.CommandUtils;
import dev.vansen.commandutils.command.CheckType;
import dev.vansen.pancakecore.inventories.HomeGUI;

public class HomeCommand {

    public HomeCommand() {
        CommandUtils.command("home")
                .aliases("homes")
                .defaultExecute(context -> {
                    context.check(CheckType.PLAYER);
                    HomeGUI.open(context.player());
                })
                .register();
    }
}
