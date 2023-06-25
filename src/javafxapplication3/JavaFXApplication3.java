/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 *
 * @author jjj
 */
public class JavaFXApplication3 extends Application {
        
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController documentController = loader.getController();
        
        Scene scene = new Scene(root);
        String css = getClass().getResource("css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        stage.setScene(scene);
//        stage.setFullScreen(true);
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
//        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        stage.show();
        documentController.ListFilesInDir(new File("C:\\"));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
