package com.epk.discord.hibernate.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "vault_access_log")
@NamedQuery(name = "VaultAccessLog_findByAccessorId",
           query = "from VaultAccessLog where accessorId = :accessorId")
public class VaultAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "accessor_id", nullable = false)
    private Long accessorId;

    @OneToMany(mappedBy = "vaultAccessLog", cascade=CascadeType.ALL)
    @Column(nullable = false)
    private Set<VaultItem> items;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "access_time", nullable = false)
    Timestamp accessTime = Timestamp.from(Instant.now());


    public VaultAccessLog() { }

    public VaultAccessLog(Long id, Long accessorId, Set<VaultItem> items, Timestamp accessTime) {
        this.id = id;
        this.accessorId = accessorId;
        this.items = items;
        this.accessTime = accessTime;
    }

    public VaultAccessLog(Long accessorId, Set<VaultItem> items, Timestamp accessTime) {
        this.accessorId = accessorId;
        this.items = items;
        this.accessTime = accessTime;
    }

    public VaultAccessLog(Long accessorId, Set<VaultItem> items) {
        this.accessorId = accessorId;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccessorId() {
        return accessorId;
    }

    public void setAccessorId(Long accessorId) {
        this.accessorId = accessorId;
    }

    public Set<VaultItem> getItems() {
        return items;
    }

    public void setItems(Set<VaultItem> items) {
        this.items = items;
    }

    public Timestamp getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Timestamp accessTime) {
        this.accessTime = accessTime;
    }
}
