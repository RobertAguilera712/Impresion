/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3.tasks;

import java.io.File;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
            ImageView iv = new ImageView(model.getImage(i));
            iv.setPreserveRatio(true);
            iv.setMouseTransparent(true);
            iv.fitHeightProperty().bind(windowHeight);
            VBox vbox = new VBox();
            final int index = i;
            
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
            pages.add(vbox);
            updateProgress(i, numberOfPages);
        }
        return pages;
    }

}
