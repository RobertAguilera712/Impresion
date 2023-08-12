/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.sun.deploy.util.StringUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafxapplication3.models.NoSelectionModel;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 * FXML Controller class
 *
 * @author Kasparov
 */
public class FXMLImprimirController implements Initializable {

    private File pdf;
    private PDDocument document;
    @FXML
    private HBox colorHbox;
    @FXML
    private MFXButton lessBtn;
    @FXML
    private MFXButton plusBtn;

    @FXML
    private MFXButton imprimirBtn;
    private IntegerProperty copias;
    @FXML
    private JFXComboBox<String> colorCombo;
    private String[] colorOpts;
    private String[] pagsOpts;
    @FXML
    private HBox nCopiasHbox;
    @FXML
    private Label copiasLabel;
    @FXML
    private HBox pagsHbox;
    @FXML
    private JFXComboBox<String> pagsCombo;
    @FXML
    private ListView<JFXCheckBox> pagsListView;
    @FXML
    private MFXButton cancelarBtn;
    @FXML
    private Group rangoGroup;
    @FXML
    private JFXComboBox<Integer> inicioCombo;
    @FXML
    private JFXComboBox<Integer> finCombo;
    @FXML
    private Label totalLabel;
    @FXML
    private Label creditoLabel;
    @FXML
    private Label faltanteLabel;
    @FXML
    private Label cambioLabel;
    @FXML
    private Label precioLabel;
    private IntegerProperty precioProperty = new SimpleIntegerProperty(JavaFXApplication3.precioBlancoNegro);
    private IntegerProperty totalProperty = new SimpleIntegerProperty();
    private IntegerProperty faltanteProperty = new SimpleIntegerProperty();
    private IntegerProperty cambioProperty = new SimpleIntegerProperty();
    private IntegerProperty paginasProperty = new SimpleIntegerProperty(0);
    private IntegerProperty allPaginasProperty = new SimpleIntegerProperty(0);
    private IntegerProperty paginasRangeProperty = new SimpleIntegerProperty(0);
    private IntegerProperty paginasSelectProperty = new SimpleIntegerProperty(0);
    private IntegerProperty InicioRangoProperty = new SimpleIntegerProperty(0);
    private IntegerProperty FinRangoProperty = new SimpleIntegerProperty(0);
    @FXML
    private Label pagsLabel;
    @FXML
    private Label copiasTotalLabel;
    @FXML
    private Label errorLabel;
    private StackPane stackPane;
    @FXML
    private VBox promtVbox;
    @FXML
    private MFXButton seguirBtn;
    @FXML
    private MFXButton noSeguirBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colorOpts = new String[]{"Blanco y negro", "Colores"};
        pagsOpts = new String[]{"Todo el documento", "Rango de páginas", "Seleccionar páginas"};
        colorCombo.getItems().setAll(colorOpts);
        colorCombo.getSelectionModel().selectFirst();
        pagsCombo.getItems().setAll(pagsOpts);
        pagsCombo.getSelectionModel().selectFirst();
        pagsListView.visibleProperty().bind(pagsCombo.valueProperty().isEqualTo(pagsOpts[2]));
        pagsListView.setSelectionModel(new NoSelectionModel<>());
        rangoGroup.visibleProperty().bind(pagsCombo.valueProperty().isEqualTo(pagsOpts[1]));

        copias = new SimpleIntegerProperty(1);
        copiasLabel.textProperty().bind(copias.asString());

        inicioCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            finCombo.getItems().clear();
            for (int i = newValue, n = document.getNumberOfPages(); i <= n; i++) {
                finCombo.getItems().add(i);
            }
            finCombo.getSelectionModel().selectFirst();
        });

        plusBtn.setOnAction((event) -> {
            copias.set(copias.add(1).get());
        });
        lessBtn.setOnAction((event) -> {
            if (copias.get() > 1) {
                copias.set(copias.subtract(1).get());
            }
        });
        imprimirBtn.setOnAction((event) -> {
            try {
                imprimir();
            } catch (IOException ex) {
                Logger.getLogger(FXMLImprimirController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PrinterException ex) {
                Logger.getLogger(FXMLImprimirController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        seguirBtn.setOnAction((event) -> {
            int nuevoCredito = JavaFXApplication3.credito.get() - totalProperty.get();
            JavaFXApplication3.setCredito(nuevoCredito);
            promtVbox.setVisible(false);
            mostrarPantallaPrincipal();
            cancelarBtn.fire();
        });

        noSeguirBtn.setOnAction((event) -> {
            promtVbox.setVisible(false);
            JavaFXApplication3.darCambio((byte) cambioProperty.get());
            JavaFXApplication3.setCredito(0);
            mostrarPantallaPrincipal();
            cancelarBtn.fire();
        });

        errorLabel.visibleProperty().bind(faltanteProperty.lessThanOrEqualTo(0).not());

        creditoLabel.getStyleClass().add("h3");
        precioLabel.getStyleClass().add("h3");
        totalLabel.getStyleClass().add("h3");
        faltanteLabel.getStyleClass().add("h3");
        cambioLabel.getStyleClass().add("h3");
        pagsLabel.getStyleClass().add("h3");
        copiasTotalLabel.getStyleClass().add("h3");

        totalProperty.bind(copias.multiply(precioProperty).multiply(paginasProperty));
        faltanteProperty.bind(Bindings.max(0, totalProperty.subtract(JavaFXApplication3.credito)));
        cambioProperty.bind(Bindings.max(JavaFXApplication3.credito.subtract(totalProperty), 0));

        creditoLabel.textProperty().bind(JavaFXApplication3.credito.asString("Crédito: $%d"));
        totalLabel.textProperty().bind(totalProperty.asString("Total: $%d"));
        faltanteLabel.textProperty().bind(faltanteProperty.asString("Faltante: $%d"));
        cambioLabel.textProperty().bind(cambioProperty.asString("Cambio: $%d"));
        pagsLabel.textProperty().bind(paginasProperty.asString("Páginas a imprimir: %d"));
        copiasTotalLabel.textProperty().bind(copias.multiply(paginasProperty).asString("Totoal de copias a imprir: %d"));

        precioLabel.textProperty().bind(precioProperty.asString("Precio: $%d"));

        colorCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(colorOpts[0])) {
                precioProperty.set(JavaFXApplication3.precioBlancoNegro);
            } else if (newValue.equals(colorOpts[1])) {
                precioProperty.set(JavaFXApplication3.precioColor);
            }
        });
        InicioRangoProperty.bind(inicioCombo.valueProperty());
        FinRangoProperty.bind(finCombo.valueProperty());
        paginasRangeProperty.bind(FinRangoProperty.subtract(InicioRangoProperty).add(1));

        paginasProperty.bind(allPaginasProperty);
        pagsCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(pagsOpts[0])) {
                paginasProperty.bind(allPaginasProperty);
            } else if (newValue.equals(pagsOpts[1])) {
                paginasProperty.bind(paginasRangeProperty);
            } else if (newValue.equals(pagsOpts[2])) {
                paginasProperty.bind(paginasSelectProperty);
            }
        });

    }

    private void mostrarPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();
            FXMLDocumentController documentController = loader.getController();
            Stage stage = JavaFXApplication3.currentStage;
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPdf(File pdf) throws IOException {
        this.pdf = pdf;
        document = PDDocument.load(pdf);
        allPaginasProperty.set(document.getNumberOfPages());
        for (int i = 1, j = 2, n = document.getNumberOfPages(); i <= n; i++) {
            JFXCheckBox c = new JFXCheckBox("Página " + i);
            c.minWidthProperty().bind(pagsListView.widthProperty().subtract(25));
            c.setPadding(new Insets(0, 0, 10, 0));
            c.setUserData(i);
            pagsListView.getItems().add(c);

            c.selectedProperty().addListener((observable) -> {
                int numPages = (int) pagsListView.getItems().stream().filter((t) -> {
                    return t.isSelected(); //To change body of generated lambdas, choose Tools | Templates.
                }).count();
                paginasSelectProperty.set(numPages);
            });

            inicioCombo.getItems().add(i);
            finCombo.getItems().add(j);
        }
        inicioCombo.getSelectionModel().selectFirst();
        finCombo.getSelectionModel().selectFirst();
    }

    public void setCancelar(EventHandler<ActionEvent> event) {
        cancelarBtn.setOnAction(event);
    }

    private void imprimir() throws IOException, PrinterException {

        if (faltanteProperty.get() > 0) {
            return;
        }

        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
        PrinterJob printJob = PrinterJob.getPrinterJob();

        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

        attributeSet.add(new Copies(copias.get()));

        if (colorCombo.getValue().equals(colorOpts[0])) {
            attributeSet.add(Chromaticity.MONOCHROME);
        } else {
            attributeSet.add(Chromaticity.COLOR);
        }

        if (pagsCombo.getValue().equals(pagsOpts[1])) {
            int inicio = inicioCombo.valueProperty().get();
            int fin = finCombo.valueProperty().get();
            attributeSet.add(new PageRanges(inicio, fin));
        }

        if (pagsCombo.getValue().equals(pagsOpts[2])) {
            String pags = pagsListView.getItems().stream().filter((t) -> {
                return t.isSelected(); //To change body of generated lambdas, choose Tools | Templates.
            }).map((JFXCheckBox t) -> t.getUserData().toString()).collect(Collectors.joining(","));
            System.out.println(pags);
            attributeSet.add(new PageRanges(pags));
        }

        printJob.setPageable(new PDFPageable(document));
        printJob.setPrintService(defaultPrintService);
        printJob.print(attributeSet);

        promtVbox.setVisible(true);
    }

}
