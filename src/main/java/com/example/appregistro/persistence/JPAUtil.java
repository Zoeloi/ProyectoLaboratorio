package com.example.appregistro.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Administra el EntityManagerFactory (EMF) de la aplicación.
 *
 * - El EMF es "pesado": se crea UNA sola vez y vive mientras la app está abierta.
 * - El EntityManager (EM) es "liviano": se crea uno por cada unidad de trabajo
 *   (guardar, actualizar, listar...) y se cierra al terminar.
 * - Las credenciales reales se leen de variables de entorno para no subirlas a GitHub.
 */
public final class JPAUtil {

    private static final String PERSISTENCE_UNIT = "laboratorioPU";
    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    private static synchronized EntityManagerFactory getFactory() {
        if (emf == null || !emf.isOpen()) {
            Map<String, Object> props = new HashMap<>();
            // Si existen estas variables de entorno, reemplazan los valores de persistence.xml
            agregarSiExiste(props, "jakarta.persistence.jdbc.url", "DB_URL");
            agregarSiExiste(props, "jakarta.persistence.jdbc.user", "DB_USER");
            agregarSiExiste(props, "jakarta.persistence.jdbc.password", "DB_PASSWORD");
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, props);
        }
        return emf;
    }

    private static void agregarSiExiste(Map<String, Object> props, String propiedad, String variable) {
        String valor = System.getenv(variable);
        if (valor != null && !valor.isBlank()) {
            props.put(propiedad, valor);
        }
    }

    /** Devuelve un EntityManager NUEVO. Quien lo pide debe cerrarlo. */
    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    /** Se llama al cerrar la aplicación (HelloApplication.stop()). */
    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
