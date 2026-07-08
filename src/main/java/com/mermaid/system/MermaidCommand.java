package com.mermaid.system;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class MermaidCommand implements CommandExecutor {

    public static final String GUI_TITLE = "§b§lMermaid Charms";

    private final MermaidPlugin plugin;

    public MermaidCommand(MermaidPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by a player.");
            return true;
        }

        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19};
        MermaidType[] types = MermaidType.values();

        for (int i = 0; i < types.length; i++) {
            gui.setItem(slots[i], createCharmItem(types[i]));
        }

        player.openInventory(gui);
        return true;
    }

    /**
     * Builds the charm item shown in the GUI and given to the player.
     * Uses a dyed leather horse armor as a placeholder icon until a
     * custom Resource Pack with real textures is installed.
     */
    public static ItemStack createCharmItem(MermaidType type) {
        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setColor(type.getColor());
        meta.setDisplayName(type.getDisplayName());
        meta.setCustomModelData(type.getCustomModelData());

        List<String> lore = new ArrayList<>();
        lore.add("§7Hold this in your inventory and");
        lore.add("§7swim in water to transform!");
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(
                MermaidPlugin.getInstance().getCharmKey(),
                org.bukkit.persistence.PersistentDataType.STRING,
                type.getId()
        );

        item.setItemMeta(meta);
        return item;
    }
}
