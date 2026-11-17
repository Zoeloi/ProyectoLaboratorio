package com.example.appregistro.controller;

import com.example.appregistro.dao.DatosException;
import com.example.appregistro.dao.EquipoDAO;
import com.example.appregistro.dao.TipoEquipoDAO;
import com.example.appregistro.model.Equipo;
import com.example.appregistro.model.TipoEquipo;
import com.example.appregistro.util.Alertas;
import com.example.appregistro.util.Validador;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller de la vista equipo-view.fxml.
 * Solo maneja eventos y componentes gráficos; el acceso a datos lo hacen los DAO.
 */
public class EquipoController {

    // ---------- Formulario ----------
    @FXML private Label lblId;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<TipoEquipo> cmbTipo;
    @FXML private ComboBox<String> cmbEstado;

    // ---------- Búsqueda y filtro ----------
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;

    // ---------- Tabla ----------
    @FXML private TableView<Equipo> tblEquipos;
    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, Integer> colCantidad;
    @FXML private TableColumn<Equipo, String> colTipo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoEquipoDAO tipoDAO = new TipoEquipoDAO();

    /** Equipo seleccionado en la tabla (null = modo "nuevo"). */
    private Equipo seleccionado;

    @FXML
    public void initialize() {
        // Columnas: el nombre entre comillas es el getter del modelo (getCodigo -> "codigo")
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("nombreTipo")); // nombre del tipo relacionado

        cmbEstado.setItems(FXCollections.observableArrayList(Equipo.ESTADOS));

        // Al seleccionar una fila, se cargan sus datos en el formulario
        tblEquipos.getSelectionModel().selectedItemProperty()
                  .addListener((obs, anterior, nuevo) -> cargarEnFormulario(nuevo));

        try {
            List<TipoEquipo> tipos = tipoDAO.listarTodos();
            cmbTipo.setItems(FXCollections.observableArrayList(tipos));
            cmbFiltroTipo.setItems(FXCollections.observableArrayList(tipos));
            cargarTabla(equipoDAO.listarTodos());
        } catch (RuntimeException e) {
            Alertas.error("Error de conexión",
                    "No se pudo conectar con PostgreSQL.\n\nRevise que el servidor esté encendido, "
                    + "que exista la base bd_laboratorio_orm y la variable DB_PASSWORD.\n\nDetalle: "
                    + e.getMessage());
        }
        actualizarBotones();
    }

    // ================================================================ BOTONES CRUD

    @FXML
    private void onGuardar() {
        if (seleccionado != null) {
            Alertas.advertencia("Guardar", "Tiene un equipo seleccionado. Use Actualizar, "
                    + "o presione Limpiar para registrar uno nuevo.");
            return;
        }
        Equipo nuevo = leerFormulario();
        if (nuevo == null) return;

        try {
            equipoDAO.guardar(nuevo);
            Alertas.info("Guardar", "Equipo registrado correctamente.");
            limpiarFormulario();
            refrescarTabla();
        } catch (DatosException e) {
            Alertas.error("No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        if (seleccionado == null) {
            Alertas.advertencia("Actualizar", "Seleccione un equipo de la tabla.");
            return;
        }
        Equipo editado = leerFormulario();
        if (editado == null) return;
        editado.setIdEquipo(seleccionado.getIdEquipo()); // mismo id -> merge() hace UPDATE

        try {
            equipoDAO.actualizar(editado);
            Alertas.info("Actualizar", "Equipo actualizado correctamente.");
            limpiarFormulario();
            refrescarTabla();
        } catch (DatosException e) {
            Alertas.error("No se pudo actualizar", e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        if (seleccionado == null) {
            Alertas.advertencia("Eliminar", "Seleccione un equipo de la tabla.");
            return;
        }
        boolean ok = Alertas.confirmar("Confirmar eliminación",
                "¿Seguro que desea eliminar el equipo " + seleccionado.getCodigo()
                + " - " + seleccionado.getNombre() + "?");
        if (!ok) return;

        try {
            equipoDAO.eliminar(seleccionado.getIdEquipo());
            Alertas.info("Eliminar", "Equipo eliminado correctamente.");
            limpiarFormulario();
            refrescarTabla();
        } catch (DatosException e) {
            Alertas.error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    // ================================================================ BÚSQUEDA / FILTRO

    @FXML
    private void onBuscar() {
        refrescarTabla();
    }

    @FXML
    private void onQuitarFiltros() {
        txtBuscar.clear();
        cmbFiltroTipo.getSelectionModel().clearSelection();
        cmbFiltroTipo.setValue(null);
        refrescarTabla();
    }

    // ================================================================ AUXILIARES

    /** Recarga la tabla respetando el texto de búsqueda y el filtro actual (combinados). */
    private void refrescarTabla() {
        try {
            cargarTabla(equipoDAO.buscar(txtBuscar.getText(), cmbFiltroTipo.getValue()));
        } catch (RuntimeException e) {
            Alertas.error("Error", "No se pudo consultar la base de datos.\n" + e.getMessage());
        }
    }

    private void cargarTabla(List<Equipo> lista) {
        tblEquipos.setItems(FXCollections.observableArrayList(lista));
    }

    /** Valida y construye un Equipo con lo escrito en el formulario. Devuelve null si hay errores. */
    private Equipo leerFormulario() {
        String error = Validador.validarEquipo(
                txtCodigo.getText(), txtNombre.getText(), txtCantidad.getText(),
                cmbTipo.getValue(), cmbEstado.getValue());
        if (error != null) {
            Alertas.advertencia("Datos inválidos", error);
            return null;
        }
        return new Equipo(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtMarca.getText() == null ? null : txtMarca.getText().trim(),
                cmbEstado.getValue(),
                Integer.parseInt(txtCantidad.getText().trim()),
                cmbTipo.getValue());
    }

    private void cargarEnFormulario(Equipo e) {
        seleccionado = e;
        if (e != null) {
            lblId.setText(String.valueOf(e.getIdEquipo()));
            txtCodigo.setText(e.getCodigo());
            txtNombre.setText(e.getNombre());
            txtMarca.setText(e.getMarca());
            txtCantidad.setText(String.valueOf(e.getCantidad()));
            cmbEstado.setValue(e.getEstado());
            cmbTipo.setValue(e.getTipoEquipo()); // funciona gracias a equals() por id
        }
        actualizarBotones();
    }

    private void limpiarFormulario() {
        tblEquipos.getSelectionModel().clearSelection();
        seleccionado = null;
        lblId.setText("(nuevo)");
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();
        cmbTipo.setValue(null);
        cmbEstado.setValue(null);
        actualizarBotones();
    }

    private void actualizarBotones() {
        // Solo cambia el texto del id; los botones validan con mensajes claros.
        if (seleccionado == null) {
            lblId.setText("(nuevo)");
        }
    }
}
