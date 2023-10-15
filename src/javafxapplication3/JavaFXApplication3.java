package javafxapplication3;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.samuelcampos.usbdrivedetector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import spark.Spark;
import webapp.WebApp;
import java.util.prefs.Preferences;
import javafx.scene.input.KeyCode;

public class JavaFXApplication3 extends Application {

    public static IntegerProperty credito = new SimpleIntegerProperty(0);
    public static int precioBlancoNegro;
    public static int precioColor;
    public static int precioScan;

    public static String webAppAdress;

    public static Preferences userPreferences;

    public static Stage currentStage;

    public static File UsbRootFile;

    private webapp.WebApp app;

    @Override
    public void start(Stage stage) throws Exception {
        
      

        userPreferences = Preferences.userRoot().node(this.getClass().getName());
        precioBlancoNegro = getPrecioBlancoNegro();
        precioColor = getPrecioColor();
        precioScan = getPrecioScan();

        getIp();
        currentStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController documentController = loader.getController();

        Scene scene = new Scene(root);
        String css = getClass().getResource("css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        // DELETE
        scene.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.C) {
                credito.set(credito.get() + 1);
            }
        });

        app = new WebApp();
        app.initApp();
        stage.setScene(scene);

        if (getFullScreen()) {
            Platform.setImplicitExit(false);
            stage.setOnCloseRequest((event) -> {
                event.consume();
            });
            stage.setFullScreen(true);
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setFullScreenExitHint("");
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
        }
        conexion();
        stage.show();
