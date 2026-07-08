package com.mermaid.system;

import org.bukkit.Color;

/**
 * The 8 Mermaid tail/charm types available in the /mermaid menu.
 * Colors are used to dye the leather charm icon and the tail armor
 * until a custom Resource Pack with real textures is added later.
 */
public enum MermaidType {

    SPRING("spring", "§dSpring Charm", Color.fromRGB(255, 182, 213)),
    SUMMER("summer", "§eSummer Charm", Color.fromRGB(255, 205, 60)),
    AUTUMN("autumn", "§6Autumn Charm", Color.fromRGB(255, 140, 30)),
    WINTER("winter", "§bWinter Charm", Color.fromRGB(200, 235, 245)),
    SEA_WITCH("sea_witch", "§5Sea Witch Charm", Color.fromRGB(120, 30, 140)),
    NEPTUNE("neptune", "§9Neptune's Charm", Color.fromRGB(20, 90, 160)),
    BLOSSOM("blossom", "§dBlossoming Charm", Color.fromRGB(255, 110, 180)),
    SIREN("siren", "§3Siren's Charm", Color.fromRGB(10, 120, 120));

    private final String id;
    private final String displayName;
    private final Color color;

    MermaidType(String id, String displayName, Color color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
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
