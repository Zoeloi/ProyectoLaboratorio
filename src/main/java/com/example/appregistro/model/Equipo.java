package com.example.appregistro.model;

import jakarta.persistence.*;

/**
 * Entidad del lado "muchos": cada equipo pertenece a un único tipo.
 * Es el lado PROPIETARIO de la relación (tiene la llave foránea id_tipo).
 */
@Entity
@Table(name = "equipo")
public class Equipo {

    /** Estados permitidos (iguales al CHECK de PostgreSQL). */
    public static final String[] ESTADOS = {"Disponible", "En mantenimiento", "Fuera de servicio"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Integer idEquipo;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "marca", length = 60)
    private String marca;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /*
     * Asociación ORM real: guardamos el OBJETO TipoEquipo, no solo un número.
     * @JoinColumn indica que la columna de llave foránea es id_tipo.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoEquipo tipoEquipo;

    public Equipo() {
    }

    public Equipo(String codigo, String nombre, String marca, String estado,
                  Integer cantidad, TipoEquipo tipoEquipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.estado = estado;
        this.cantidad = cantidad;
        this.tipoEquipo = tipoEquipo;
    }

    public Integer getIdEquipo() { return idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public TipoEquipo getTipoEquipo() { return tipoEquipo; }
    public void setTipoEquipo(TipoEquipo tipoEquipo) { this.tipoEquipo = tipoEquipo; }

    /** Usado por la columna "Tipo" del TableView. */
    public String getNombreTipo() {
        return tipoEquipo != null ? tipoEquipo.getNombre() : "";
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
