/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3.tasks;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javafx.concurrent.Task;

/**
 *
 * @author Kasparov
 */
public class WordToPdfTask extends Task<File> {

    private final File inputWord;

    @Override
    protected File call() throws Exception {
        updateMessage("Convirtiendo documento Word a PDF");
        File outputFile = new File(inputWord.getParentFile().getAbsolutePath(), inputWord.getName().concat(".pdf"));
        try {
            InputStream docxInputStream = new FileInputStream(inputWord);
            OutputStream outputStream = new FileOutputStream(outputFile);
            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
            outputStream.close();
            docxInputStream.close();
            converter.shutDown();
            System.out.println("FILE CONVERTED SUCCESSFULLY");
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WordToPdfTask(File inputWord) {
        this.inputWord = inputWord;
    }

}
