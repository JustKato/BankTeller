package com.danlegt.bankteller;

import com.danlegt.bankteller.command.TellerCommand;
import com.danlegt.bankteller.events.TellerEvent;
import com.danlegt.bankteller.util.BankNoteManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BankTeller extends JavaPlugin {

    public static BankTeller me = null;

    private static Economy econ = null;

    @Override
    public void onEnable() {
        me = this;

        setupEconomy();
        registerCommands();
        registerEvents();
        BankNoteManager.loadData();
    }

    public static Economy getEconomy() {
        return econ;
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }

        econ = rsp.getProvider();
    }

    private void registerCommands() {
        final TellerCommand tellerCommand = new TellerCommand();

        this.getCommand("bankteller").setExecutor(tellerCommand);
        this.getCommand("bankteller").setTabCompleter(tellerCommand);
    }

    private void registerEvents() {
        final TellerEvent tellerEvent = new TellerEvent();

        this.getServer().getPluginManager().registerEvents(tellerEvent, this);
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }
}
