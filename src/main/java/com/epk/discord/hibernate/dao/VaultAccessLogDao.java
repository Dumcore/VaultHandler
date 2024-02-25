package com.epk.discord.hibernate.dao;

import com.epk.discord.VaultHandler;
import com.epk.discord.hibernate.HibernateUtil;
import com.epk.discord.hibernate.entity.VaultAccessLog;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

import static com.epk.discord.hibernate.HibernateUtil.getSessionFactory;

public class VaultAccessLogDao {

    private static final Logger log = LoggerFactory.getLogger(VaultAccessLogDao.class);

    public static List<VaultAccessLog> findVaultAccessLogsByAccessorId(Long id) {
        if (id == null) {
            return null;
        }
        getSessionFactory();
        List<VaultAccessLog> resultList = null;
        try (Session session = getSessionFactory().openSession()) {
            resultList = session.createNamedQuery("VaultAccessLog_findByAccessorId", VaultAccessLog.class)
                    .setParameter("accessorId", id).getResultList();
        } catch (HibernateException ex) {
            log.error("Exception occurred while trying to get VaultAccessLogs by accessorId: " + id, ex);
        }
        return resultList;
    }

    // TODO: Fix query for this and debug results!
    public static List<VaultAccessLog> findVaultAccessLogsByAccessorIdSinceDate(Long id, Timestamp startDate) {
        if (id == null) {
            return null;
        }
        getSessionFactory();
        List<VaultAccessLog> resultList = null;
        try (Session session = getSessionFactory().openSession()) {
            resultList = session.createNamedQuery("VaultAccessLog_findByAccessorIdSinceDate", VaultAccessLog.class)
                    .setParameter("accessorId", id)
                    .setParameter("accessTime", startDate).getResultList();
            log.debug("Found " + resultList.size() + " VaultAccessLogs with accessorId: " + id + " and after " + startDate.toString());
        } catch (HibernateException ex) {
            log.error("Exception occurred while trying to get VaultAccessLogs by accessorId: " + id + " since " + startDate.toString(), ex);
        }
        return resultList;
    }

}
