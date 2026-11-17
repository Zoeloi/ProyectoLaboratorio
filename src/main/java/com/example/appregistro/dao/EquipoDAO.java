package com.example.appregistro.dao;

import com.example.appregistro.model.Equipo;
import com.example.appregistro.model.TipoEquipo;
import com.example.appregistro.persistence.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Capa de persistencia de Equipo.
 * Patrón de cada operación de escritura:
 *   1) crear EntityManager   2) begin()   3) operación   4) commit()
 *   si algo falla antes del commit -> rollback()   5) siempre close()
 */
public class EquipoDAO {

    // ------------------------------------------------------------ CREAR
    public void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Se vincula con un tipo EXISTENTE (entidad managed cargada desde tipo_equipo)
            equipo.setTipoEquipo(buscarTipo(em, equipo.getTipoEquipo()));
            em.persist(equipo);   // la entidad pasa a estado "managed"
            tx.commit();          // aquí se ejecuta el INSERT definitivo
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();    // deshace todo: no quedan cambios parciales
            }
            throw traducir(e, "guardar");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------ ACTUALIZAR
    public void actualizar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            equipo.setTipoEquipo(buscarTipo(em, equipo.getTipoEquipo()));
            // merge(): copia el estado del objeto "detached" (viene de la UI)
            // a una entidad managed; al hacer commit Hibernate ejecuta el UPDATE.
            em.merge(equipo);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw traducir(e, "actualizar");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------ ELIMINAR
    public void eliminar(Integer idEquipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // remove() solo funciona sobre entidades managed, por eso primero find()
            Equipo equipo = em.find(Equipo.class, idEquipo);
            if (equipo == null) {
                throw new DatosException("El equipo ya no existe en la base de datos.", null);
            }
            em.remove(equipo);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw traducir(e, "eliminar");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------ LEER / CONSULTAR
    /** Lista todos los equipos con su tipo. */
    public List<Equipo> listarTodos() {
        return buscar(null, null);
    }

    /**
     * Búsqueda parcial por nombre y/o filtro por tipo (se pueden combinar).
     * JPQL con parámetros (setParameter): NUNCA se concatena el texto del usuario.
     */
    public List<Equipo> buscar(String textoNombre, TipoEquipo tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo t WHERE 1 = 1");
            boolean porNombre = textoNombre != null && !textoNombre.isBlank();
            boolean porTipo = tipo != null;

            if (porNombre) {
                jpql.append(" AND LOWER(e.nombre) LIKE :patron");
            }
            if (porTipo) {
                jpql.append(" AND t.idTipo = :idTipo");
            }
            jpql.append(" ORDER BY e.codigo");

            TypedQuery<Equipo> query = em.createQuery(jpql.toString(), Equipo.class);
            if (porNombre) {
                query.setParameter("patron", "%" + textoNombre.trim().toLowerCase() + "%");
            }
            if (porTipo) {
                query.setParameter("idTipo", tipo.getIdTipo());
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Busca el tipo en la BD dentro del mismo EntityManager; si no existe, error claro. */
    private TipoEquipo buscarTipo(EntityManager em, TipoEquipo tipo) {
        TipoEquipo encontrado = (tipo == null || tipo.getIdTipo() == null)
                ? null : em.find(TipoEquipo.class, tipo.getIdTipo());
        if (encontrado == null) {
            throw new DatosException("El tipo de equipo seleccionado no existe.", null);
        }
        return encontrado;
    }

    // ------------------------------------------------------------ ERRORES
    /** Convierte la excepción técnica en un mensaje entendible. */
    private DatosException traducir(RuntimeException e, String operacion) {
        if (e instanceof DatosException) {
            return (DatosException) e;
        }
        String detalle = mensajeRaiz(e);
        String msg;
        if (detalle.contains("equipo_codigo_key")) {
            msg = "Ya existe un equipo con ese código. Los códigos no pueden repetirse.";
        } else if (detalle.contains("ck_equipo_cantidad")) {
            msg = "La cantidad no puede ser negativa.";
        } else if (detalle.contains("ck_equipo_estado")) {
            msg = "El estado no es válido. Use: Disponible, En mantenimiento o Fuera de servicio.";
        } else if (detalle.contains("fk_equipo_tipo")) {
            msg = "El tipo de equipo seleccionado no existe.";
        } else {
            msg = "No se pudo " + operacion + " el equipo: " + detalle;
        }
        return new DatosException(msg + "\n\nSe ejecutó rollback(): no quedó ningún cambio parcial en la base de datos.", e);
    }

    private String mensajeRaiz(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return String.valueOf(t.getMessage());
    }
}
