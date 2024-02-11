package com.epk.discord.dto;

import com.epk.discord.hibernate.entity.VaultAccessLog;
import com.epk.discord.hibernate.entity.VaultItem;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VaultAccessDTO {

    private boolean putIn = true;

    // key = item, v = amount
    private Map<KnownItem, Integer> knownItems = new HashMap<>();

    private Map<String, Integer> customItems = new HashMap<>();

    private Long accessorId;

    public VaultAccessDTO() { }

    public VaultAccessDTO(boolean putIn) {
        this.putIn = putIn;
    }

    public VaultAccessDTO(boolean putIn, Long accessorId) {
        this.putIn = putIn;
        this.accessorId = accessorId;
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

    public VaultAccessLog toVaultAccessLog() {
        VaultAccessLog accessLog = new VaultAccessLog();
        accessLog.setAccessorId(accessorId);
        // persist only known items. Maybe change later depending on requirements!
        Set<VaultItem> items = Sets.newHashSet(knownItems.entrySet().stream().map(
                item -> new VaultItem(item.getKey().label, item.getValue())
                ).toList());
        accessLog.setItems(items);
        return accessLog;
    }
}
