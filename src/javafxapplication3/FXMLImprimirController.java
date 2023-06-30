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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
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
    }

    public void setPdf(File pdf) throws IOException {
        this.pdf = pdf;
        document = PDDocument.load(pdf);
        for (int i = 1, j = 2, n = document.getNumberOfPages(); i <= n; i++) {
            JFXCheckBox c = new JFXCheckBox("Página " + i);
            c.minWidthProperty().bind(pagsListView.widthProperty().subtract(25));
            c.setPadding(new Insets(0, 0, 10, 0));
            c.setUserData(i);
            pagsListView.getItems().add(c);

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

    }

}
