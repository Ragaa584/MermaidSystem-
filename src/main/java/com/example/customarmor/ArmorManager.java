/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.inventory.ItemStack
 */
package com.example.customarmor;

import com.example.customarmor.ArmorPiece;
import com.example.customarmor.CustomArmorPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public final class ArmorManager {
    private final CustomArmorPlugin plugin;
    private final Map<String, ArmorPiece> pieces = new LinkedHashMap<String, ArmorPiece>();

    ArmorManager(CustomArmorPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.pieces.clear();
        ConfigurationSection root = this.plugin.getConfig().getConfigurationSection("armor");
        if (root == null) {
            this.plugin.getLogger().warning("No 'armor' section in config.yml.");
            return;
        }
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            try {
                ArmorPiece piece = ArmorPiece.fromConfig(id, sec);
                this.pieces.put(id.toLowerCase(), piece);
                this.plugin.getLogger().info("  - " + id + " (" + String.valueOf(piece.material()) + ", cmd=" + piece.customModelData() + ")");
            }
            catch (Exception ex) {
                this.plugin.getLogger().warning("Failed to load armor '" + id + "': " + ex.getMessage());
            }
        }
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.load();
    }

    public ArmorPiece get(String id) {
        return this.pieces.get(id.toLowerCase());
    }

    public Collection<ArmorPiece> all() {
        return Collections.unmodifiableCollection(this.pieces.values());
    }

    public List<String> ids() {
        return new ArrayList<String>(this.pieces.keySet());
    }

    public int count() {
        return this.pieces.size();
    }

    public ItemStack createItem(String id) {
        ArmorPiece piece = this.get(id);
        if (piece == null) {
            return null;
        }
        return piece.createItemStack();
    }
}

