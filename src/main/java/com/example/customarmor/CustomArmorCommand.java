/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.example.customarmor;

import com.example.customarmor.CustomArmorPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CustomArmorCommand
implements CommandExecutor,
TabCompleter {
    private final CustomArmorPlugin plugin;

    CustomArmorCommand(CustomArmorPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub;
        if (args.length == 0) {
            this.sendHelp(sender);
            return true;
        }
        switch (sub = args[0].toLowerCase()) {
            case "give": {
                Player target;
                if (!sender.hasPermission("customarmor.give")) {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "No permission.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /customarmor give <id> [player] [amount]");
                    return true;
                }
                String id = args[1];
                ItemStack item = this.plugin.armorManager().createItem(id);
                if (item == null) {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "Unknown armor id: " + id);
                    return true;
                }
                if (args.length >= 3) {
                    target = this.plugin.getServer().getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Player not found: " + args[2]);
                        return true;
                    }
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Console must specify a player.");
                        return true;
                    }
                    Player p = (Player)sender;
                    target = p;
                }
                int amount = 1;
                if (args.length >= 4) {
                    try {
                        amount = Math.max(1, Integer.parseInt(args[3]));
                    }
                    catch (NumberFormatException ignored) {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount.");
                        return true;
                    }
                }
                item.setAmount(amount);
                target.getInventory().addItem(new ItemStack[]{item}).forEach((idx, leftover) -> target.getWorld().dropItemNaturally(target.getLocation(), leftover));
                sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Gave " + amount + "x " + id + " to " + target.getName() + ".");
                break;
            }
            case "list": {
                sender.sendMessage(String.valueOf(ChatColor.GOLD) + "Custom armor (" + this.plugin.armorManager().count() + "):");
                for (String id : this.plugin.armorManager().ids()) {
                    sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "  - " + id);
                }
                break;
            }
            case "reload": {
                if (!sender.hasPermission("customarmor.reload")) {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "No permission.");
                    return true;
                }
                this.plugin.armorManager().reload();
                sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Reloaded " + this.plugin.armorManager().count() + " armor pieces.");
                break;
            }
            default: {
                this.sendHelp(sender);
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(String.valueOf(ChatColor.GOLD) + "CustomArmor commands:");
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "  /customarmor give <id> [player] [amount]");
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "  /customarmor list");
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "  /customarmor reload");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return this.filter(Arrays.asList("give", "list", "reload"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return this.filter(this.plugin.armorManager().ids(), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            ArrayList<String> names = new ArrayList<String>();
            this.plugin.getServer().getOnlinePlayers().forEach(p -> names.add(p.getName()));
            return this.filter(names, args[2]);
        }
        return List.of();
    }

    private List<String> filter(List<String> options, String prefix) {
        ArrayList<String> out = new ArrayList<String>();
        for (String o : options) {
            if (!o.toLowerCase().startsWith(prefix.toLowerCase())) continue;
            out.add(o);
        }
        return out;
    }
}

