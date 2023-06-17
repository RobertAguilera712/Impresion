package javafxapplication3;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafxapplication3.models.PdfModel;
import javafxapplication3.tasks.LoadPdfTask;

/**
 * FXML Controller class
 *
 * @author jjj
 */
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
    
    public void setPdf(File pdf) {
        this.pdf = pdf;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.minWidthProperty().bind(pane.widthProperty());
        listView.minHeightProperty().bind(pane.heightProperty().subtract(100));
        listView.maxHeightProperty().bind(pane.heightProperty().subtract(100));
    }

    public void showPdf() {
        LoadPdfTask loadPdfTask = new LoadPdfTask(pdf, listView.maxHeightProperty());
        progressLabel.textProperty().bind(loadPdfTask.messageProperty());
        progressVbox.visibleProperty().bind(loadPdfTask.runningProperty());
        progressSpinner.progressProperty().bind(loadPdfTask.progressProperty());
        loadPdfTask.valueProperty().addListener((ObservableValue<? extends ObservableList<VBox>> observable, ObservableList<VBox> oldValue, ObservableList<VBox> newValue) -> {
            listView.setItems(newValue);
        });
        loadPdfTask.setOnFailed((WorkerStateEvent event) -> {
            loadPdfTask.getException().printStackTrace();
        });
       
        Thread tr = new Thread(loadPdfTask);
        tr.setDaemon(true);
        tr.setPriority(Thread.MAX_PRIORITY);
        tr.start();
    }

}
