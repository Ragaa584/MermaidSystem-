package com.mermaid.system;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MermaidGUIListener implements Listener {

    private final MermaidPlugin plugin;

    public MermaidGUIListener(MermaidPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!MermaidCommand.GUI_TITLE.equals(event.getView().getTitle())) {
            return;
        }

        // Prevent taking items out of the menu itself
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) {
            return;
        }

        String typeId = meta.getPersistentDataContainer().get(plugin.getCharmKey(), PersistentDataType.STRING);
        if (typeId == null) {
            return;
        }

        MermaidType type = MermaidType.fromId(typeId);
        if (type == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // Remove any charm the player already has, so only one is active at a time
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.hasItemMeta()
                    && stack.getItemMeta().getPersistentDataContainer().has(plugin.getCharmKey(), PersistentDataType.STRING)) {
                inv.setItem(i, null);
            }
        }

        inv.addItem(MermaidCommand.createCharmItem(type));
        player.closeInventory();
        player.sendMessage("§bYou received the " + type.getDisplayName() + "§b!");
    }
}
