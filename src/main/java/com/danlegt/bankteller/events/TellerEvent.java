package com.danlegt.bankteller.events;

import com.danlegt.bankteller.BankTeller;
import com.danlegt.bankteller.models.Teller;
import com.danlegt.bankteller.util.BankNoteManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TellerEvent implements Listener {

    public static List<Player> CUSTOM_WITHDRAWAL_PLAYERS = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTellerDamange(EntityDamageEvent event) {
        var ent = event.getEntity();

        if (ent instanceof Villager v) {
            if (Teller.isBankTeller(v)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTellerInteract(PlayerInteractEntityEvent event) {
        var ent = event.getRightClicked();

        if ( ent instanceof Villager v ) {
            if ( Teller.isBankTeller(v) ) {
                v.getLocation().getWorld().playSound(v.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1f, 1f);
                event.setCancelled(true);

                // Open teller inventory
                event.getPlayer().openInventory(Teller.getTellerInventory(v));

            }
        }
    }

    // Teller inventory Events

    @EventHandler
    public void onTellerInventoryClick(InventoryClickEvent ev) {
        // Check for the holder
        var holder = ev.getInventory().getHolder();

        // Check if the owner is a villager
        if ( !(holder instanceof Villager) ) {
            return;
        }

        // Make sure it's a teller
        if ( !Teller.isBankTeller((Villager) holder) ) {
            return;
        }

        var p = (Player) ev.getViewers().get(0);

        // Cancel the event since we will never actually move items
        ev.setCancelled(true);

        // Check the clicked item
        var clickedItem = ev.getCurrentItem();
        var clickedMeta = clickedItem.getItemMeta();
        var clickedPc   = clickedMeta.getPersistentDataContainer();

        if ( clickedPc.has(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.DOUBLE) ) {
            var withdrawalAmount = clickedPc.get(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.DOUBLE);

            if ( withdrawalAmount != null ) {
                Teller.givePlayerBankNote(p, withdrawalAmount);
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Failed to get withdrawal amount from Teller inventory, this should be impossible, make sure the inventory withdrawal amount for the teller is an integer, floating point or double. Do not use any symbols, text or commas, only numeric values with dots(.) to delimit the decimals.");
                p.closeInventory();
            }

        } else if ( clickedPc.has(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.STRING) ) {
            var withdrawalType = clickedPc.get(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.STRING);

            if ( withdrawalType.equals("custom_amount") ) {
                CUSTOM_WITHDRAWAL_PLAYERS.add(p);
            }
            p.closeInventory();

            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "BankTeller" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "ENTER WITHDRAWAL AMOUNT: ");

            // Timeout Handling
            (new BukkitRunnable() {
                @Override
                public void run() {
                    if ( CUSTOM_WITHDRAWAL_PLAYERS.contains(p) ) {
                        CUSTOM_WITHDRAWAL_PLAYERS.remove(p);
                        Teller.sendTellerMessage(p, "Time expired, try again");
                    }
                }
            }).runTaskLater(BankTeller.me, 20 * 5);

        }


    }

    @EventHandler
    public void onBankNoteRedeem(PlayerInteractEvent ev) {
        var p = ev.getPlayer();

        // Check if sneaking
        if ( !p.isSneaking() ) {
            return;
        }

        // Not Right-Clicking
        if ( !ev.getAction().equals(Action.RIGHT_CLICK_AIR) && !ev.getAction().equals(Action.RIGHT_CLICK_BLOCK) ) {
            return;
        }

        // Check if item in hand is banknote
        ItemStack hand = p.getInventory().getItemInMainHand();
        ItemMeta meta = hand.getItemMeta();
        if ( meta == null )
            return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if ( !data.has(Teller.getBankNoteNamespaceKey(), PersistentDataType.DOUBLE) ) {
            return;
        }

        var noteUUID = data.get(new NamespacedKey(BankTeller.me, "banknote_value_ID_ANTIDUPE_SAFETY"), PersistentDataType.STRING);

        var withdrawalAmount = data.get(Teller.getBankNoteNamespaceKey(), PersistentDataType.DOUBLE);
        // Invalid Withdrawal Amount
        if ( withdrawalAmount == null || withdrawalAmount <= 0 || noteUUID == null ) {
            Bukkit.getLogger().log(Level.WARNING, "Player tried to withdraw invalid amount from a banknote, please investigate: " + p.getDisplayName() + " | " + p.getUniqueId());
            return;
        }

        // Check for duplicate UUID
        if (BankNoteManager.isUUIDRedeemed(noteUUID)) {
            Teller.sendTellerMessage(p, ChatColor.RED + "This banknote has already been redeemed!");
            BankNoteManager.notifyAdmins(p, noteUUID);
            return;
        }

        // Remove item from inv
        hand.setAmount(0);

        // Add that money to the player
        BankTeller.getEconomy().depositPlayer(p, withdrawalAmount);
        Teller.sendTellerMessage(p, ChatColor.WHITE + "Amount of " + withdrawalAmount + " has been redeemed");
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);

        // Save the UUID to prevent future redemptions
        BankNoteManager.saveUUID(noteUUID);
    }

    @EventHandler
    public void listenForCustomWithdrawal(AsyncPlayerChatEvent ev) {
        Player p = ev.getPlayer();
        if ( CUSTOM_WITHDRAWAL_PLAYERS.contains(p) ) {
            CUSTOM_WITHDRAWAL_PLAYERS.remove(p);

            // Try and parse message to double
            Double withdrawalAmount = null;

            try {
                withdrawalAmount = Double.valueOf(ev.getMessage());
            } catch ( Exception e ) {
                withdrawalAmount = null;
            }

            var playerBalance = BankTeller.getEconomy().getBalance(p);

            if ( withdrawalAmount == null || withdrawalAmount.isNaN() || withdrawalAmount < 10 ) {
                Teller.sendTellerMessage(p, ChatColor.RED + "Invalid withdrawal amount specified, please specify a real number.");
                return;
            }

            if ( withdrawalAmount > playerBalance ) {
                Teller.sendTellerMessage(p, ChatColor.RED + "Your account's balance is too low for that.");
                return;
            }

            ev.setCancelled(true);
            Teller.givePlayerBankNote(p, withdrawalAmount);
        }
    }

}
