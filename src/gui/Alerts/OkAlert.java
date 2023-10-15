package gui.Alerts;

import com.jfoenix.controls.JFXAlert;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class OkAlert extends Alert{

	private Btn okButton;
	
	public OkAlert(AlertIcon icon, Window window){
		super(icon, window);
		setOkButton();
	}

	private void setOkButton(){
		this.okButton = new Btn(BtnType.PRIMARY);
		this.okButton.setOnAction(e -> this.alert.close());
		((VBox)this.lookup("#container")).getChildren().add(this.okButton);
	}

	public void setOkButtonText(String text){
		this.okButton.setText(text);
	}

	public void setOkButtonAction(EventHandler<ActionEvent> event){
		this.okButton.setOnAction(e -> {
			this.alert.close();
			event.handle(e);
	});
	}

	public void showAndWait(){
		this.alert.showAndWait();
	}
	
}
