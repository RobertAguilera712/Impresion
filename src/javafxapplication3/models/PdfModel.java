/*
 * Copyright (c) TAKAHASHI,Toru 2015
 */
package javafxapplication3.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * PDF文書のページを表すクラス。
 *
 * @author toru
 */
public class PdfModel {

    private static final Logger logger = Logger.getLogger(PdfModel.class.getName());

    private PDDocument document;
    private PDFRenderer renderer;

    public PdfModel(File file) {
        try {
            document = PDDocument.load(file);
            renderer = new PDFRenderer(document);
        } catch (IOException ex) {
            throw new UncheckedIOException("PDDocument thorws IOException file=" + file.getAbsolutePath(), ex);
        }
    }

    public int numPages() {
        return document.getPages().getCount();
    }

    public Image getImage(int pageNumber) {
        try {
            BufferedImage pageImage = renderer.renderImage(pageNumber);
            return SwingFXUtils.toFXImage(pageImage, null);
        } catch (IOException ex) {
            System.out.println("ERROR");
        }
        return null;
    }
}
