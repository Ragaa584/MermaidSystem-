/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.example.customarmor;

import com.example.customarmor.ArmorManager;
import com.example.customarmor.CustomArmorCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomArmorPlugin
extends JavaPlugin {
    private static CustomArmorPlugin instance;
    private ArmorManager armorManager;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.armorManager = new ArmorManager(this);
        this.armorManager.load();
        this.getCommand("customarmor").setExecutor((CommandExecutor)new CustomArmorCommand(this));
        this.getCommand("customarmor").setTabCompleter((TabCompleter)new CustomArmorCommand(this));
        this.getLogger().info("Loaded " + this.armorManager.count() + " custom armor pieces.");
    }

    public void onDisable() {
        this.getLogger().info("CustomArmorPlugin disabled.");
    }

    public static CustomArmorPlugin get() {
        return instance;
    }

    public ArmorManager armorManager() {
        return this.armorManager;
    }
}

