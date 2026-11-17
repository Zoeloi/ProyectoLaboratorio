package com.example.appregistro.dao;

import com.example.appregistro.model.TipoEquipo;
import com.example.appregistro.persistence.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TipoEquipoDAO {

    /** Lista todos los tipos (para llenar los ComboBox). Consulta JPQL sobre entidades. */
    public List<TipoEquipo> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoEquipo t ORDER BY t.nombre", TipoEquipo.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
