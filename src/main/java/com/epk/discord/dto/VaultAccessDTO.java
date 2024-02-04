package com.epk.discord.dto;

import java.util.HashMap;
import java.util.Map;

public class VaultAccessDTO {

    private boolean putIn = true;

    // key = item, v = amount
    private Map<KnownItem, Integer> knownItems = new HashMap<>();

    private Map<String, Integer> customItems = new HashMap<>();

    public VaultAccessDTO() { }

    public VaultAccessDTO(boolean putIn) {
        this.putIn = putIn;
    }

    public boolean isPutIn() {
        return putIn;
    }

    public Map<KnownItem, Integer> getKnownItems() {
        return knownItems;
    }

    public void setKnownItems(Map<KnownItem, Integer> knownItems) {
        this.knownItems = knownItems;
    }

    public void addKnownItem(KnownItem item, int amount) {
        knownItems.put(item, amount);
    }

    public Map<String, Integer> getCustomItems() {
        return customItems;
    }

    public void setCustomItems(Map<String, Integer> customItems) {
        this.customItems = customItems;
    }

    public void addCustomItems(String item, int amount) {
        customItems.put(item, amount);
    }

    public void addItems(Map<String, Integer> items) {
        items.entrySet().stream().forEach(item -> addItem(item.getKey(), item.getValue()));

    }

    public void addItem(String item, int amount) {

        KnownItem knownItem = KnownItem.getByLabel(item);
        if (knownItem != null)
            addKnownItem(knownItem, amount);
        else
            addCustomItems(item, amount);
    }
}
