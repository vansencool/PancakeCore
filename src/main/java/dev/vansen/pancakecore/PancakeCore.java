package dev.vansen.pancakecore;

import dev.vansen.commandutils.api.CommandAPI;
import dev.vansen.pancakecore.commands.economy.EconomyCommand;
import dev.vansen.pancakecore.commands.home.HomeCommand;
import dev.vansen.pancakecore.commands.user.BalanceCommand;
import dev.vansen.pancakecore.commands.user.PayCommand;
import dev.vansen.pancakecore.sql.SQLiteManager;
import dev.vansen.pancakecore.sql.field.FieldType;
import dev.vansen.pancakecore.vault.PancakeEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@SuppressWarnings("unused")
public final class PancakeCore extends JavaPlugin {
    private static PancakeCore instance;
    private static SQLiteManager sqliteEconomy;
    private static SQLiteManager sqliteHomes;
    private static Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        CommandAPI.set(this);

        sqliteEconomy = SQLiteManager.setup()
                .file(new File(getDataFolder(), "economy/economy.db"))
                .start();

        sqliteHomes = SQLiteManager.setup()
                .file(new File(getDataFolder(), "homes/homes.db"))
                .start();

        if (!sqliteEconomy().exists("economy")) {
            sqliteEconomy.list("economy")
                    .field("uuid", FieldType.STRING)
                    .field("balance", FieldType.DOUBLE)
                    .create();
        }
        if (!sqliteHomes.exists("homes")) {
            sqliteHomes.list("homes")
                    .field("uuid", FieldType.STRING)
                    .field("section", FieldType.INTEGER)
                    .field("location", FieldType.BLOB)
                    .create();
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServicesManager().register(Economy.class, new PancakeEconomy(), this, ServicePriority.Highest);
            economy = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
        }

        new EconomyCommand();
        new PayCommand();
        new BalanceCommand();
        new HomeCommand();
    }

    public static SQLiteManager sqliteEconomy() {
        return sqliteEconomy;
    }

    public static SQLiteManager sqliteHomes() {
        return sqliteHomes;
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
}
