/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import java.io.FileInputStream;
import java.io.OutputStream;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.utils.IOUtils;

public class WebApp {

    public void initApp() {
        Spark.port(5088);
        Spark.staticFiles.location("/webapp/static");

        Spark.post("/upload", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    // Create a directory to save uploaded files
                    File uploadDir = new File("uploads");
                    FileUtils.cleanDirectory(uploadDir);
                    uploadDir.mkdir();

                    // Process multipart/form-data request
                    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
                    Collection<Part> parts = request.raw().getParts();
                    for (Part part : parts) {
                        String fileName = part.getSubmittedFileName();
                        if (fileName != null) {
                            File file = new File(uploadDir, fileName);
                            try (InputStream input = part.getInputStream()) {
                                // Save the file
                                java.nio.file.Files.copy(input, file.toPath());
                            }
                        }
                    }

                    return "{\"codigo\": 200}";
                } catch (IOException e) {
                    response.status(500);
                    e.printStackTrace();
                    return "{\"codigo\": 201}";
                }
            }
        });

        Spark.get("/newestpdf", (request, response) -> {
            File staticFilesFolder = new File(System.getProperty("user.dir") + "/src/webapp/static/pdf"); // Update this with the path to your static files folder
            System.out.println(staticFilesFolder.getAbsolutePath());

            // List all PDF files in the static folder
            File[] pdfFiles = staticFilesFolder.listFiles((dir, name) -> name.endsWith(".pdf"));

            if (pdfFiles != null && pdfFiles.length > 0) {
                // Sort the PDF files by last modified timestamp to get the newest one
                Arrays.sort(pdfFiles, Comparator.comparingLong(File::lastModified));

                // Get the newest PDF file
                File newestPdfFile = pdfFiles[pdfFiles.length - 1];

                // Set the response content type to "application/pdf"
                response.type("application/pdf");

                // Open the PDF file as an input stream
                FileInputStream fileInputStream = new FileInputStream(newestPdfFile);

                // Get the response output stream
                OutputStream outputStream = response.raw().getOutputStream();

                // Copy the PDF content to the response output stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close the input and output streams
                fileInputStream.close();
                outputStream.close();

                return response.raw();

            } else {
                response.status(404); // Not Found
                return "No PDF files found in the static folder.";
            }
        });
    }

//        Spark.get("/", (req, res) -> {
//            try {
//                InputStream input = getClass().getResourceAsStream("upload.html");
//                return IOUtils.toString(input);
//           } catch (IOException e) {
//                res.status(500);
//                return "Error loading upload form.";
//            }
//        });
}
