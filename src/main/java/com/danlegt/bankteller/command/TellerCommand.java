package com.danlegt.bankteller.command;

import com.danlegt.bankteller.BankTeller;
import com.danlegt.bankteller.models.Teller;
import com.danlegt.bankteller.util.BankNoteManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TellerCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        // Check if the command sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can execute the bankteller command");
            return true;
        }

        Player player = (Player) commandSender;

        // Handle subcommands
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            showHelp(player);
        } else if (args[0].equalsIgnoreCase("spawn")) {
            spawnTeller(player);
        } else if (args[0].equalsIgnoreCase("inspect")) {
            inspectTellerNote(player);
        } else if (args[0].equalsIgnoreCase("remove")) {
            removeTeller(player);
        } else {
            player.sendMessage(ChatColor.RED + "Unknown subcommand. Use /teller help.");
        }

        return true; // Returning true indicates the command was executed successfully
    }

    // Show help for /teller help
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "BankTeller Plugin Help:");
        player.sendMessage(ChatColor.GREEN + "/teller spawn   "  + ChatColor.WHITE + " - Spawns a new bank teller.");
        player.sendMessage(ChatColor.GREEN + "/teller remove  "  + ChatColor.WHITE + " - Removes the bank teller you're looking at or within 2 blocks.");
        player.sendMessage(ChatColor.GREEN + "/teller inspect "  + ChatColor.WHITE + " - Inspect the BankNote from your hand to check for duplicates and extra information.");
        player.sendMessage(ChatColor.GREEN + "/teller help    "  + ChatColor.WHITE + " - Shows this help message.");
    }

    // Show help for /teller help
    private void inspectTellerNote(Player player) {
        // Get the item in the player's main hand
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemMeta meta = hand.getItemMeta();

        if (meta == null) {
            Teller.sendTellerMessage(player, ChatColor.RED + "You are not holding a banknote.");
            return;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Check if the item is a valid banknote
        if (!data.has(Teller.getBankNoteNamespaceKey(), PersistentDataType.DOUBLE)) {
            Teller.sendTellerMessage(player, ChatColor.RED + "This is not a valid banknote.");
            return;
        }

        // Get the withdrawal amount
        Double amount = data.get(Teller.getBankNoteNamespaceKey(), PersistentDataType.DOUBLE);

        // Get the UUID (anti-dupe ID)
        String uuid = data.get(new NamespacedKey(BankTeller.me, "banknote_value_ID_ANTIDUPE_SAFETY"), PersistentDataType.STRING);

        // Get the author (creator) of the banknote
        String author = meta.getLore() != null && meta.getLore().size() >= 3 ? ChatColor.stripColor(meta.getLore().get(2).split(":")[1].trim()) : "Unknown";

        // Get the creation date
        String creationDate = meta.getLore() != null && meta.getLore().size() >= 2 ? ChatColor.stripColor(meta.getLore().get(1).split(":")[1].trim()) : "Unknown";

        // Check if the UUID is null (should never be null if created properly)
        if (uuid == null) {
            Teller.sendTellerMessage(player, ChatColor.RED + "This banknote is invalid or missing data.");
            return;
        }

        // Format the information nicely for chat
        player.sendMessage(ChatColor.DARK_GRAY + "-------------------------------------");
        player.sendMessage(ChatColor.GOLD + "BankNote Information:");
        player.sendMessage(ChatColor.WHITE + " - " + ChatColor.GRAY + "Value: " + ChatColor.GREEN + amount);
        player.sendMessage(ChatColor.WHITE + " - " + ChatColor.GRAY + "Creation Date: " + ChatColor.YELLOW + creationDate);
        player.sendMessage(ChatColor.WHITE + " - " + ChatColor.GRAY + "Author: " + ChatColor.LIGHT_PURPLE + author);
        player.sendMessage(ChatColor.WHITE + " - " + ChatColor.GRAY + "UUID: " + ChatColor.AQUA + uuid);

        // Check if the note has already been redeemed
        if (BankNoteManager.isUUIDRedeemed(uuid)) {
            player.sendMessage(ChatColor.RED + " - " + ChatColor.BOLD + "Warning: " + ChatColor.RED + "This banknote has already been redeemed!");
        } else {
            player.sendMessage(ChatColor.GREEN + " - " + "This banknote is valid for redemption.");
        }
        player.sendMessage(ChatColor.DARK_GRAY + "-------------------------------------");
    }

    // Spawn a new BankTeller at the player's location
    private void spawnTeller(Player player) {
        Villager teller = Teller.spawnTeller(player.getLocation());

        player.sendMessage(ChatColor.GREEN + "BankTeller has been spawned.");
    }

    // Remove the BankTeller the player is looking at or within 2-block radius
    private void removeTeller(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(2, 2, 2);
        boolean didRemove = false;

        for (Entity entity : nearbyEntities) {
            if ( didRemove ) break;

            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                if (Teller.isBankTeller(villager)) {
                    villager.remove();
                    player.sendMessage(ChatColor.GREEN + "BankTeller removed.");
                    didRemove = true;
                }
            }
        }

        if ( !didRemove ) {
            player.sendMessage(ChatColor.RED + "No BankTeller found within 2 blocks.");
        }
    }

    // Implement tab completion for the subcommands
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {

            if ("help".startsWith(args[0].toLowerCase())) {
                completions.add("help");
            }

            if ("inspect".startsWith(args[0].toLowerCase())) {
                completions.add("inspect");
            }

            if ("spawn".startsWith(args[0].toLowerCase())) {
                completions.add("spawn");
            }

            if ("remove".startsWith(args[0].toLowerCase())) {
                completions.add("remove");
            }
        }
        return completions;
    }
}