//        documentController.ListFilesInDir(new File("C:\\"));
    }

    public static int getPrecioColor() {
        return userPreferences.getInt("precioColor", 2);
    }

    public static int getPrecioBlancoNegro() {
        return userPreferences.getInt("precioBlancoNegro", 1);
    }

    public static int getPrecioScan() {
        return userPreferences.getInt("precioScan", 3);
    }

    public static boolean getFullScreen() {
        return userPreferences.getBoolean("fullScreen", false);
    }

    public static void setPrecioColor(int precioColor) {
        userPreferences.putInt("precioColor", precioColor);
        JavaFXApplication3.precioColor = precioColor;
    }

    public static void setPrecioBlancoNegro(int precioBlancoNegro) {
        userPreferences.putInt("precioBlancoNegro", precioBlancoNegro);
        JavaFXApplication3.precioBlancoNegro = precioBlancoNegro;
    }

    public static void setPrecioScan(int precioScan) {
        userPreferences.putInt("precioScan", precioScan);
        JavaFXApplication3.precioScan = precioScan;
    }

    public static void setFullScreen(boolean fullScreen) {
        userPreferences.putBoolean("fullScreen", fullScreen);
    }

    public static void setRectangleX(double x) {
        userPreferences.putDouble("rectangleX", x);
    }

    public static void setRectangleY(double y) {
        userPreferences.putDouble("rectangleY", y);
    }

    public static void setRectangleWidth(double width) {
        userPreferences.putDouble("rectangleWidth", width);
    }

    public static void setRectangleHeight(double height) {
        userPreferences.putDouble("rectangleHeight", height);
    }

    public static double getRectangleX() {
        return userPreferences.getDouble("rectangleX", 0);
    }

    public static double getRectangleY() {
        return userPreferences.getDouble("rectangleY", 0);
    }

    public static double getRectangleWidth() {
        return userPreferences.getDouble("rectangleWidth", 0);
    }

    public static double getRectangleHeight() {
        return userPreferences.getDouble("rectangleHeight", 0);
    }

    public static String getPasswordRed() {
        return userPreferences.get("passwordRed", "");
    }

    public static void setPasswordRed(String password) {
        userPreferences.put("passwordRed", password);
    }

    public static String getNombreRed() {
        return userPreferences.get("nombreRed", "");
    }

    public static void setNombreRed(String nombre) {
        userPreferences.put("nombreRed", nombre);
    }

    public static String getPassword() {
        return userPreferences.get("password", "12345");
    }

    public static void setPassword(String password) {
        userPreferences.put("password", password);
    }

    public static void setImageX(double x) {
        userPreferences.putDouble("imageX", x);
    }

    public static void setImageY(double y) {
        userPreferences.putDouble("imageY", y);
    }

    public static void setImageWidth(double width) {
        userPreferences.putDouble("imageWidth", width);
    }

    public static void setImageHeight(double height) {
        userPreferences.putDouble("imageHeight", height);
    }

    public static double getImageX() {
        return userPreferences.getDouble("imageX", 0);
    }

    public static double getImageY() {
        return userPreferences.getDouble("imageY", 0);
    }

    public static double getImageWidth() {
        return userPreferences.getDouble("imageWidth", -1);
    }

    public static double getImageHeight() {
        return userPreferences.getDouble("imageHeight", -1);
    }

    public static USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();
    public static IUSBDriveListener listener;

    @Override
    public void stop() {
        Spark.stop();
        if (port != null && port.isOpen()) {
            port.closePort();
            port.removeDataListener();
        }
        try {
            if (driveDetector != null) {
                if (listener != null) {
                    driveDetector.removeDriveListener(listener);
                }

                driveDetector.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(JavaFXApplication3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Check if the interface is up and not a loopback interface
                if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    if (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        // Filter for IPv4 addresses and exclude link-local addresses
                        if (inetAddress.getHostAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")
                                && !inetAddress.isLinkLocalAddress()) {
                            webAppAdress = "http://" + inetAddress.getHostAddress() + ":5088/upload.html";
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private int productID = 29987;
    private int vendorID = 6790;
    private static SerialPort port;

    private void conexion() {
//		String puerto = "/dev/ttyUSB0";
        int baudios = 9600;
        SerialPort[] sp = SerialPort.getCommPorts();
        Optional<SerialPort> portOptional = Arrays.stream(sp)
                .filter(p -> p.getProductID() == productID && p.getVendorID() == vendorID)
                .findFirst();

        if (portOptional.isPresent()) {
            port = portOptional.get();
            port.setBaudRate(9600);
            port.setNumDataBits(8);
            port.setNumStopBits(1);
            port.setParity(0);
            if (port.openPort()) {
                System.out.println("Puerto abierto");
                aumentarCredio(0);
                activarTragaMoenedas();
                port.addDataListener(dataListener);
            }
        }
//		sp.setBaudRate(baudios);
//		sp.setNumDataBits(8);
//		sp.setNumStopBits(1);
//		sp.setParity(0);
//		sp.openPort();
//		entrada = sp.getInputStream();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void activarTragaMoenedas() {
        System.out.println("Activando traga monedas");
        port.writeBytes(new byte[]{58, 77, 49, 0, 13}, 5);
    }

    public static void darCambio(byte cambio) {
        if (port == null) {
            return;
        }
        byte cantidad = (byte) (cambio + 1);
        port.writeBytes(new byte[]{58, 77, 50, cantidad, 13}, 5);
    }

    public static void setCredito(int credit) {
        Platform.runLater(() -> {
            credito.set(credit);
        });
        /**
         * 3A 4D 33 XX 0D Dónde 3A = : en ASCII, 58 en byte 4D =M en ASCII, 77
         * en byte 33 = 3 en ASCII, 51 en byte XX = Cantidad de dinero que
         * escribirá en la pantalla LCD expresada en hexadecimal, variable
         * nuevoCredito 0D = Byte de final de cadena, 13 en byte
         */
        if (port == null) {
            return;
        }
        port.writeBytes(new byte[]{58, 77, 51, (byte) credit, 13}, 5);
    }

    private void aumentarCredio(int valor) {

        byte nuevoCredito = (byte) (credito.get() + valor);
        Platform.runLater(() -> {
            credito.set(credito.get() + valor);
        });
        /**
         * 3A 4D 33 XX 0D Dónde 3A = : en ASCII, 58 en byte 4D =M en ASCII, 77
         * en byte 33 = 3 en ASCII, 51 en byte XX = Cantidad de dinero que
         * escribirá en la pantalla LCD expresada en hexadecimal, variable
         * nuevoCredito 0D = Byte de final de cadena, 13 en byte
         */
        if (port == null) {
            return;
        }
        port.writeBytes(new byte[]{58, 77, 51, nuevoCredito, 13}, 5);

    }

    public interface ConfirmationListener {

        void onConfirm();

        void onCancel();
    }

    public static void showConfirmation(String title, String message, StackPane root, ConfirmationListener listener) {
        // Create the JFXDialogLayout
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label(title));
        dialogLayout.setBody(new Label(message));

        // Create the JFXDialog
        JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.CENTER);

        // Create "Yes" and "No" buttons
        JFXButton yesButton = new JFXButton("Si");
        JFXButton noButton = new JFXButton("No");

        // Handle button actions
        yesButton.setOnAction(e -> {
            listener.onConfirm();
            dialog.close();
        });

        noButton.setOnAction(e -> {
            listener.onCancel();
            dialog.close();
        });

        // Add the buttons to the layout
        dialogLayout.setActions(yesButton, noButton);

        // Show the dialog
        dialog.show();
    }

    SerialPortDataListener dataListener = new SerialPortDataListener() {
        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; // We want to listen for data available events
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                SerialPort serialPort = event.getSerialPort();
                byte[] newData = new byte[serialPort.bytesAvailable()];
                int numRead = serialPort.readBytes(newData, newData.length);
                byte primerByte = newData[0];
                switch (primerByte) {
                    case 58:
                        aumentarCredio(newData[1]);
                        break;
                }
            }
        }

    };

}
