package javafxapplication3;

import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXAlert;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import gui.Alerts.Alert;
import gui.Alerts.AlertIcon;
import gui.Alerts.ConfirmationAlert;
import gui.Alerts.OkAlert;
import gui.Alerts.WaitAlert;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.enums.ButtonType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafxapplication3.models.NoSelectionModel;
import javax.imageio.ImageIO;
import net.glxn.qrgen.QRCode;
import net.samuelcampos.usbdrivedetector.events.DeviceEventType;
import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedetector.events.USBStorageEvent;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * FXML Controller class
 *
 * @author Kasparov
 */
public class FXMLScannerController implements Initializable {

    @FXML
    private Pane pane;
    @FXML
    private Label titleLabel;
    @FXML
    private ImageView previewIv;

    private Webcam webcam;
    @FXML
    private MFXButton scanBtn;

    private int imageX;
    private int imageY;
    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;
    @FXML
    private HBox btnsHbox;
    @FXML
    private MFXButton saveBtn;

    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage;

    @FXML
    private MFXButton addPageBtn;
    @FXML
    private ListView<VBox> listView;

    private ObservableList<VBox> docPages;

    private IntegerProperty precioProperty = new SimpleIntegerProperty(JavaFXApplication3.precioScan);
    private IntegerProperty totalProperty = new SimpleIntegerProperty();
    private IntegerProperty faltanteProperty = new SimpleIntegerProperty();
    private IntegerProperty cambioProperty = new SimpleIntegerProperty();
    private IntegerProperty paginasProperty = new SimpleIntegerProperty(0);

    @FXML
    private VBox infoVbox;
    @FXML
    private Label pagsLabel;
    @FXML
    private Label precioLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label faltanteLabel;
    @FXML
    private Label cambioLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private MFXButton btnAtras;

    private JFXAlert<Void> folderPickerAlert;

    private JFXAlert<Void> saveOptionsAlert;

    BooleanProperty usbAtacched = new SimpleBooleanProperty(false);

    public IUSBDriveListener listener = new IUSBDriveListener() {
        @Override
        public void usbDriveEvent(USBStorageEvent usbse) {
            if (usbse.getEventType() == DeviceEventType.CONNECTED) {
                Platform.runLater(() -> {
                    JavaFXApplication3.UsbRootFile = usbse.getStorageDevice().getRootDirectory();
                    usbAtacched.set(true);
                });
                // USB REMOVED
            } else {
                Platform.runLater(() -> {
                    JavaFXApplication3.UsbRootFile = null;
                    usbAtacched.set(false);

                });
            }
        }

    };

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usbAtacched.set(JavaFXApplication3.UsbRootFile != null);
        JavaFXApplication3.listener = listener;
        JavaFXApplication3.driveDetector.addDriveListener(listener);

