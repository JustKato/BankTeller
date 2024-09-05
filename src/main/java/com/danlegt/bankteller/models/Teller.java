package com.danlegt.bankteller.models;

import com.danlegt.bankteller.BankTeller;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Teller {

    public static Villager spawnTeller(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        villager.setPersistent(true);
        villager.setCustomName(ChatColor.GREEN + "BankTeller");
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);

        villager.setProfession(Villager.Profession.LIBRARIAN);
        villager.setVillagerType(Villager.Type.DESERT);

        Random rand = new Random();
        for ( int i = 0; i < 5; i++ ) {
            loc.getWorld().spawnParticle(Particle.CLOUD, loc, rand.nextInt(10), 0, rand.nextDouble() / 3000, 0);
        }

        loc.getWorld().playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);

        // Mark this villager as a "BankTeller" using PersistentDataContainer
        PersistentDataContainer data = villager.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(BankTeller.me, "bankteller");
        data.set(key, PersistentDataType.STRING, "true");

        return villager;
    }



    // Check if a villager is a BankTeller using PersistentDataContainer
    public static boolean isBankTeller(Villager villager) {
        PersistentDataContainer data = villager.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(BankTeller.me, "bankteller");
        return data.has(key, PersistentDataType.STRING);
    }

    public static NamespacedKey getTellerInventoryBankNoteNameSpace() {
        return new NamespacedKey(BankTeller.me, "bankteller_inventory");
    }

    public static Inventory getTellerInventory(Villager teller) {
        Inventory inv = Bukkit.createInventory(teller, InventoryType.CHEST, ChatColor.GREEN + "BankTeller");

        // Block out with stained glass panes for pretty inventory
        ItemStack blockedSlot = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        for ( int i = 0; i < inv.getSize(); i++ ) {
            inv.setItem(i, blockedSlot);
        }

        var price1 = 5d;
        var price2 = 10d;
        var price3 = 50d;

        var possibleWithdrawalAmounts = Arrays.asList(price1, price2, price3);

        for ( int i = 0; i < 3; i++ ) {
            var val = possibleWithdrawalAmounts.get(i);
            ItemStack subTractItem = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = subTractItem.getItemMeta();

            meta.setDisplayName( ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + val + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + " BankNote");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Withdraw " + ChatColor.WHITE + val + ChatColor.GRAY + " into a BankNote",
                    ChatColor.GRAY + "from your account"
            ));

            var persistentDataContainer = meta.getPersistentDataContainer();
            persistentDataContainer.set(getTellerInventoryBankNoteNameSpace(), PersistentDataType.DOUBLE, val);

            subTractItem.setItemMeta(meta);

            inv.setItem(10 + i * 2, subTractItem);
        }


        ItemStack subTractItem = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = subTractItem.getItemMeta();

        meta.setDisplayName( ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "?" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + " BankNote");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Withdraw a custom amount into a BankNote",
                ChatColor.GRAY + "from your account"
        ));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DENSITY, 1, true);

        var persistentDataContainer = meta.getPersistentDataContainer();
        persistentDataContainer.set(getTellerInventoryBankNoteNameSpace(), PersistentDataType.STRING, "custom_amount");

        subTractItem.setItemMeta(meta);

        inv.setItem(16, subTractItem);


        return inv;
    }

}
