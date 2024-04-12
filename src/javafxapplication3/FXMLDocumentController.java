package javafxapplication3;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jfoenix.controls.JFXToggleButton;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
import javax.imageio.ImageIO;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import net.samuelcampos.usbdrivedetector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedetector.events.DeviceEventType;
import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedetector.events.USBStorageEvent;
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
    BooleanProperty hasParent = new SimpleBooleanProperty(false);

    @FXML
    private Group filesGroup;

    @FXML
    private VBox emptyVbox;
    @FXML
    private ImageView qrImage;

    public IUSBDriveListener listener = new IUSBDriveListener() {
        @Override
        public void usbDriveEvent(USBStorageEvent usbse) {
            if (usbse.getEventType() == DeviceEventType.CONNECTED) {
                Platform.runLater(() -> {
                    JavaFXApplication3.UsbRootFile = usbse.getStorageDevice().getRootDirectory();
                    listFiles(JavaFXApplication3.UsbRootFile);
                });
                // USB REMOVED
            } else {
                Platform.runLater(() -> {
                    try {
                        JavaFXApplication3.UsbRootFile = null;
                        emptyVbox.visibleProperty().set(true);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                        Parent root = loader.load();
                        FXMLDocumentController documentController = loader.getController();
                        Stage stage = JavaFXApplication3.currentStage;
                        stage.getScene().setRoot(root);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }

    };

    @FXML
    private MFXButton ScanBtn;
    @FXML
    private ImageView redQrImage;
    @FXML
    private MFXButton CopyBtn;
    @FXML
    private JFXToggleButton conectadoToggle;

//    private static void fireFileCreatedEvent(Path createdFilePath) {
//        // Here, you can implement your own logic to handle the file creation event
//        // For example, you can trigger callbacks, update UI, etc.
//        System.out.println("File created event: " + createdFilePath);
//    }
    private void watchForFileCreation(Path directoryToWatch) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        directoryToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    currentFolder = new File("uploads");
                    Platform.runLater(() -> {
                        ListFilesInDir(currentFolder);
                    });
//                    Path createdFilePath = (Path) event.context();
//                    fireFileCreatedEvent(createdFilePath);
                }
            }

            key.reset();
        }
    }

    @Override

    public void initialize(URL url, ResourceBundle rb) {
        qrImage.visibleProperty().bind(conectadoToggle.selectedProperty());
        redQrImage.visibleProperty().bind(conectadoToggle.selectedProperty().not());
        
        Path directoryToWatch = Paths.get("uploads");
        // Create a new thread for handling events
        Thread eventHandlerThread = new Thread(() -> {
            try {
                watchForFileCreation(directoryToWatch);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        eventHandlerThread.setDaemon(true);
        // Start the event handler thread
        eventHandlerThread.start();

        JavaFXApplication3.listener = listener;
        JavaFXApplication3.driveDetector.addDriveListener(listener);
        titleLabel.layoutXProperty().bind(pane.widthProperty().subtract(titleLabel.widthProperty()).divide(2));

        filesGroup.visibleProperty().bind(emptyVbox.visibleProperty().not());

        vacioLabel = new Label("Esta carpeta está vacía");
        vacioLabel.getStyleClass().add("h2");
        btnAtras.layoutXProperty().bind(pane.widthProperty().subtract(btnAtras.widthProperty()).subtract(20));
        btnAtras.visibleProperty().bind(hasParent);
        btnAtras.setOnAction((event) -> {
            listFiles(currentFolder.getParentFile());
        });
        pathLabel.minWidthProperty().bind(pane.widthProperty().subtract(40));
        scrollPane.minWidthProperty().bind(pane.widthProperty().subtract(40));
        scrollPane.maxHeightProperty().bind(pane.heightProperty().subtract(100));

        Label creditoLabel = new Label();
        creditoLabel.getStyleClass().add("credito");
        pane.getChildren().add(creditoLabel);
        creditoLabel.layoutXProperty().bind(pane.widthProperty().subtract(creditoLabel.widthProperty()).subtract(20));
        creditoLabel.layoutYProperty().bind(pane.heightProperty().subtract(creditoLabel.heightProperty()).subtract(20));
        creditoLabel.textProperty().bind(JavaFXApplication3.credito.asString("Crédito: $%d"));

        String nombreRed = JavaFXApplication3.getNombreRed();
        String passwordRed = JavaFXApplication3.getPasswordRed();
        String networkInfo = "WIFI:S:" + nombreRed + ";T:WPA;P:" + passwordRed + ";;";

        File qrStream = QRCode.from(JavaFXApplication3.webAppAdress).withSize(200, 200).file();
        qrImage.setImage(new Image("file:" + qrStream.getAbsolutePath()));

        File qrRedStream = QRCode.from(networkInfo).withSize(200, 200).file();
        redQrImage.setImage(new Image("file:" + qrRedStream.getAbsolutePath()));

        KeyCombination keyCombination = new KeyCharacterCombination(",", KeyCombination.CONTROL_DOWN);

        pane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (keyCombination.match(event)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
                    Parent root = loader.load();
                    Stage stage = JavaFXApplication3.currentStage;
                    stage.getScene().setRoot(root);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

//        settingsBtn.setOnAction((event) -> {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLSettings.fxml"));
//                Parent root = loader.load();
//                Stage stage = JavaFXApplication3.currentStage;
//                stage.getScene().setRoot(root);
//            } catch (IOException ex) {
//                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
        ScanBtn.setOnAction((event) -> {
            try {

                JavaFXApplication3.listener = null;
                JavaFXApplication3.driveDetector.removeDriveListener(listener);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLScanner.fxml"));
                Parent root = loader.load();
                Stage stage = JavaFXApplication3.currentStage;
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        CopyBtn.setOnAction((event) -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLScanner.fxml"));
                Parent root = loader.load();
                FXMLScannerController controller = loader.getController();
                controller.setCopiado();
                Stage stage = JavaFXApplication3.currentStage;
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

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
        emptyVbox.visibleProperty().set(false);
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
            if (FilenameUtils.isExtension(file.getName(), "pdf")) {
                image = new Image(getClass().getResourceAsStream("icons/pdf.png"));
            } else {
                image = new Image(getClass().getResourceAsStream("icons/word.png"));
            }
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
    }

}
