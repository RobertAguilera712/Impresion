package javafxapplication3;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafxapplication3.models.NoSelectionModel;
import javafxapplication3.models.PdfModel;
import javafxapplication3.tasks.LoadPdfTask;

public class FXMLPdfViewverController implements Initializable {

    private File pdf;
    private PdfModel model;
    private int numPages;
    @FXML
    private Pane pane;

    @FXML
    private VBox progressVbox;
    @FXML
    private Label progressLabel;
    @FXML
    private MFXProgressSpinner progressSpinner;
    @FXML
    private ListView<VBox> listView;

    private DoubleProperty pageHeightPropery;
    @FXML
    private MFXButton zoomInBtn;
    private double initZoom;
    private IntegerProperty zoomValue;
    @FXML
    private Label zoomLabel;
    @FXML
    private MFXButton zoomOutBtn;
    MFXListView<VBox> list;
    @FXML
    private HBox zoomHbox;
    @FXML
    private Label pageLabel;
    @FXML
    private Group viewerGroup;
    @FXML
    private HBox buttonsHbox;
    @FXML
    private MFXButton btnImprimir;
    @FXML
    private MFXButton btnAtras;
    @FXML
    private Label titleLabel;
    @FXML
    private MFXButton cancelarBtn;
    private LoadPdfTask loadPdfTask;

    public void setPdf(File pdf) {
        this.pdf = pdf;
        titleLabel.setText(pdf.getAbsolutePath());
    }

    public void setPdfFromWord(File word) {
        convertWordToPdf(word);
    }

    private void convertWordToPdf(File inputWord) {
        File outputFile = new File(inputWord.getParentFile().getAbsolutePath(), inputWord.getName().concat(".pdf"));
        try {
            InputStream docxInputStream = new FileInputStream(inputWord);
            OutputStream outputStream = new FileOutputStream(outputFile);
            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
            outputStream.close();
            docxInputStream.close();
            converter.shutDown();
            System.out.println("success");
            pdf = outputFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.setSelectionModel(new NoSelectionModel<>());
        listView.minWidthProperty().bind(pane.widthProperty().subtract(listView.layoutXProperty().multiply(2)));
        listView.minHeightProperty().bind(pane.heightProperty().subtract(listView.layoutYProperty()).subtract(20));
        listView.maxHeightProperty().bind(pane.heightProperty().subtract(listView.layoutYProperty()).subtract(20));
        listView.setOnZoom((ZoomEvent event) -> {
            double newZoom = pageHeightPropery.multiply(event.getZoomFactor()).get();
            newZoom = newZoom > initZoom * 5 ? initZoom * 5 : newZoom;
            newZoom = newZoom < initZoom * 0.2 ? initZoom * 0.2 : newZoom;
            pageHeightPropery.set(newZoom);

        });
        pageHeightPropery = new SimpleDoubleProperty(listView.heightProperty().get());
        zoomValue = new SimpleIntegerProperty(0);
        zoomLabel.textProperty().bind(zoomValue.asString());
        zoomInBtn.setOnAction((event) -> {
            double newZoom = pageHeightPropery.add(initZoom * 0.1).get();
            newZoom = newZoom > initZoom * 5 ? initZoom * 5 : newZoom;
            pageHeightPropery.set(newZoom);

        });
        zoomOutBtn.setOnAction((event) -> {
            double newZoom = pageHeightPropery.subtract(initZoom * 0.1).get();
            newZoom = newZoom < initZoom * 0.2 ? initZoom * 0.2 : newZoom;
            pageHeightPropery.set(newZoom);
        });
        cancelarBtn.setOnAction((event) -> {
            if (loadPdfTask != null && loadPdfTask.isRunning()) {
                loadPdfTask.cancel();
                try {
                    listFilesInDir();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLPdfViewverController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btnAtras.setOnAction((event) -> {
            try {
                listFilesInDir();
            } catch (IOException ex) {
                Logger.getLogger(FXMLPdfViewverController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        viewerGroup.visibleProperty().bind(progressVbox.visibleProperty().not());
        progressVbox.layoutXProperty().bind(pane.widthProperty().subtract(progressVbox.widthProperty()).divide(2));
        progressVbox.layoutYProperty().bind(pane.heightProperty().subtract(progressVbox.heightProperty()).divide(2));
        buttonsHbox.layoutXProperty().bind(pane.widthProperty().subtract(buttonsHbox.widthProperty()).subtract(20));
        zoomHbox.layoutXProperty().bind(pane.widthProperty().subtract(zoomHbox.widthProperty()).divide(2));
        titleLabel.layoutXProperty().bind(pane.widthProperty().subtract(titleLabel.widthProperty()).divide(2));
    }

    public void showPdf() {
        loadPdfTask = new LoadPdfTask(pdf, pageHeightPropery);

        progressLabel.textProperty().bind(loadPdfTask.messageProperty());
        progressVbox.visibleProperty().bind(loadPdfTask.runningProperty());
        progressSpinner.progressProperty().bind(loadPdfTask.progressProperty());
        loadPdfTask.valueProperty().addListener((ObservableValue<? extends ObservableList<VBox>> observable, ObservableList<VBox> oldValue, ObservableList<VBox> newValue) -> {
            listView.setItems(newValue);
        });
        loadPdfTask.setOnSucceeded((event) -> {
            pageHeightPropery.set(listView.heightProperty().get());
            initZoom = listView.heightProperty().get();
            zoomValue.bind(pageHeightPropery.divide(initZoom).multiply(100));

            pageLabel.setText(1 + " / " + listView.getItems().size());
            ScrollBar sb = (ScrollBar) listView.lookup(".scroll-bar:vertical");
            if (sb != null) {
                sb.valueProperty().addListener((observable) -> {
                    VirtualFlow vf = (VirtualFlow) listView.lookup(".virtual-flow");
                    int page = vf.getFirstVisibleCell().getIndex() + 1;
                    pageLabel.setText(page + " / " + listView.getItems().size());
                });
            }
        });

        Thread tr = new Thread(loadPdfTask);
        tr.setDaemon(true);
        tr.setPriority(Thread.MAX_PRIORITY);
        tr.start();
    }

    private void listFilesInDir() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController documentController = loader.getController();
        Stage stage = (Stage) pane.getScene().getWindow();
        Scene scene = new Scene(root);
        String css = getClass().getResource("css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
        documentController.ListFilesInDir(pdf.getParentFile());
    }

}
