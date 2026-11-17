package com.example.appregistro.util;

import com.example.appregistro.model.Equipo;
import com.example.appregistro.model.TipoEquipo;

import java.util.Arrays;

/**
 * Validaciones de la aplicación ANTES de ir a la base de datos.
 * Si algo está mal devuelve el mensaje de error; si todo está bien devuelve null.
 */
public final class Validador {

    private Validador() {
    }

    public static String validarEquipo(String codigo, String nombre, String cantidadTexto,
                                       TipoEquipo tipo, String estado) {
        StringBuilder errores = new StringBuilder();

        if (codigo == null || codigo.isBlank()) {
            errores.append("• El código es obligatorio.\n");
        }
        if (nombre == null || nombre.isBlank()) {
            errores.append("• El nombre es obligatorio.\n");
        }
        if (cantidadTexto == null || cantidadTexto.isBlank()) {
            errores.append("• La cantidad es obligatoria.\n");
        } else {
            try {
                int cantidad = Integer.parseInt(cantidadTexto.trim());
                if (cantidad < 0) {
                    errores.append("• La cantidad no puede ser negativa.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("• La cantidad debe ser un número entero.\n");
            }
        }
        if (tipo == null) {
            errores.append("• Seleccione un tipo de equipo.\n");
        }
        if (estado == null || !Arrays.asList(Equipo.ESTADOS).contains(estado)) {
            errores.append("• Seleccione un estado permitido (Disponible, En mantenimiento o Fuera de servicio).\n");
        }
        return errores.length() == 0 ? null : errores.toString();
    }
}
