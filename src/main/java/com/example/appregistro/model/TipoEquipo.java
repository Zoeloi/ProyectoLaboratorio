package com.example.appregistro.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad del lado "uno" de la relación: un tipo tiene muchos equipos.
 */
@Entity
@Table(name = "tipo_equipo")
public class TipoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Integer idTipo;

    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /*
     * Relación inversa (bidireccional). mappedBy apunta al atributo "tipoEquipo"
     * de la clase Equipo, que es el lado propietario.
     * SIN cascade REMOVE: borrar un tipo no debe borrar sus equipos.
     */
    @OneToMany(mappedBy = "tipoEquipo", fetch = FetchType.LAZY)
    private List<Equipo> equipos = new ArrayList<>();

    public TipoEquipo() {
    }

    public TipoEquipo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<Equipo> getEquipos() { return equipos; }
    public void setEquipos(List<Equipo> equipos) { this.equipos = equipos; }

    /** Así el ComboBox muestra el nombre y no "TipoEquipo@1a2b3c". */
    @Override
    public String toString() {
        return nombre;
    }

    /** Dos tipos son iguales si tienen el mismo id (sirve para seleccionar en el ComboBox). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoEquipo)) return false;
        TipoEquipo otro = (TipoEquipo) o;
        return idTipo != null && idTipo.equals(otro.idTipo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idTipo);
    }
}
