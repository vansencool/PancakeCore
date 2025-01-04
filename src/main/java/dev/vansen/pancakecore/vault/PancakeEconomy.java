package dev.vansen.pancakecore.vault;

import dev.vansen.pancakecore.PancakeCore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

@SuppressWarnings("all")
public class PancakeEconomy implements Economy {

    public double getPlayerBalance(String uuid) {
        try {
            return Double.parseDouble(PancakeCore.sqliteEconomy()
                    .read()
                    .table("economy")
                    .column("balance")
                    .where("uuid", uuid)
                    .fetch().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean playerExists(String uuid) {
        return PancakeCore.sqliteEconomy()
                .read()
                .table("economy")
                .column("balance")
                .where("uuid", uuid)
                .fetch() != null;
    }

    public void updateBalance(String uuid, double newBalance) {
        if (!playerExists(uuid)) {
            PancakeCore.sqliteEconomy()
                    .add("economy")
                    .value("uuid", uuid)
                    .value("balance", newBalance)
                    .insert();
            return;
        }

        PancakeCore.sqliteEconomy()
                .update("economy")
                .where("uuid", uuid)
                .set("balance", newBalance)
                .execute();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "PancakeEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("$%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return playerExists(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return playerExists(player.getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return getPlayerBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getPlayerBalance(player.getUniqueId().toString());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getPlayerBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getPlayerBalance(player.getUniqueId().toString());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getPlayerBalance(player.getUniqueId().toString()) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        String uuid = player.getUniqueId().toString();
        double currentBalance = getPlayerBalance(uuid);
        if (currentBalance < amount) {
            return new EconomyResponse(amount, currentBalance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
        updateBalance(uuid, currentBalance - amount);
        return new EconomyResponse(amount, currentBalance - amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        String uuid = player.getUniqueId().toString();
        double currentBalance = getPlayerBalance(uuid);
        updateBalance(uuid, currentBalance + amount);
        return new EconomyResponse(amount, currentBalance + amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        PancakeCore.sqliteEconomy()
                .add("economy")
                .value("uuid", player.getUniqueId().toString())
                .value("balance", 0)
                .insert();
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(playerName));
    }
}