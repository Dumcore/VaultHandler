package com.epk.discord.hibernate.entity;

import jakarta.persistence.*;

@Entity
public class VaultItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String item;

    @ManyToOne()
    private VaultAccessLog vaultAccessLog;

    public VaultItem() { }

    public VaultItem(String item, Integer amount) {
        this.item = item;
        this.amount = amount;
    }

    public VaultItem(String item, Integer amount, VaultAccessLog accessLog) {
        this.item = item;
        this.amount = amount;
        this.vaultAccessLog = accessLog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public VaultAccessLog getVaultAccessLog() {
        return vaultAccessLog;
    }

    public void setVaultAccessLog(VaultAccessLog vaultAccessLog) {
        this.vaultAccessLog = vaultAccessLog;
    }

    @Override
    public String toString() {
        return "VaultItem {" +
                "id=" + id +
                ", amount=" + amount +
                ", item='" + item + '\'' +
                '}';
    }
}
