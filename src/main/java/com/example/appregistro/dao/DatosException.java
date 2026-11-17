package com.example.appregistro.dao;

/**
 * Error de la capa de datos con un mensaje entendible para el usuario.
 */
public class DatosException extends RuntimeException {
    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
