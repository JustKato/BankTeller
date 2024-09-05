package com.danlegt.bankteller.events;

import com.danlegt.bankteller.BankTeller;
import com.danlegt.bankteller.models.Teller;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class TellerEvent implements Listener {

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

        // Cancel the event since we will never actually move items
        ev.setCancelled(true);

        // Check the clicked item
        var clickedItem = ev.getCurrentItem();
        var clickedMeta = clickedItem.getItemMeta();
        var clickedPc   = clickedMeta.getPersistentDataContainer();

        if ( clickedPc.has(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.DOUBLE) ) {
            var withdrawalAmount = clickedPc.get(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.DOUBLE);

            BankTeller.me.getServer().broadcastMessage("Amount to subtract: " + withdrawalAmount);
        } else if ( clickedPc.has(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.STRING) ) {
            var withdrawalType = clickedPc.get(Teller.getTellerInventoryBankNoteNameSpace(), PersistentDataType.STRING);

            if ( withdrawalType.equals("custom_amount") ) {
                // TODO: Withdraw Custom Amount via ChatInput
                BankTeller.me.getServer().broadcastMessage("Amount to subtract: " + ChatColor.GOLD + "?");
            }
        }


    }

}
