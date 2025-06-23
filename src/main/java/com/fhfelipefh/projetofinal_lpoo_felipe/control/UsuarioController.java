package com.fhfelipefh.projetofinal_lpoo_felipe.control;

import com.fhfelipefh.projetofinal_lpoo_felipe.model.Usuario;

import javax.persistence.EntityManager;
import java.util.List;

public class UsuarioController {

    public Usuario create(String nome, String email) {
        EntityManager em = JpaUtil.getEntityManager();
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);

        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
        em.close();
        return u;
    }

    public Usuario update(Usuario usuario) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Usuario merged = em.merge(usuario);
        em.getTransaction().commit();
        em.close();
        return merged;
    }

    public void delete(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Usuario u = em.find(Usuario.class, id);
        if (u != null) em.remove(u);
        em.getTransaction().commit();
        em.close();
    }

    public List<Usuario> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Usuario> list = em.createQuery("FROM Usuario", Usuario.class).getResultList();
        em.close();
        return list;
    }
}