        btnAtras.setOnAction((event) -> {
            try {
                JavaFXApplication3.listener = null;
                JavaFXApplication3.driveDetector.removeDriveListener(listener);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                Parent root = loader.load();
                Stage stage = JavaFXApplication3.currentStage;
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Label creditoLabel = new Label();
        creditoLabel.getStyleClass().add("credito");
        pane.getChildren().add(creditoLabel);
        creditoLabel.layoutXProperty().bind(pane.widthProperty().subtract(creditoLabel.widthProperty()).subtract(20));
        creditoLabel.layoutYProperty().bind(pane.heightProperty().subtract(creditoLabel.heightProperty()).subtract(20));
        creditoLabel.textProperty().bind(JavaFXApplication3.credito.asString("Crédito: $%d"));

        totalProperty.bind(precioProperty);
        faltanteProperty.bind(Bindings.max(0, totalProperty.subtract(JavaFXApplication3.credito)));
        cambioProperty.bind(Bindings.max(JavaFXApplication3.credito.subtract(totalProperty), 0));
        errorLabel.visibleProperty().bind(faltanteProperty.lessThanOrEqualTo(0).not());

        precioLabel.getStyleClass().add("h3");
        totalLabel.getStyleClass().add("h3");
        faltanteLabel.getStyleClass().add("h3");
        cambioLabel.getStyleClass().add("h3");
        pagsLabel.getStyleClass().add("h3");

        totalLabel.textProperty().bind(totalProperty.asString("Total: $%d"));
        faltanteLabel.textProperty().bind(faltanteProperty.asString("Faltante: $%d"));
        cambioLabel.textProperty().bind(cambioProperty.asString("Cambio: $%d"));

        pagsLabel.textProperty().bind(paginasProperty.asString("Páginas: %d"));

        precioLabel.textProperty().bind(precioProperty.asString("Precio: $%d"));

        currentPage = 0;
        document = new PDDocument();
        renderer = new PDFRenderer(document);

        docPages = FXCollections.observableArrayList();

        listView.setSelectionModel(new NoSelectionModel<>());
        listView.setItems(docPages);

        imageX = (int) JavaFXApplication3.getImageX();
        imageY = (int) JavaFXApplication3.getImageHeight();
        imageWidth = (int) JavaFXApplication3.getImageWidth();
        imageHeight = (int) JavaFXApplication3.getImageHeight();

        imageWidth = imageWidth != 0 ? imageWidth : 10;
        imageHeight = imageHeight != 0 ? imageHeight : 10;

        if (Webcam.getWebcams().size() > 0) {
            webcam = Webcam.getDefault();
            if (webcam != null) {
                webcam.open();
            }
        }

        scanBtn.setOnAction((event) -> {
            image = webcam.getImage();
            previewIv.setImage(SwingFXUtils.toFXImage(image, null));
        });

        addPageBtn.setOnAction((event) -> {
            try {
                PDPage page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);

                PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document, toByteArray(image, "jpg"), "");
                float pageWidth = PDRectangle.LETTER.getWidth();
                float pageHeight = PDRectangle.LETTER.getHeight();
                float imageWidth = imageXObject.getWidth();
                float imageHeight = imageXObject.getHeight();

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    float scale = Math.min(pageWidth / imageWidth, pageHeight / imageHeight);
                    float x = (pageWidth - (imageWidth * scale)) / 2;
                    float y = (pageHeight - (imageHeight * scale)) / 2;

                    contentStream.drawImage(imageXObject, x, y, imageWidth * scale, imageHeight * scale);
                }

                // SHOW IMAGE OF PAGE
                BufferedImage pageImage = renderer.renderImage(currentPage);

                ImageView iv = new ImageView(SwingFXUtils.toFXImage(pageImage, null));
                iv.setPreserveRatio(true);
                iv.setMouseTransparent(true);
                iv.setFitHeight(150);
                VBox vbox = new VBox();

                vbox.setStyle("-fx-background-color: white");
                //Instantiating the Shadow class 
                DropShadow dropShadow = new DropShadow();

                //setting the type of blur for the shadow 
                dropShadow.setBlurType(BlurType.GAUSSIAN);

                //Setting color for the shadow 
                dropShadow.setColor(new Color(0, 0, 0, 0.3));

                //Setting the height of the shadow
                dropShadow.setHeight(2);

                //Setting the width of the shadow 
                dropShadow.setWidth(2);

                //Setting the radius of the shadow 
                dropShadow.setRadius(0.5);
                //setting the offset of the shadow 
                dropShadow.setOffsetX(0);
                dropShadow.setOffsetY(0);

                //Setting the spread of the shadow 
                dropShadow.setSpread(100);

                iv.setEffect(dropShadow);

                vbox.getChildren().add(iv);
                vbox.setAlignment(Pos.TOP_CENTER);

                docPages.add(vbox);

                currentPage++;
                paginasProperty.set(paginasProperty.get() + 1);

            } catch (IOException ex) {
                Logger.getLogger(FXMLScannerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        saveBtn.setOnAction((event) -> {

            if (faltanteProperty.get() > 0) {
                return;
            }

            ConfirmationAlert calert = new ConfirmationAlert(AlertIcon.QUESTION, JavaFXApplication3.currentStage);
            calert.setTitle("¿Guardar el documento?");
            calert.setTextContent("¿Estas seguro de guardar el documento con el contendio actual?");

            calert.setConfirmationButtonText("Si");

            calert.setConfirmationButtonAction((e) -> {
                JavaFXApplication3.darCambio((byte) cambioProperty.get());
                JavaFXApplication3.credito.set(0);

                showSaveOptionsAlert();

            });

            calert.setCancellationButtonText("No");
            calert.showAndWait();

        });
    }

    private void showSaveOptionsAlert() {

        // Saving pdf to static files of the page
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        // Get the current date and time
        Date now = new Date();
        // Format the  current date and time as a string
        String timestamp = dateFormat.format(now);
        // Combine the folder path and timestamp to create the file name
        String fileName = "scan_" + timestamp + ".pdf";

        File staticFilesFolder = new File(System.getProperty("user.dir") + "/src/webapp/static/pdf");
        File file = new File(staticFilesFolder.getAbsolutePath(), fileName);
        try {
            FileUtils.cleanDirectory(staticFilesFolder);
        } catch (IOException ex) {
            Logger.getLogger(FXMLScannerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        saveOptionsAlert = new JFXAlert<>(JavaFXApplication3.currentStage);
        saveOptionsAlert.setOverlayClose(false);

        Pane alertPane = new Pane();
        alertPane.setPrefSize(600, 400);

        // Create the title label
        Label titleLabelAlert = new Label("Opciones de guardado");
        titleLabelAlert.getStyleClass().add("h1");
        titleLabelAlert.layoutXProperty().bind(alertPane.widthProperty().subtract(titleLabelAlert.widthProperty()).divide(2));
        titleLabelAlert.setLayoutY(15);

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setLayoutX((alertPane.getPrefWidth() - 200) / 2);
        vbox.setLayoutY((alertPane.getPrefHeight() - 200) / 2);

        // Create the image view
        ImageView imageView = new ImageView();
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        MFXButton cerrarBtn = new MFXButton("Cerrar");
        cerrarBtn.setButtonType(ButtonType.RAISED);

        cerrarBtn.setOnAction((event) -> {
            saveOptionsAlert.close();
        });

        MFXButton usbBtn = new MFXButton("Guardar en USB");
        usbBtn.setButtonType(ButtonType.RAISED);
        usbBtn.visibleProperty().bind(usbAtacched);
        usbBtn.setOnAction((event) -> {
            try {
                if (folderPickerAlert == null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFolderPicker.fxml"));
                    Parent root = loader.load();
                    FXMLFolderPickerController controller = loader.getController();

                    folderPickerAlert = new JFXAlert<>(JavaFXApplication3.currentStage);
                    folderPickerAlert.setOverlayClose(false);
                    folderPickerAlert.setContent(root);

                    controller.setPdf(file);
                    controller.setCancelActionButton((ee) -> {
                        folderPickerAlert.close();
                    });
                }

                folderPickerAlert.show();

            } catch (IOException ex) {
                Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // Add the elements to the VBox
        vbox.getChildren().addAll(imageView, usbBtn, cerrarBtn);

        // Add the elements to the pane
        alertPane.getChildren().addAll(titleLabelAlert, vbox);

        try {
            document.save(file);
            document.close();
            String pdfPath = JavaFXApplication3.webAppAdress.replaceFirst("upload.html", "newestpdf");
            System.out.println(pdfPath);
            File qrStream = QRCode.from(pdfPath).withSize(200, 200).file();
            imageView.setImage(new Image("file:" + qrStream.getAbsolutePath()));

            saveOptionsAlert.setContent(alertPane);
            saveOptionsAlert.show();

        } catch (IOException ex) {
            Logger.getLogger(FXMLScannerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }

}
