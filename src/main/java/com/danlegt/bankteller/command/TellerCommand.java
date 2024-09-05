package com.danlegt.bankteller.command;

import com.danlegt.bankteller.models.Teller;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

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
        player.sendMessage(ChatColor.GREEN + "/teller spawn"  + ChatColor.WHITE + " - Spawns a new bank teller.");
        player.sendMessage(ChatColor.GREEN + "/teller remove" + ChatColor.WHITE + " - Removes the bank teller you're looking at or within 2 blocks.");
        player.sendMessage(ChatColor.GREEN + "/teller help"   + ChatColor.WHITE + " - Shows this help message.");
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
