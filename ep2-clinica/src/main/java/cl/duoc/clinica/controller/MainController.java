package cl.duoc.clinica.controller;

import cl.duoc.clinica.dao.GsonMascotaDao;
import cl.duoc.clinica.dao.PersistenciaException;
import cl.duoc.clinica.model.Gato;
import cl.duoc.clinica.model.Mascota;
import cl.duoc.clinica.model.Perro;
import cl.duoc.clinica.repository.MascotaRepository;
import cl.duoc.clinica.repository.Repository;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    // Constantes para el tipoChoice
    public static final String TIPO_PERRO = "Perro";
    public static final String TIPO_GATO = "Gato";
    // Constantes para alertalabel
    public final static String ALERTA_DEFAULT = "Sin alerta";
    public final static String ALERTA_EXITO = "Registro Exitoso";
    public final static String ALERTA_NO_VALIDO = "Dato no valido";
    public final static String ALERTA_ELIMINADO = "Registro Eliminado";
    public final static String ALERTA_NO_SELECCIONADO = "Falta de seleccion";
    public final static String ALERTA_LECTURA = "Error al leer archivo";
    public final static String ALERTA_ESCRITURA = "Error al escribir archivo";

    // El controlador solo conoce la interfaz Repository
    private Repository<Mascota> repositorio;
    private final ObservableList<Mascota> mascotas = FXCollections.observableArrayList();

    // Etiqueta de mensajes al pie de la ventana
    @FXML
    private Label alertalabel;

    // Campos comunes
    @FXML
    private ChoiceBox<String> tipoChoice;
    @FXML
    private TextField nombreText;
    @FXML
    private TextField edadText;
    @FXML
    private TextField pesoText;

    // Campos de Perro
    @FXML
    private TextField razaText;
    @FXML
    private CheckBox vacunadoCheck;
    @FXML
    private CheckBox disponibleCheck;

    // Campo de Gato
    @FXML
    private CheckBox exteriorCheck;

    // Tabla
    @FXML
    private TableView<Mascota> mascotasTable;
    @FXML
    private TableColumn<Mascota, String> tipoCol;
    @FXML
    private TableColumn<Mascota, String> nombreCol;
    @FXML
    private TableColumn<Mascota, Number> edadCol;
    @FXML
    private TableColumn<Mascota, Number> pesoCol;
    @FXML
    private TableColumn<Mascota, String> costoCol;

    @FXML
    public void initialize() {
        // Formulario
        this.tipoChoice.getItems().addAll(TIPO_PERRO, TIPO_GATO);
        this.tipoChoice.setValue(TIPO_PERRO);
        this.tipoChoice.setOnAction(e -> seleccionarTipo());
        seleccionarTipo();
        this.setAlertalabelMensaje(ALERTA_DEFAULT, "");

        // Columnas de la tabla
        this.tipoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClass().getSimpleName()));
        this.nombreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        this.edadCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getEdad()));
        this.pesoCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPeso()));
        this.costoCol.setCellValueFactory(c ->
                new SimpleStringProperty("$" + String.format("%.0f", c.getValue().calcularCostoConsulta())));
        this.mascotasTable.setItems(this.mascotas);

        // Persistencia: se cargan los archivos al abrir la aplicación
        this.repositorio = new MascotaRepository(
                new GsonMascotaDao<>(Path.of("perros.json"), Perro.class),
                new GsonMascotaDao<>(Path.of("gatos.json"), Gato.class));
        try {
            this.repositorio.cargar();
            this.mascotas.setAll(this.repositorio.listar());
        } catch (PersistenciaException e) {
            this.setAlertalabelMensaje(ALERTA_LECTURA, e.getMessage() + " La aplicación inicia sin mascotas.");
        }
    }

    // Habilita solo los campos que corresponden al tipo elegido
    @FXML
    public void seleccionarTipo() {
        String tipo = this.tipoChoice.getValue();
        if (tipo == null) {
            tipo = "";
        }
        switch (tipo) {
            case TIPO_PERRO:
                this.razaText.setDisable(false);
                this.vacunadoCheck.setDisable(false);
                this.disponibleCheck.setDisable(false);
                this.exteriorCheck.setDisable(true);
                this.exteriorCheck.setSelected(false);
                break;
            case TIPO_GATO:
                this.razaText.setDisable(true);
                this.razaText.setText("");
                this.vacunadoCheck.setDisable(true);
                this.vacunadoCheck.setSelected(false);
                this.disponibleCheck.setDisable(true);
                this.disponibleCheck.setSelected(false);
                this.exteriorCheck.setDisable(false);
                break;
            default:
                this.razaText.setDisable(true);
                this.vacunadoCheck.setDisable(true);
                this.disponibleCheck.setDisable(true);
                this.exteriorCheck.setDisable(true);
                this.setAlertalabelMensaje(ALERTA_NO_SELECCIONADO, "Campo Seleccionar Tipo");
        }
    }

    @FXML
    public void onregistrar() {
        String tipo = this.tipoChoice.getValue();
        if (tipo == null) {
            this.setAlertalabelMensaje(ALERTA_NO_SELECCIONADO, "Campo Seleccionar Tipo");
            return;
        }

        // Se revisan todos los datos antes de crear el objeto y se juntan los errores
        List<String> errores = new ArrayList<>();

        String nombre = this.nombreText.getText();
        if (nombre == null || nombre.isBlank()) {
            errores.add("El nombre no puede estar vacío");
        }

        int edad = 0;
        try {
            edad = Integer.parseInt(this.edadText.getText().trim());
            if (edad < 0 || edad > 30) {
                errores.add("La edad debe estar entre 0 y 30 años");
            }
        } catch (NumberFormatException e) {
            errores.add("La edad debe ser un número entero");
        }

        double peso = 0;
        try {
            peso = Double.parseDouble(this.pesoText.getText().trim().replace(',', '.'));
            if (!Double.isFinite(peso) || peso <= 0) {
                errores.add("El peso debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            errores.add("El peso debe ser un número");
        }

        String raza = this.razaText.getText();
        if (TIPO_PERRO.equals(tipo) && (raza == null || raza.isBlank())) {
            errores.add("La raza no puede estar vacía");
        }

        if (!errores.isEmpty()) {
            this.setAlertalabelMensaje(ALERTA_NO_VALIDO, String.join("; ", errores));
            return;
        }

        try {
            Mascota mascota;
            if (TIPO_PERRO.equals(tipo)) {
                Perro perro = new Perro(nombre, edad, peso, raza, this.vacunadoCheck.isSelected());
                if (this.disponibleCheck.isSelected()) {
                    perro.marcarDisponible();
                }
                mascota = perro;
            } else {
                mascota = new Gato(nombre, edad, peso, this.exteriorCheck.isSelected());
            }

            this.repositorio.agregar(mascota);
            this.mascotas.setAll(this.repositorio.listar());
            this.limpiarCampos();
            this.setAlertalabelMensaje(ALERTA_EXITO, "Mascota - " + mascota.getNombre());
        } catch (IllegalArgumentException e) {
            // Respaldo: reglas del modelo
            this.setAlertalabelMensaje(ALERTA_NO_VALIDO, e.getMessage());
        } catch (PersistenciaException e) {
            this.setAlertalabelMensaje(ALERTA_ESCRITURA, e.getMessage());
        }
    }

    @FXML
    public void oneliminar() {
        Mascota seleccionada = this.mascotasTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            this.setAlertalabelMensaje(ALERTA_NO_SELECCIONADO,
                    "Seleccione una mascota de la tabla para eliminarla");
            return;
        }
        try {
            this.repositorio.eliminar(seleccionada);
            this.mascotas.setAll(this.repositorio.listar());
            this.setAlertalabelMensaje(ALERTA_ELIMINADO, "Mascota - " + seleccionada.getNombre());
        } catch (PersistenciaException e) {
            this.setAlertalabelMensaje(ALERTA_ESCRITURA, e.getMessage());
        }
    }

    @FXML
    public void onlimpiar() {
        this.limpiarCampos();
        this.setAlertalabelMensaje(ALERTA_DEFAULT, "");
    }

    private void limpiarCampos() {
        this.nombreText.setText("");
        this.edadText.setText("");
        this.pesoText.setText("");
        this.razaText.setText("");
        this.vacunadoCheck.setSelected(false);
        this.disponibleCheck.setSelected(false);
        this.exteriorCheck.setSelected(false);
        this.tipoChoice.setValue(TIPO_PERRO);
        this.seleccionarTipo();
    }

    private void setAlertalabelMensaje(String tipo, String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = tipo;
        } else {
            mensaje = tipo + ": " + mensaje;
        }
        switch (tipo) {
            case ALERTA_EXITO:
            case ALERTA_ELIMINADO:
                this.alertalabel.setVisible(true);
                this.alertalabel.setTextFill(Color.GREEN);
                break;
            case ALERTA_NO_VALIDO:
            case ALERTA_NO_SELECCIONADO:
            case ALERTA_LECTURA:
            case ALERTA_ESCRITURA:
                this.alertalabel.setVisible(true);
                this.alertalabel.setTextFill(Color.RED);
                break;
            default:
                this.alertalabel.setVisible(false);
                this.alertalabel.setTextFill(Color.BLACK);
        }
        this.alertalabel.setText(mensaje);
    }
}
