package com.fhfelipefh.projetofinal_lpoo_felipe.control;

import com.fhfelipefh.projetofinal_lpoo_felipe.model.Sala;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class SalaController {

    public Sala create(String nome, Integer capacidade,
                       String localizacao, BigDecimal precoHora) {

        EntityManager em = JpaUtil.getEntityManager();
        Sala sala = new Sala();
        sala.setNome(nome);
        sala.setCapacidade(capacidade);
        sala.setLocalizacao(localizacao);
        sala.setPrecoHora(precoHora);

        em.getTransaction().begin();
        em.persist(sala);
        em.getTransaction().commit();
        em.close();
        return sala;
    }

    public Sala update(Sala sala) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Sala merged = em.merge(sala);
        em.getTransaction().commit();
        em.close();
        return merged;
    }

    public void delete(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Sala sala = em.find(Sala.class, id);
        if (sala != null) em.remove(sala);
        em.getTransaction().commit();
        em.close();
    }

    public List<Sala> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Sala> list = em.createQuery("FROM Sala", Sala.class).getResultList();
        em.close();
        return list;
    }
}
