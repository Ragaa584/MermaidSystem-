package com.example.customarmor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ArmorPiece {
    private final String id;
    private final String displayName;
    private final Material material;
    private final int customModelData;
    private final List<String> lore;
    private final Map<Enchantment, Integer> enchantments;
    private final boolean unbreakable;

    // NEW: which equipment asset + slot this piece uses for the worn-body look.
    private final NamespacedKey equipmentAsset;
    private final EquipmentSlot equipmentSlot;

    private ArmorPiece(String id, String displayName, Material material, int customModelData,
                        List<String> lore, Map<Enchantment, Integer> enchantments, boolean unbreakable,
                        NamespacedKey equipmentAsset, EquipmentSlot equipmentSlot) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.customModelData = customModelData;
        this.lore = lore;
        this.enchantments = enchantments;
        this.unbreakable = unbreakable;
        this.equipmentAsset = equipmentAsset;
        this.equipmentSlot = equipmentSlot;
    }

    static ArmorPiece fromConfig(String id, ConfigurationSection sec) {
        String displayName = sec.getString("display-name", id);
        String matName = sec.getString("material", "LEATHER_HELMET");
        Material material = Material.matchMaterial(matName);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material: " + matName);
        }
        int customModelData = sec.getInt("custom-model-data", 0);
        List<String> lore = sec.getStringList("lore");
        boolean unbreakable = sec.getBoolean("unbreakable", false);

        LinkedHashMap<Enchantment, Integer> enchants = new LinkedHashMap<>();
        ConfigurationSection enchSec = sec.getConfigurationSection("enchantments");
        if (enchSec != null) {
            for (String key : enchSec.getKeys(false)) {
                Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));
                if (ench == null) {
                    throw new IllegalArgumentException("Unknown enchantment: " + key);
                }
                int level = enchSec.getInt(key, 1);
                enchants.put(ench, level);
            }
        }

        // Which power does this piece belong to? Strip the known piece-type suffix
        // from the id to get the power name, e.g. "bloom_chestplate" -> "bloom",
        // "bloom_crown" -> "bloom". This is what points to the equipment asset file:
        // assets/customarmor/equipment/<power>.json
        String powerName = id.toLowerCase()
            .replace("_chestplate", "")
            .replace("_leggings", "")
            .replace("_helmet", "")
            .replace("_crown", "")
            .replace("_boots", "");
        // Allow an explicit override in config.yml (equipment-asset: xyz) if the
        // automatic guess above isn't right for a particular piece.
        String assetOverride = sec.getString("equipment-asset", null);
        NamespacedKey equipmentAsset = new NamespacedKey("customarmor",
            assetOverride != null ? assetOverride : powerName);

        EquipmentSlot slot = switch (material.name()) {
            case String m when m.endsWith("_HELMET") -> EquipmentSlot.HEAD;
            case String m when m.endsWith("_CHESTPLATE") -> EquipmentSlot.CHEST;
            case String m when m.endsWith("_LEGGINGS") -> EquipmentSlot.LEGS;
            case String m when m.endsWith("_BOOTS") -> EquipmentSlot.FEET;
            default -> EquipmentSlot.CHEST;
        };

        return new ArmorPiece(id, displayName, material, customModelData, lore, enchants, unbreakable,
            equipmentAsset, slot);
    }

    ItemStack createItemStack() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.displayName));
        if (this.customModelData != 0) {
            meta.setCustomModelData(this.customModelData);
        }
        if (!this.lore.isEmpty()) {
            List<String> colored = new ArrayList<>();
            for (String line : this.lore) {
                colored.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(colored);
        }
        for (Map.Entry<Enchantment, Integer> e : this.enchantments.entrySet()) {
            meta.addEnchant(e.getKey(), e.getValue(), true);
        }
        if (this.unbreakable) {
            meta.setUnbreakable(true);
        }
        meta.getPersistentDataContainer().set(
            new NamespacedKey(CustomArmorPlugin.get(), "armor_id"), PersistentDataType.STRING, this.id);
        item.setItemMeta(meta);

        // *** THE ACTUAL FIX ***
        // This is the piece that was missing entirely before: without it, the game
        // has no idea it should render anything different on the player's body, so
        // it just falls back to plain vanilla netherite no matter what custom-model-data
        // or resource pack files exist.
        Equippable equippable = Equippable.equippable(this.equipmentSlot)
            .assetId(this.equipmentAsset)
            .build();
        item.setData(DataComponentTypes.EQUIPPABLE, equippable);

        return item;
    }

    public String id() { return this.id; }
    public String displayName() { return this.displayName; }
    public Material material() { return this.material; }
    public int customModelData() { return this.customModelData; }
    public NamespacedKey equipmentAsset() { return this.equipmentAsset; }
    public EquipmentSlot equipmentSlot() { return this.equipmentSlot; }
}
