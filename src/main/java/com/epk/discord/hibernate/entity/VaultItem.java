package com.epk.discord.hibernate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class VaultItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String item;

    public VaultItem() { }

    public VaultItem(String item, Integer amount) {
        this.item = item;
        this.amount = amount;
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
}
