package com.mermaid.system;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runs every 10 ticks (0.5s). For every online player:
 *  - checks if they are carrying a Mermaid Charm
 *  - if they are AND swimming in water -> equips the matching tail (leggings+boots)
 *  - if not (charm removed or left the water) -> restores their original armor
 */
public class SwimTask extends BukkitRunnable {

    private final MermaidPlugin plugin;

    public SwimTask(MermaidPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            MermaidType charmType = findCharmType(player);
            boolean transformed = plugin.getSavedArmor().containsKey(player.getUniqueId());

            boolean shouldBeTransformed = charmType != null && player.isInWater();

            if (shouldBeTransformed && !transformed) {
                equipTail(plugin, player, charmType);
            } else if (!shouldBeTransformed && transformed) {
                restoreArmor(plugin, player);
            }
        }
    }

    private MermaidType findCharmType(Player player) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || !stack.hasItemMeta()) continue;
            String id = stack.getItemMeta().getPersistentDataContainer()
                    .get(plugin.getCharmKey(), PersistentDataType.STRING);
            if (id != null) {
                return MermaidType.fromId(id);
            }
        }
        return null;
    }

    private static void equipTail(MermaidPlugin plugin, Player player, MermaidType type) {
        PlayerInventory inv = player.getInventory();

        // Save what the player was originally wearing
        ItemStack[] original = new ItemStack[]{inv.getLeggings(), inv.getBoots()};
        plugin.getSavedArmor().put(player.getUniqueId(), original);

        inv.setLeggings(createTailPiece(plugin, Material.LEATHER_LEGGINGS, type));
        inv.setBoots(createTailPiece(plugin, Material.LEATHER_BOOTS, type));
    }

    public static void restoreArmor(MermaidPlugin plugin, Player player) {
        ItemStack[] original = plugin.getSavedArmor().remove(player.getUniqueId());
        if (original == null) return;

        PlayerInventory inv = player.getInventory();
        inv.setLeggings(original[0]);
        inv.setBoots(original[1]);
    }

    private static ItemStack createTailPiece(MermaidPlugin plugin, Material material, MermaidType type) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setColor(type.getColor());
        meta.setDisplayName(type.getDisplayName() + " §fTail");
        meta.getPersistentDataContainer().set(plugin.getTailKey(), PersistentDataType.STRING, type.getId());

        // Prevent players from unequipping it manually while transformed
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }
}
