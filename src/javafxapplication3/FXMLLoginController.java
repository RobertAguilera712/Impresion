/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import com.jfoenix.controls.JFXPasswordField;
import gui.Alerts.AlertIcon;
import gui.Alerts.OkAlert;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kasparov
 */
public class FXMLLoginController implements Initializable {

    @FXML
    private MFXButton loginBtn;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Pane pane;
    @FXML
    private Label titleLabel;
    @FXML
    private MFXButton btnAtras;
    @FXML
    private VBox vbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String password = JavaFXApplication3.getPassword();
        
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

        loginBtn.setOnAction((event) -> {
            if (passwordField.getText().equals(password)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLSettings.fxml"));
                    Parent root = loader.load();
                    Stage stage = JavaFXApplication3.currentStage;
                    stage.getScene().setRoot(root);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                OkAlert alert = new OkAlert(AlertIcon.ERROR, JavaFXApplication3.currentStage);
                alert.setTitle("Contraseña incorrecta");
                alert.setTextContent("La contraseña es incorrecta. Intentalo de nuevo");
                alert.showAndWait();
            }
        });
    }

}
