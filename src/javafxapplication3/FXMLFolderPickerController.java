/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import gui.Alerts.AlertIcon;
import gui.Alerts.OkAlert;
import gui.Alerts.WaitAlert;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * FXML Controller class
 *
 * @author Kasparov
 */
public class FXMLFolderPickerController implements Initializable {

    @FXML
    private Pane pane;
    @FXML
    private Label titleLabel;
    @FXML
    private MFXButton btnAtras;
    @FXML
    private Label pathLabel;
    @FXML
    private MFXScrollPane scrollPane;
    @FXML
    private FlowPane flowPane;

    private File currentFolder;
    private Label vacioLabel;
    BooleanProperty hasParent = new SimpleBooleanProperty(false);

    private File pdf;
    @FXML
    private HBox buttonsHbox;
    private MFXButton btnCancelar;
    @FXML
    private MFXButton btnGuardar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnCancelar = new MFXButton("");

        vacioLabel = new Label("Esta carpeta está vacía");
        vacioLabel.getStyleClass().add("h2");

        btnAtras.visibleProperty().bind(hasParent);
        btnAtras.setOnAction((event) -> {
            listFiles(currentFolder.getParentFile());
        });
        currentFolder = JavaFXApplication3.UsbRootFile;
        listFiles(currentFolder);

        btnGuardar.setOnAction((event) -> {

            try {
                Files.copy(pdf.toPath(), currentFolder.toPath().resolve(pdf.getName()), StandardCopyOption.REPLACE_EXISTING);
                OkAlert alert = new OkAlert(AlertIcon.SUCCESS, JavaFXApplication3.currentStage);
                alert.setTitle("Archivo guardado");
                alert.setTextContent("El archivo se guardó en la carpeta seleccionada con el nombre: " + pdf.getName());
                alert.setOkButtonAction((ee) -> {
                    btnCancelar.fire();
                });
                alert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(FXMLFolderPickerController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
    }

    private void listFiles(File file) {
        pathLabel.setText(file.getAbsolutePath());
        currentFolder = file;
        hasParent.set(currentFolder.getParentFile() != null);
        flowPane.getChildren().clear();
        WildcardFileFilter wildcardFileFilter = WildcardFileFilter.builder().setWildcards("*.pdf", "*.docx").get();
        Collection<File> files = FileUtils.listFilesAndDirs(file, DirectoryFileFilter.DIRECTORY, null);
        files.remove(file);
        if (files.isEmpty()) {
            flowPane.getChildren().add(vacioLabel);
        }
        for (File f : files) {
            flowPane.getChildren().add(generarItemGrid(f));
        }
    }

    public void setPdf(File pdf) {
        this.pdf = pdf;
    }

    public void setCancelActionButton(EventHandler<ActionEvent> action) {
        btnCancelar.setOnAction(action);
    }

    private GridPane generarItemGrid(File file) {
        GridPane container = new GridPane();
        final String itemClass = "file";
        container.getStyleClass().add(itemClass);
        container.getRowConstraints().add(new RowConstraints() {
            {
                setVgrow(Priority.NEVER);
            }
        });
        container.getRowConstraints().add(new RowConstraints() {
            {
                setVgrow(Priority.ALWAYS);
                setValignment(VPos.TOP);
            }
        });
        ImageView imageView = new ImageView();
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        Image image = null;
        Label label = new Label(file.getName());
        label.getStyleClass().add(itemClass);
        label.setMaxWidth(80);
        if (file.isDirectory()) {
            image = new Image(getClass().getResourceAsStream("icons/folder.png"));
        }
        imageView.setImage(image);
        container.add(imageView, 0, 0);
        container.add(label, 0, 1);
        container.setOnMousePressed((MouseEvent event) -> {
            fileClicked(file);
        });
        return container;
    }

    private void fileClicked(File file) {
        if (file.isDirectory()) {
            listFiles(file);
        }
    }

}
