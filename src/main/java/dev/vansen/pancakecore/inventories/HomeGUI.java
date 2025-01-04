package dev.vansen.pancakecore.inventories;

import dev.vansen.inventoryutils.inventory.FairInventory;
import dev.vansen.inventoryutils.inventory.InventorySize;
import dev.vansen.inventoryutils.item.ItemBuilder;
import dev.vansen.pancakecore.homes.HomeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class HomeGUI {

    @SuppressWarnings("ConstantConditions")
    public static void open(@NotNull Player player) {
        FairInventory inventory = FairInventory.create("ʜᴏᴍᴇs", InventorySize.rows(4));

        for (int homeIndex = 1; homeIndex <= 5; homeIndex++) {
            boolean isSet = HomeManager.isSet(player, homeIndex);
            Material bedMaterial = isSet ? Material.LIGHT_BLUE_BED : Material.GRAY_BED;
            Material dyeMaterial = isSet ? Material.BLUE_DYE : Material.GRAY_DYE;
            Component name = MiniMessage.miniMessage().deserialize(isSet ?
                    "<!i><#00a4fc>ʜᴏᴍᴇ " + homeIndex : "<!i><#cfcfcf>ɴᴏ ʜᴏᴍᴇ sᴇᴛ");
            List<Component> lore;
            List<Component> lore2 = List.of(MiniMessage.miniMessage().deserialize(isSet ? "<!i><#fcfcfc>Click to teleport to your home" : "<!i><#cfcfcf>-<#fcfcfc> Click to create a home"));

            if (isSet) {
                lore = List.of(
                        MiniMessage.miniMessage().deserialize("<!i><#fcfcfc>Click to delete this home")
                );
            } else {
                lore = List.of(
                        MiniMessage.miniMessage().deserialize("<!i><#fcfcfc>Click to create a home")
                );
            }

            int slot = 12 + homeIndex - 1;
            int finalHomeIndex = homeIndex;
            inventory.set(slot, ItemBuilder.of(bedMaterial)
                    .name(name)
                    .lore(lore2)
                    .build()
                    .click(event -> {
                        if (isSet) {
                            try {
                                player.teleportAsync(Objects.requireNonNull(HomeManager.getHome(player, finalHomeIndex)).getLocation());
                                player.sendActionBar(MiniMessage.miniMessage().deserialize("<#87ff93>You teleported to home " + finalHomeIndex));
                                event.setCancelled(true);
                            } catch (Exception e) {
                                player.sendRichMessage("<#ff6183>Failed to teleport to home " + finalHomeIndex);
                                event.setCancelled(true);
                            }
                        } else {
                            if (player.getLocation().getWorld().getName().equals("spawn")) {
                                player.sendRichMessage("<#ff6183>You can't create a home at spawn!");
                                event.setCancelled(true);
                                return;
                            }
                            HomeManager.createHome(player, finalHomeIndex, player.getLocation());
                            player.sendRichMessage("<#87ff93>Home " + finalHomeIndex + " set!");
                            player.closeInventory();
                            open(player);
                            event.setCancelled(true);
                        }
                    }));
            int dyeSlot = slot + 9;
            inventory.set(dyeSlot, ItemBuilder.of(dyeMaterial)
                    .name(name)
                    .lore(lore)
                    .build()
                    .click(event -> {
                        if (event.getCurrentItem().getType() == Material.BLUE_DYE) {
                            event.setCancelled(true);
                            player.closeInventory();
                            openWarning(player, finalHomeIndex);
                            return;
                        }
                        if (isSet) {
                            try {
                                player.teleportAsync(Objects.requireNonNull(HomeManager.getHome(player, finalHomeIndex)).getLocation());
                                player.sendActionBar(MiniMessage.miniMessage().deserialize("<#87ff93>You teleported to home " + finalHomeIndex));
                                event.setCancelled(true);
                            } catch (Exception e) {
                                player.sendRichMessage("<#ff6183>Failed to teleport to home " + finalHomeIndex);
                                event.setCancelled(true);
                            }
                        } else {
                            HomeManager.createHome(player, finalHomeIndex, player.getLocation());
                            player.sendRichMessage("<#87ff93>Home " + finalHomeIndex + " created!");
                            player.closeInventory();
                            open(player);
                            event.setCancelled(true);
                        }
                    }));
        }

        inventory.show(player);
    }

    public static void openWarning(Player player, int index) {
        FairInventory inventory = FairInventory.create("Confirm Delete", InventorySize.rows(3));

        inventory.set(12, ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .name(MiniMessage.miniMessage().deserialize("<!i><#fc0000>ᴄᴀɴᴄᴇʟ"))
                .lore(MiniMessage.miniMessage().deserialize("<!i><#fcfcfc>Click to cancel"))
                .build()
                .click(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    open(player);
                }));

        inventory.set(14, ItemBuilder.of(Material.BLUE_DYE)
                .name(MiniMessage.miniMessage().deserialize("<!i><#00a4fc>ʜᴏᴍᴇ " + index))
                .build()
                .click(event -> {
                    event.setCancelled(true);
                    open(player);
                }));

        inventory.set(16, ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
                .name(MiniMessage.miniMessage().deserialize("<!i><#00fc00>ᴄᴏɴꜰɪʀᴍ"))
                .lore(MiniMessage.miniMessage().deserialize("<!i><#fcfcfc>Click to delete"))
                .build()
                .click(event -> {
                    event.setCancelled(true);
                    HomeManager.deleteHome(player, index);
                    player.closeInventory();
                    open(player);
                }));

        inventory.show(player);
    }
}
