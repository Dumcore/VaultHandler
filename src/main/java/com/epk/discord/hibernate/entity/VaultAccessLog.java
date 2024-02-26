package com.epk.discord.hibernate.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vault_access_log")
@NamedQuery(name = "VaultAccessLog_findByAccessorId",
           query = "from VaultAccessLog where accessorId = :accessorId")
@NamedQuery(name = "VaultAccessLog_findByAccessorIdSinceDate",
           query = "from VaultAccessLog where accessorId = :accessorId and accessTime >= :accessTime")
@NamedQuery(name = "VaultAccessLog_findVaultAccessLogsByAccessorIdDuringTimeSpan",
           query = "from VaultAccessLog where accessorId = :accessorId and accessTime >= :startTime and accessTime <= :endTime")
public class VaultAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "accessor_id", nullable = false)
    private Long accessorId;

    @Column(name = "put_in", nullable = false)
    private Boolean putIn;

    @OneToMany(mappedBy = "vaultAccessLog", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @Column(nullable = false)
    private Set<VaultItem> items;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "access_time", nullable = false)
    Timestamp accessTime = Timestamp.from(Instant.now());


    public VaultAccessLog() { }

    public VaultAccessLog(Long id, Long accessorId, boolean putIn, Set<VaultItem> items, Timestamp accessTime) {
        this.id = id;
        this.accessorId = accessorId;
        this.putIn = putIn;
        this.items = items;
        this.accessTime = accessTime;
    }

    public VaultAccessLog(Long accessorId, boolean putIn, Set<VaultItem> items, Timestamp accessTime) {
        this.accessorId = accessorId;
        this.putIn = putIn;
        this.items = items;
        this.accessTime = accessTime;
    }

    public VaultAccessLog(Long accessorId, boolean putIn, Set<VaultItem> items) {
        this.accessorId = accessorId;
        this.putIn = putIn;
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

    public Boolean getPutIn() {
        return putIn;
    }

    public void setPutIn(Boolean putIn) {
        this.putIn = putIn;
    }

    @Override
    public String toString() {
        return "VaultAccessLog {" +
                "id=" + id +
                ", accessorId=" + accessorId +
                ", items=" + Arrays.toString(List.of(items).toArray())+
                ", accessTime=" + accessTime +
                '}';
    }
}
