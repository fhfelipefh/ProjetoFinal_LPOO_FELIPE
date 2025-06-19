package com.fhfelipefh.projetofinal_lpoo_felipe.control;

import com.fhfelipefh.projetofinal_lpoo_felipe.model.Reserva;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.ReservaStatus;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Sala;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaController {

    public Reserva create(LocalDateTime inicio, LocalDateTime fim,
                          Sala sala, Usuario usuario) {

        return create(inicio, fim, sala, usuario, ReservaStatus.PENDENTE);
    }

    public Reserva create(LocalDateTime inicio, LocalDateTime fim,
                          Sala sala, Usuario usuario, ReservaStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Long> q = em.createQuery("""
                    SELECT COUNT(r) FROM Reserva r
                    WHERE r.sala = :sala
                      AND r.dataHoraInicio < :fim
                      AND r.dataHoraFim > :inicio
                """, Long.class);
        q.setParameter("sala", sala);
        q.setParameter("fim", fim);
        q.setParameter("inicio", inicio);
        if (q.getSingleResult() > 0) {
            em.close();
            throw new IllegalStateException("Conflito de horário nesta sala");
        }
        Reserva r = new Reserva();
        r.setDataHoraInicio(inicio);
        r.setDataHoraFim(fim);
        r.setSala(sala);
        r.setUsuario(usuario);
        r.setStatus(status);
        em.getTransaction().begin();
        em.persist(r);
        em.getTransaction().commit();
        em.close();
        return r;
    }

    public Reserva updateStatus(Integer id, ReservaStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Reserva r = em.find(Reserva.class, id);
        if (r != null) r.setStatus(status);
        em.getTransaction().commit();
        em.close();
        return r;
    }

    public void delete(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Reserva r = em.find(Reserva.class, id);
        if (r != null) em.remove(r);
        em.getTransaction().commit();
        em.close();
    }

    public List<Reserva> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Reserva> list = em.createQuery("FROM Reserva", Reserva.class).getResultList();
        em.close();
        return list;
    }

    public List<Reserva> findBySala(Sala sala) {
        EntityManager em = JpaUtil.getEntityManager();
        List<Reserva> list = em.createQuery(
                        "FROM Reserva r WHERE r.sala = :sala", Reserva.class)
                .setParameter("sala", sala)
                .getResultList();
        em.close();
        return list;
    }

    public List<Reserva> findByUsuario(Usuario usuario) {
        EntityManager em = JpaUtil.getEntityManager();
        List<Reserva> list = em.createQuery(
                        "FROM Reserva r WHERE r.usuario = :u", Reserva.class)
                .setParameter("u", usuario)
                .getResultList();
        em.close();
        return list;
    }

    public List<Reserva> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = JpaUtil.getEntityManager();
        List<Reserva> list = em.createQuery("""
                                FROM Reserva r
                                WHERE r.dataHoraInicio >= :ini
                                  AND r.dataHoraFim   <= :fim
                        """, Reserva.class)
                .setParameter("ini", inicio)
                .setParameter("fim", fim)
                .getResultList();
        em.close();
        return list;
    }

    public Reserva update(Integer id,
                          LocalDateTime inicio,
                          LocalDateTime fim,
                          Sala sala,
                          Usuario usuario) {
        return update(id, inicio, fim, sala, usuario, ReservaStatus.PENDENTE);
    }

    public Reserva update(Integer id,
                          LocalDateTime inicio,
                          LocalDateTime fim,
                          Sala sala,
                          Usuario usuario,
                          ReservaStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Long> q = em.createQuery("""
                    SELECT COUNT(r)
                      FROM Reserva r
                     WHERE r.sala = :sala
                       AND r.id <> :id
                       AND r.dataHoraInicio < :fim
                       AND r.dataHoraFim   > :inicio
                """, Long.class);
        q.setParameter("sala", sala);
        q.setParameter("id", id);
        q.setParameter("fim", fim);
        q.setParameter("inicio", inicio);
        if (q.getSingleResult() > 0) {
            em.close();
            throw new IllegalStateException("Conflito de horário nesta sala");
        }
        em.getTransaction().begin();
        Reserva r = em.find(Reserva.class, id);
        if (r == null) {
            em.getTransaction().rollback();
            em.close();
            throw new IllegalArgumentException("Reserva não encontrada: " + id);
        }
        r.setDataHoraInicio(inicio);
        r.setDataHoraFim(fim);
        r.setSala(sala);
        r.setUsuario(usuario);
        r.setStatus(status);
        em.getTransaction().commit();
        em.close();
        return r;
    }

    public boolean isDisponivel(Integer salaId, LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = JpaUtil.getEntityManager();
        Long qtd = em.createQuery("""
                            SELECT COUNT(r)
                              FROM Reserva r
                             WHERE r.sala.id = :salaId
                               AND r.dataHoraInicio < :fim
                               AND r.dataHoraFim   > :inicio
                        """, Long.class)
                .setParameter("salaId", salaId)
                .setParameter("fim", fim)
                .setParameter("inicio", inicio)
                .getSingleResult();
        em.close();
        return qtd == 0;
    }
}
