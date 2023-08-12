package javafxapplication3;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXPopup;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafxapplication3.models.NoSelectionModel;
import javafxapplication3.models.PdfModel;
import javafxapplication3.tasks.LoadPdfTask;
import javafxapplication3.tasks.WordToPdfTask;
import javax.swing.GroupLayout;

public class FXMLPdfViewverController implements Initializable {

    private File pdf;
    private File word;
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
    private WordToPdfTask wordToPdfTask;
    BooleanProperty loading = new SimpleBooleanProperty(false);
    private Popup popup;

    public void setPdf(File pdf) {
        try {
            this.pdf = pdf;
            titleLabel.setText(pdf.getAbsolutePath());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLImprimir.fxml"));
            Parent root = loader.load();
            FXMLImprimirController imprimirController = loader.getController();
            popup = new Popup();
            imprimirController.setPdf(pdf);
            imprimirController.setCancelar((event) -> {
                popup.hide();
            });
            popup.centerOnScreen();
            popup.getContent().add(root);
        } catch (IOException ex) {
            Logger.getLogger(FXMLPdfViewverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPdfFromWord(File word) {
        this.word = word;
        titleLabel.setText(word.getAbsolutePath());
        convertWordToPdf(word);
    }

    private void convertWordToPdf(File inputWord) {
        wordToPdfTask = new WordToPdfTask(inputWord);
        progressLabel.textProperty().bind(wordToPdfTask.messageProperty());
        loading.bind(wordToPdfTask.runningProperty());
        wordToPdfTask.setOnSucceeded((event) -> {
            pdf = wordToPdfTask.getValue();
            if (pdf != null) {
                showPdf();
            }
        });
        Thread tr = new Thread(wordToPdfTask);
        tr.setDaemon(true);
        tr.setPriority(Thread.MAX_PRIORITY);
        tr.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progressVbox.visibleProperty().bind(loading);

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
                    listFilesInDir(pdf);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLPdfViewverController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (wordToPdfTask != null && wordToPdfTask.isRunning()) {
                wordToPdfTask.cancel();
                try {
                    listFilesInDir(word);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLPdfViewverController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btnAtras.setOnAction((event) -> {
            try {
                listFilesInDir(pdf);
            } catch (IOException ex) {
                Logger.getLogger(FXMLPdfViewverController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnImprimir.setOnAction((event) -> {
            
            popup.show(pane.getScene().getWindow());
        });
        viewerGroup.visibleProperty().bind(progressVbox.visibleProperty().not());
        progressVbox.layoutXProperty().bind(pane.widthProperty().subtract(progressVbox.widthProperty()).divide(2));
        progressVbox.layoutYProperty().bind(pane.heightProperty().subtract(progressVbox.heightProperty()).divide(2));
        buttonsHbox.layoutXProperty().bind(pane.widthProperty().subtract(buttonsHbox.widthProperty()).subtract(20));
        zoomHbox.layoutXProperty().bind(pane.widthProperty().subtract(zoomHbox.widthProperty()).divide(2));
        titleLabel.layoutXProperty().bind(pane.widthProperty().subtract(titleLabel.widthProperty()).divide(2));

        Label creditoLabel = new Label();
        creditoLabel.getStyleClass().add("credito");
        pane.getChildren().add(creditoLabel);
        creditoLabel.layoutXProperty().bind(pane.widthProperty().subtract(creditoLabel.widthProperty()).subtract(40));
        creditoLabel.layoutYProperty().bind(pane.heightProperty().subtract(creditoLabel.heightProperty()).subtract(20));
        creditoLabel.textProperty().bind(JavaFXApplication3.credito.asString("Crédito: $%d"));

    }

    public void showPdf() {
        loadPdfTask = new LoadPdfTask(pdf, pageHeightPropery);

        progressLabel.textProperty().bind(loadPdfTask.messageProperty());
        loading.bind(loadPdfTask.runningProperty());
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

    private void listFilesInDir(File file) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController documentController = loader.getController();
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.getScene().setRoot(root);
        documentController.ListFilesInDir(file.getParentFile());
    }

}
