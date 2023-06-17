/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3.tasks;

import java.io.File;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafxapplication3.models.PdfModel;

/**
 *
 * @author Kasparov
 */
public class LoadPdfTask extends Task<ObservableList<VBox>> {

    private final int numberOfPages;
    private final DoubleProperty windowHeight;
    private final PdfModel model;

    public LoadPdfTask(File pdfFile, DoubleProperty windowHeight) {
        model = new PdfModel(pdfFile);
        numberOfPages = model.numPages();
        this.windowHeight = windowHeight;
    }

    @Override
    protected ObservableList<VBox> call() throws Exception {
        updateMessage("Cargargo archivo...");
        ObservableList<VBox> pages = FXCollections.observableArrayList();
        for (int i = 0; i < numberOfPages; i++) {
            if (isCancelled()) {
                updateMessage("Cancelled");
                break;
            }
            System.out.println("PAGE " + i + "Added");
            ImageView iv = new ImageView(model.getImage(i));
            iv.setPreserveRatio(true);
       
            iv.fitHeightProperty().bind(windowHeight);
            VBox vbox = new VBox();
            vbox.setStyle("-fx-background-color: #d4d4d7");
            vbox.getChildren().add(iv);
            vbox.setAlignment(Pos.TOP_CENTER);
            pages.add(vbox);
            updateProgress(i, numberOfPages);
        }
        return pages;
    }

}
