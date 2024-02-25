package com.epk.discord.hibernate;

import com.epk.discord.VaultHandler;
import com.epk.discord.hibernate.entity.VaultAccessLog;
import com.epk.discord.hibernate.entity.VaultItem;
import org.hibernate.*;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    private static final Logger log = LoggerFactory.getLogger(VaultHandler.class);

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder().configure().build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void persistEntity(Object entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            log.trace("Creating transaction");
            transaction = session.beginTransaction();
            log.debug("Persisting entity " + entity.getClass() + ": " + entity.toString());
            session.persist(entity);
            transaction.commit();
            log.debug("Successfully persisted entity: " + entity.getClass());
        } catch (HibernateException ex) {
            log.error("Exception on persisting entity of type " + entity.getClass().getName() + "! See exception for more information!", ex);
            if (transaction != null) {
                log.debug("Rollback transaction");
                transaction.rollback();
            }
        }
    }

    public static List<VaultAccessLog> getVaultAccessLogsByAccessorId(Long id) {
        if (id == null) {
            return null;
        }
        List<VaultAccessLog> resultList = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            resultList = session.createNamedQuery("VaultAccessLog_findByAccessorId", VaultAccessLog.class)
                    .setParameter("accessorId", id).getResultList();
        } catch (HibernateException ex) {
            log.error("Exception occurred while trying to get VaultAccessLogs by accessorId: " + id, ex);
        }
        return resultList;
    }

    public static List<VaultItem> getAllVaultItems() {
        List<VaultItem> resultList = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            resultList = session.createQuery("from VaultItem", VaultItem.class).getResultList();
        } catch (HibernateException ex) {
            log.error("Exception occurred while trying to get all VaultItems", ex);
        }
        return resultList;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}

