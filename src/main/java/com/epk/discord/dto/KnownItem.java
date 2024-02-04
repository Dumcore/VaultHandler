package com.epk.discord.dto;

public enum KnownItem {
    PDW("Kampf PDW"),
    PISTOL("Kampfpistole"),
    BATON("Schlagstock"),
    FLASHLIGHT("Taschenlampe"),
    TASER("Tazer"),
    EXTENDED_MAGAZIN("Polizei Erweitertes Magazin"),
    SILENCER("Polizei Schalldämpfer"),
    VEST("Schutzweste"),
    MEDIKIT("Medikit"),
    REPAIRKIT("Repairkit"),
    MAGAZINE("Magazin");

    public final String label;

    private KnownItem(String label) {
        this.label = label;
    }

    public static KnownItem getByLabel(String label) {
        for(KnownItem item: KnownItem.values()) {
            if (item.label.equals(label)) {
                return item;
            }
        }
        return null; // not found
    }
}
