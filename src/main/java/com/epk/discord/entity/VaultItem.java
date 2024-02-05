package com.epk.discord.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class VaultItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer amount;

    @Column
    private String item;
}
