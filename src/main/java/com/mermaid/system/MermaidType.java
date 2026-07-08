package com.mermaid.system;

import org.bukkit.Color;

/**
 * The 8 Mermaid tail/charm types available in the /mermaid menu.
 * Colors are used to dye the leather charm icon and the tail armor
 * until a custom Resource Pack with real textures is added later.
 */
public enum MermaidType {

    SPRING("spring", "§dSpring Charm", Color.fromRGB(255, 182, 213), 1101),
    SUMMER("summer", "§eSummer Charm", Color.fromRGB(255, 205, 60), 1102),
    AUTUMN("autumn", "§6Autumn Charm", Color.fromRGB(255, 140, 30), 1103),
    WINTER("winter", "§bWinter Charm", Color.fromRGB(200, 235, 245), 1104),
    SEA_WITCH("sea_witch", "§5Sea Witch Charm", Color.fromRGB(120, 30, 140), 1105),
    NEPTUNE("neptune", "§9Neptune's Charm", Color.fromRGB(20, 90, 160), 1106),
    SIRENS_BLESSING("sirens_blessing", "§dSiren's Blessing", Color.fromRGB(255, 110, 180), 1107),
    SIRENS_ELEGY("sirens_elegy", "§3Siren's Elegy", Color.fromRGB(10, 120, 120), 1108);

    private final String id;
    private final String displayName;
    private final Color color;
    private final int customModelData;

    MermaidType(String id, String displayName, Color color, int customModelData) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.customModelData = customModelData;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public static MermaidType fromId(String id) {
        for (MermaidType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
