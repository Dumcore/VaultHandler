package com.epk.discord.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "vault_access_log")
public class VaultAccessLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "accessor_id")
    private Long accessorId;

    @OneToMany(mappedBy = "vault_access_log")
    private Set<VaultItem> items;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "access_time")
    Timestamp accessTime = Timestamp.from(Instant.now());
}
