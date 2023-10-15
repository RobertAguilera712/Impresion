package gui.Alerts;

import com.jfoenix.controls.JFXAlert;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public abstract class Alert extends VBox{

	final protected JFXAlert<Void> alert;

	public Alert(AlertIcon icon, Window window){
		super();
		Node nodo;
		try {
			nodo = FXMLLoader.load(getClass().getResource("Alerta.fxml"));
			this.getChildren().setAll(nodo);
		} catch (IOException ex) {
			Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
		}
		this.setIcon(icon);
		this.alert = new JFXAlert<>(window);
		this.alert.setOverlayClose(false);
		this.alert.setContent(this);
	}

	public void setTitle(String title){
		((Label)this.lookup("#txtTitle")).setText(title);
		this.alert.setTitle(title);
	}

	public void setTextContent(String content){
		((Label)this.lookup("#txtContent")).setText(content);
	}

	private void setIcon(AlertIcon icon){
		Label iconLabel = (Label) this.lookup("#icon");
		switch (icon){
			case WARNING:
				iconLabel.getStyleClass().add("warning");
				iconLabel.getStyleClass().add("bg-warning");
				break;
			case ERROR:
				iconLabel.getStyleClass().add("error");
				iconLabel.getStyleClass().add("bg-danger");
				break;
			case QUESTION:
				iconLabel.getStyleClass().add("question");
				iconLabel.getStyleClass().add("bg-primary");
				break;
			case SUCCESS:
				iconLabel.getStyleClass().add("success");
				iconLabel.getStyleClass().add("bg-success");
				iconLabel.getStyleClass().add("text-success");
				break;
		}
	}
}
