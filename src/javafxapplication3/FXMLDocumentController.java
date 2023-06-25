package javafxapplication3;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
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
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileEqualsFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
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
    @FXML
    private Label titleLabel;
    @FXML
    private Pane pane;
    @FXML
    private MFXButton btnAtras;
    @FXML
    private Label pathLabel;
    @FXML
    private MFXScrollPane scrollPane;

    private File currentFolder;
    private Label vacioLabel;
    public BooleanProperty hasParent = new SimpleBooleanProperty(false);
    BooleanProperty loading = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vacioLabel = new Label("Esta carpeta está vacía");
        vacioLabel.getStyleClass().add("h2");
        btnAtras.visibleProperty().bind(hasParent);
        btnAtras.setOnAction((event) -> {
            listFiles(currentFolder.getParentFile());
        });
        scrollPane.minWidthProperty().bind(pane.widthProperty().subtract(40));
        pathLabel.minWidthProperty().bind(pane.widthProperty().subtract(40));
        scrollPane.maxHeightProperty().bind(pane.heightProperty().subtract(100));
    }

    public void ListFilesInDir(File dir) {
        listFiles(dir);
    }

    private void listFiles(File file) {
        pathLabel.setText(file.getAbsolutePath());
        currentFolder = file;
        hasParent.set(currentFolder.getParentFile() != null);
        flowPane.getChildren().clear();
        WildcardFileFilter wildcardFileFilter = WildcardFileFilter.builder().setWildcards("*.pdf", "*.docx").get();
        OrFileFilter orFileFilter = new OrFileFilter(wildcardFileFilter, DirectoryFileFilter.DIRECTORY);
        Collection<File> files = FileUtils.listFilesAndDirs(file, orFileFilter, null);
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
        } else if (file.isFile() && FilenameUtils.isExtension(file.getName(), "pdf")) {
            try {
                showPDF(file);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                showWord(file);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void showPDF(File file) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLPdfViewver.fxml"));
        Parent root = loader.load();
        FXMLPdfViewverController scene2Controller = loader.getController();
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.getScene().setRoot(root);
        scene2Controller.setPdf(file);
        scene2Controller.showPdf();
    }

    private void showWord(File file) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLPdfViewver.fxml"));
        Parent root = loader.load();
        FXMLPdfViewverController scene2Controller = loader.getController();
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.getScene().setRoot(root);
        scene2Controller.setPdfFromWord(file);
        scene2Controller.showPdf();
    }

}
