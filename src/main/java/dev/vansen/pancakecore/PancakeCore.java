package dev.vansen.pancakecore;

import dev.vansen.commandutils.api.CommandAPI;
import dev.vansen.inventoryutils.InventoryUtils;
import dev.vansen.pancakecore.commands.CommandManager;
import dev.vansen.pancakecore.commands.economy.EconomyCommand;
import dev.vansen.pancakecore.commands.home.HomeCommand;
import dev.vansen.pancakecore.commands.staff.FeedCommand;
import dev.vansen.pancakecore.commands.staff.HealCommand;
import dev.vansen.pancakecore.commands.user.BalanceCommand;
import dev.vansen.pancakecore.commands.user.PayCommand;
import dev.vansen.pancakecore.events.EventManager;
import dev.vansen.pancakecore.events.block.BlockBreak;
import dev.vansen.pancakecore.events.block.BlockPlace;
import dev.vansen.pancakecore.placeholders.Placeholders;
import dev.vansen.pancakecore.vault.PancakeEconomy;
import net.milkbowl.vault.economy.Economy;
import net.vansen.noksdb.NoksDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@SuppressWarnings("unused")
public final class PancakeCore extends JavaPlugin {
    private static PancakeCore instance;
    private static Economy economy;
    private static NoksDB storeEconomy;
    private static NoksDB storeHomes;

    public static NoksDB storeEconomy() {
        return storeEconomy;
    }

    public static NoksDB storeHomes() {
        return storeHomes;
    }

    public static PancakeEconomy economy() {
        return (PancakeEconomy) economy;
    }

    /**
     * Plugin instance.
     *
     * @return Plugin instance
     */
    public static PancakeCore plugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        CommandAPI.set(this);
        InventoryUtils.init(this);

        storeEconomy = NoksDB.builder()
                .autoSaveAsync(true)
                .requireClassRegistration(false)
                .storageFile(new File(getDataFolder(), "economy/economy.dat"))
                .build();

        storeHomes = NoksDB.builder()
                .autoSaveAsync(true)
                .requireClassRegistration(false)
                .storageFile(new File(getDataFolder(), "homes/homes.dat"))
                .build();

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServicesManager().register(Economy.class, new PancakeEconomy(), this, ServicePriority.Highest);
            economy = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders().register();
        }

        events();
        commands();
    }

    private void events() {
        EventManager.register(BlockBreak.class, BlockPlace.class);
    }

    private void commands() {
        CommandManager.register(
                HomeCommand.class,
                EconomyCommand.class,
                FeedCommand.class,
                BalanceCommand.class,
                PayCommand.class,
                HealCommand.class);
    }
}
