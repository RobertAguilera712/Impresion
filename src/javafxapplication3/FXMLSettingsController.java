package javafxapplication3;

import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import gui.Alerts.AlertIcon;
import gui.Alerts.OkAlert;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.print.attribute.standard.Chromaticity;

public class FXMLSettingsController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private VBox settingsVbox;
    @FXML
    private HBox precioBn;
    @FXML
    private MFXButton lessBnBtn;
    @FXML
    private Label precioBnLabel;
    @FXML
    private MFXButton plusBnBtn;
    @FXML
    private HBox precioColor;
    @FXML
    private MFXButton lessColorBtn;
    @FXML
    private Label precioColorLabel;
    @FXML
    private MFXButton plusColorBtn;
    @FXML
    private HBox precioCopiaBn;
    @FXML
    private MFXButton lessCopiaBnBtn;
    @FXML
    private Label precioCopiaBnLabel;
    @FXML
    private MFXButton plusCopiaBnBtn;
    @FXML
    private HBox precioCopiaColor;
    @FXML
    private MFXButton lessCopiaColorBtn;
    @FXML
    private Label precioCopiaColorLabel;
    @FXML
    private MFXButton plusCopiaColorBtn;
    @FXML
    private HBox precioEscaneo;
    @FXML
    private MFXButton lessEscaneoBtn;
    @FXML
    private Label precioEscaneoLabel;
    @FXML
    private MFXButton plusEscaneoBtn;

    private IntegerProperty precioBnProperty;
    private IntegerProperty precioColorProperty;
    private IntegerProperty precioScanProperty;

    @FXML
    private Pane pane;
    @FXML
    private MFXButton guardarBtn;
    @FXML
    private JFXComboBox<String> pantallaCombo;
    private String[] pantallaOpts;
    @FXML
    private MFXButton btnAtras;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField redPasswordField;
    @FXML
    private JFXTextField redTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String password = JavaFXApplication3.getPassword();
        String nombreRed = JavaFXApplication3.getNombreRed();
        String redPassword = JavaFXApplication3.getPasswordRed();
        passwordField.setText(password);
        redTextField.setText(nombreRed);
        redPasswordField.setText(redPassword);
        

        btnAtras.setOnAction((event) -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                Parent root = loader.load();
                Stage stage = JavaFXApplication3.currentStage;
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        pantallaOpts = new String[]{"Si", "No"};
        pantallaCombo.getItems().setAll(pantallaOpts);

        if (JavaFXApplication3.getFullScreen()) {
            pantallaCombo.getSelectionModel().selectFirst();

        } else {
            pantallaCombo.getSelectionModel().selectLast();
        }

        precioBnProperty = new SimpleIntegerProperty(JavaFXApplication3.getPrecioBlancoNegro());
        precioColorProperty = new SimpleIntegerProperty(JavaFXApplication3.getPrecioColor());
        precioScanProperty = new SimpleIntegerProperty(JavaFXApplication3.getPrecioScan());

        precioBnLabel.textProperty().bind(precioBnProperty.asString("$%d"));
        precioColorLabel.textProperty().bind(precioColorProperty.asString("$%d"));
        precioEscaneoLabel.textProperty().bind(precioScanProperty.asString("$%d"));

        plusBnBtn.setOnAction((event) -> {
            precioBnProperty.set(precioBnProperty.add(1).get());
        });

        lessBnBtn.setOnAction((event) -> {
            if (precioBnProperty.get() > 1) {
                precioBnProperty.set(precioBnProperty.subtract(1).get());
            }
        });

        plusColorBtn.setOnAction((event) -> {
            precioColorProperty.set(precioColorProperty.add(1).get());
        });

        lessColorBtn.setOnAction((event) -> {
            if (precioColorProperty.get() > 1) {
                precioColorProperty.set(precioColorProperty.subtract(1).get());
            }
        });

        plusEscaneoBtn.setOnAction((event) -> {
            precioScanProperty.set(precioScanProperty.add(1).get());
        });

        lessEscaneoBtn.setOnAction((event) -> {
            if (precioScanProperty.get() > 1) {
                precioScanProperty.set(precioScanProperty.subtract(1).get());
            }
        });

        guardarBtn.setOnAction((event) -> {
            if (passwordField.getText().length() <= 0) {
                OkAlert alert = new OkAlert(AlertIcon.ERROR, JavaFXApplication3.currentStage);
                alert.setTitle("Error");
                alert.setTextContent("La contraseña no puede estar vacía");
                alert.showAndWait();
                return;
            }

            if (redPasswordField.getText().length() <= 0) {
                OkAlert alert = new OkAlert(AlertIcon.ERROR, JavaFXApplication3.currentStage);
                alert.setTitle("Error");
                alert.setTextContent("La contraseña de red no puede estar vacía");
                alert.showAndWait();
                return;
            }

            if (redTextField.getText().length() <= 0) {
                OkAlert alert = new OkAlert(AlertIcon.ERROR, JavaFXApplication3.currentStage);
                alert.setTitle("Error");
                alert.setTextContent("El nombre de la red no puede estar vacío");
                alert.showAndWait();
                return;
            }

            String newPassword = passwordField.getText();
            JavaFXApplication3.setPassword(newPassword);
            
            JavaFXApplication3.setNombreRed(redTextField.getText());
            JavaFXApplication3.setPasswordRed(redPasswordField.getText());

            JavaFXApplication3.setPrecioColor(precioColorProperty.get());
            JavaFXApplication3.setPrecioBlancoNegro(precioBnProperty.get());
            JavaFXApplication3.setPrecioScan(precioScanProperty.get());

            boolean fullScreen = pantallaCombo.getValue().equals(pantallaOpts[0]);
            JavaFXApplication3.setFullScreen(fullScreen);

            OkAlert alert = new OkAlert(AlertIcon.SUCCESS, JavaFXApplication3.currentStage);
            alert.setTitle("Configuración actualizada");
            alert.setTextContent("La configuración se actualizó con éxito");
            alert.setOkButtonAction((e) -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                    Parent root = loader.load();
                    Stage stage = JavaFXApplication3.currentStage;
                    stage.getScene().setRoot(root);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            alert.showAndWait();
        });

    }

}
