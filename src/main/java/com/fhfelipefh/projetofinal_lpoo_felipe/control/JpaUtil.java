package com.fhfelipefh.projetofinal_lpoo_felipe.control;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JpaUtil {

    private static final String PU_NAME = "reservasPU";
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory(PU_NAME);

    private JpaUtil() {
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void closeFactory() {
        if (emf.isOpen()) emf.close();
    }
}
