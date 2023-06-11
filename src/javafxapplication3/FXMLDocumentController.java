/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author jjj
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TilePane tilePane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File f = new File("C:\\");
        WildcardFileFilter fileFilter = new WildcardFileFilter.Builder().setWildcards("*.pdf").get();
        Collection<File> files = FileUtils.listFilesAndDirs(f, DirectoryFileFilter.INSTANCE, null);
        for (File file : files) {
            tilePane.getChildren().add(generarItem(file));
        }
        

    }

    private VBox generarItem(File file) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        Image image = null;
        Label label = new Label(file.getName());
        label.setMaxWidth(100);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        if (file.isDirectory()) {
            image = new Image(getClass().getResourceAsStream("icons/folder.png"));
        } else if (file.isFile()) {
            image = new Image(getClass().getResourceAsStream("icons/pdf.png"));
        }
        imageView.setImage(image);
        container.getChildren().add(imageView);
        container.getChildren().add(label);
        container.setUserData(file);
        return container;
    }

}
