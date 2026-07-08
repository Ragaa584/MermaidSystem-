package com.mermaid.system;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MermaidPlugin extends JavaPlugin {

    private static MermaidPlugin instance;

    // Keys used to tag items with Persistent Data (so we can recognize them later)
    private NamespacedKey charmKey;
    private NamespacedKey tailKey;

    // Remembers each transformed player's original leggings/boots so we can restore them
    private final Map<UUID, org.bukkit.inventory.ItemStack[]> savedArmor = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        charmKey = new NamespacedKey(this, "mermaid_charm_type");
        tailKey = new NamespacedKey(this, "mermaid_tail");

        getCommand("mermaid").setExecutor(new MermaidCommand(this));

        getServer().getPluginManager().registerEvents(new MermaidGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new MermaidPlayerListener(this), this);

        new SwimTask(this).runTaskTimer(this, 10L, 10L); // every 0.5s

        getLogger().info("Mermaid System enabled!");
    }

    @Override
    public void onDisable() {
        // Restore any transformed players' armor before shutdown
        for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
            SwimTask.restoreArmor(this, player);
        }
        getLogger().info("Mermaid System disabled!");
    }

    public static MermaidPlugin getInstance() {
        return instance;
    }

    public NamespacedKey getCharmKey() {
        return charmKey;
    }

    public NamespacedKey getTailKey() {
        return tailKey;
    }

    public Map<UUID, org.bukkit.inventory.ItemStack[]> getSavedArmor() {
        return savedArmor;
    }
}
