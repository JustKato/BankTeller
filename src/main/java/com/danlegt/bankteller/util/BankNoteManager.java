package com.danlegt.bankteller.util;

import com.danlegt.bankteller.BankTeller;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BankNoteManager {
    private static final File bankNoteFile = new File(BankTeller.me.getDataFolder(), "redeemed_banknotes.json");
    private static final Set<String> redeemedUUIDs = new HashSet<>();
    private static final Map<String, Long> uuidTimestamps = new HashMap<>();
    private static final long WEEK_IN_MILLIS = 7L * 24 * 60 * 60 * 1000;

    // Load data when plugin starts
    public static void loadData() {
        if (!bankNoteFile.exists()) {
            try {
                bankNoteFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<String> lines = Files.readAllLines(bankNoteFile.toPath());
                lines.forEach(line -> {
                    String[] parts = line.split(",");
                    redeemedUUIDs.add(parts[0]);
                    uuidTimestamps.put(parts[0], Long.parseLong(parts[1]));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Schedule clean-up task
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanUpOldUUIDs();
            }
        }.runTaskTimerAsynchronously(BankTeller.me, 0, 20 * 60 * 60 * 24); // Run every 24 hours
    }

    // Save UUID to file and cache
    public static void saveUUID(String uuid) {
        if (!redeemedUUIDs.contains(uuid)) {
            redeemedUUIDs.add(uuid);
            uuidTimestamps.put(uuid, System.currentTimeMillis());
            try {
                Files.write(bankNoteFile.toPath(), Arrays.asList(uuid + "," + System.currentTimeMillis()), java.nio.file.StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Check if UUID exists in the cache
    public static boolean isUUIDRedeemed(String uuid) {
        return redeemedUUIDs.contains(uuid);
    }

    // Notify administrators of a duplicate attempt
    public static void notifyAdmins(Player player, String uuid) {
        Bukkit.getOnlinePlayers().stream()
                .filter(Player::isOp)
                .forEach(admin -> admin.sendMessage(ChatColor.RED + "Player " + player.getName() + " tried to redeem a duplicate banknote with UUID: " + uuid));
    }

    // Clean up UUIDs older than 7 days
    private static void cleanUpOldUUIDs() {
        long currentTime = System.currentTimeMillis();
        List<String> toRemove = uuidTimestamps.entrySet().stream()
                .filter(entry -> currentTime - entry.getValue() > WEEK_IN_MILLIS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        toRemove.forEach(uuid -> {
            redeemedUUIDs.remove(uuid);
            uuidTimestamps.remove(uuid);
        });

        // Rewrite the file after cleaning
        List<String> toWrite = redeemedUUIDs.stream()
                .map(uuid -> uuid + "," + uuidTimestamps.get(uuid))
                .collect(Collectors.toList());

        try {
            Files.write(bankNoteFile.toPath(), toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
