package javafxapplication3;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
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

public class JavaFXApplication3 extends Application {

    public static IntegerProperty credito = new SimpleIntegerProperty(0);
    public static int precioBlancoNegro = 1;
    public static int precioColor = 2;
    
    public static Stage currentStage;

    @Override
    public void start(Stage stage) throws Exception {
        currentStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController documentController = loader.getController();

        Scene scene = new Scene(root);
        String css = getClass().getResource("css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);

//        Platform.setImplicitExit(false);
//        stage.setOnCloseRequest((event) -> {
//            event.consume();
//        });
//        stage.setFullScreen(true);
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
//        stage.setFullScreenExitHint("");
//        stage.setResizable(false);
        conexion();
        stage.show();
//        documentController.ListFilesInDir(new File("C:\\"));
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
    
    private void activarTragaMoenedas(){
             port.writeBytes(new byte[]{58, 77, 49, 0, 13}, 5);
    }
    
    public static void darCambio(byte cambio){
        byte cantidad = (byte) (cambio + 1);
     port.writeBytes(new byte[]{58, 77, 50, cantidad, 13}, 5);
    }

        private void aumentarCredio(int valor) {
        byte nuevoCredito = (byte) (credito.get() + valor);
        Platform.runLater(() -> {
            credito.set(credito.get() + valor);
        });
        /**
         * 3A 4D 33 XX 0D 
         * Dónde 
         * 3A = : en ASCII, 58 en byte
         * 4D =M en ASCII, 77 en byte
         * 33 = 3 en ASCII, 51 en byte
         * XX = Cantidad de dinero que escribirá en la pantalla LCD expresada en
         * hexadecimal, variable nuevoCredito
         * 0D = Byte de final de cadena, 13 en byte
         */
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
