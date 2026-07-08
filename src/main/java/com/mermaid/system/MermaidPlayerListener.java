package com.mermaid.system;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MermaidPlayerListener implements Listener {

    private final MermaidPlugin plugin;

    public MermaidPlayerListener(MermaidPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // If they log out while transformed, give their real armor back
        SwimTask.restoreArmor(plugin, event.getPlayer());
    }
}
