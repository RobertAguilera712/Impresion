package gui.Alerts;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.stage.Window;
import javafx.util.Duration;

public class WaitAlert extends Alert {

    private PauseTransition pause;

    public WaitAlert(AlertIcon icon, Window window) {
        super(icon, window);
    }

    public void showAndWaitFor(double seconds) {
        this.pause = new PauseTransition(Duration.seconds(seconds));
        this.pause.setOnFinished(e -> {
            this.alert.close();
        });
        this.alert.show();
        this.pause.play();
    }

    public void Show() {
        this.alert.show();
    }

    public void Close() {
        this.alert.close();
    }

}
