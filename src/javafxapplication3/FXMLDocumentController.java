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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileEqualsFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SymbolicLinkFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author jjj
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private FlowPane flowPane;

    private Label vacioLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Pane pane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button btnAtras;

    private File currentFolder;

    BooleanProperty hasParent = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File f = new File("C:\\");
        listFiles(f);
        vacioLabel = new Label("Esta carpeta está vacía");
        vacioLabel.getStyleClass().add("h2");
        btnAtras.layoutXProperty().bind(pane.widthProperty().subtract(btnAtras.widthProperty()).subtract(24));
        btnAtras.visibleProperty().bind(hasParent);
        btnAtras.setOnAction((event) -> {
            listFiles(currentFolder.getParentFile());
        });
        scrollPane.setLayoutX(20);
//        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.minViewportWidthProperty().bind(pane.widthProperty().subtract(60));
        scrollPane.maxHeightProperty().bind(pane.heightProperty().subtract(100));
        titleLabel.layoutXProperty().bind(pane.widthProperty().subtract(titleLabel.widthProperty()).divide(2));
    }

    private void listFiles(File file) {
        currentFolder = file;
        hasParent.set(currentFolder.getParentFile() != null);
        flowPane.getChildren().clear();
        PrefixFileFilter prefixFileFilter = new PrefixFileFilter(".");
        NotFileFilter notFileFilter = new NotFileFilter(SymbolicLinkFileFilter.INSTANCE);
        AndFileFilter andFileFilter = new AndFileFilter(notFileFilter, DirectoryFileFilter.INSTANCE);
        Collection<File> files = FileUtils.listFilesAndDirs(file, notFileFilter, null);
        files.remove(file);
        if (files.isEmpty()) {
            flowPane.getChildren().add(vacioLabel);
        }
        for (File f : files) {
            flowPane.getChildren().add(generarItemGrid(f));
        }
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
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        Image image = null;
        Label label = new Label(file.getName());
        label.getStyleClass().add(itemClass);
        label.setMaxWidth(100);
        if (file.isDirectory()) {
            image = new Image(getClass().getResourceAsStream("icons/folder.png"));
        } else if (file.isFile()) {
            image = new Image(getClass().getResourceAsStream("icons/pdf.png"));
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
