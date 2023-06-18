package javafxapplication3;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
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

    public void setPdf(File pdf) {
        this.pdf = pdf;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.setSelectionModel(new NoSelectionModel<>());
        listView.minWidthProperty().bind(pane.widthProperty().subtract(listView.layoutXProperty().multiply(2)));
        listView.minHeightProperty().bind(pane.heightProperty().subtract(listView.layoutYProperty()));
        listView.maxHeightProperty().bind(pane.heightProperty().subtract(listView.layoutYProperty()));
        listView.setOnZoom((ZoomEvent event) -> {
            pageHeightPropery.set(pageHeightPropery.multiply(event.getZoomFactor()).get());
        });
        pageHeightPropery = new SimpleDoubleProperty(listView.heightProperty().get());
        zoomValue = new SimpleIntegerProperty(0);
        zoomLabel.textProperty().bind(zoomValue.asString());
        zoomInBtn.setOnAction((event) -> {
            pageHeightPropery.set(pageHeightPropery.add(initZoom * 0.1).get());
        });
        zoomOutBtn.setOnAction((event) -> {
            pageHeightPropery.set(pageHeightPropery.subtract(initZoom * 0.1).get());
        });
    }

    public void showPdf() {
        LoadPdfTask loadPdfTask = new LoadPdfTask(pdf, pageHeightPropery);
        pageHeightPropery.set(listView.heightProperty().get());
        initZoom = listView.heightProperty().get();
        zoomValue.bind(pageHeightPropery.divide(initZoom).multiply(100));

        progressLabel.textProperty().bind(loadPdfTask.messageProperty());
        progressVbox.visibleProperty().bind(loadPdfTask.runningProperty());
        progressSpinner.progressProperty().bind(loadPdfTask.progressProperty());
        loadPdfTask.valueProperty().addListener((ObservableValue<? extends ObservableList<VBox>> observable, ObservableList<VBox> oldValue, ObservableList<VBox> newValue) -> {
            listView.setItems(newValue);
        });
        loadPdfTask.setOnSucceeded((event) -> {
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

}
