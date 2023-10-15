package gui.Alerts;

import com.jfoenix.controls.JFXAlert;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class ConfirmationAlert extends Alert {

	private Btn confirmationButton;
	private Btn cancellationButton;
	
	public ConfirmationAlert(AlertIcon icon, Window window){
		super(icon, window);
		setButtons();
	}

	private void setButtons(){
		HBox btnContainer = new HBox(20);
		btnContainer.setAlignment(Pos.CENTER);
		this.confirmationButton = new Btn(BtnType.SUCCESS, "Confirmar");
		this.confirmationButton.setOnAction(e -> this.alert.close());
		this.cancellationButton = new Btn(BtnType.DANGER, "Rechazar");
		this.cancellationButton.setOnAction(e -> this.alert.close());
		btnContainer.getChildren().addAll(cancellationButton, confirmationButton);
		((VBox)this.lookup("#container")).getChildren().add(btnContainer);
	}

	public void setConfirmationButtonText(String text){
		this.confirmationButton.setText(text);
	}

	public void setCancellationButtonText(String text){
		this.cancellationButton.setText(text);
	}

	public void setConfirmationButtonAction(EventHandler<ActionEvent> event){
		this.confirmationButton.setOnAction(e -> {
			this.alert.close();
			event.handle(e);
	});
	}

	public void setCancellationButtonAction(EventHandler<ActionEvent> event){
		this.cancellationButton.setOnAction(e -> {
			this.alert.close();
			event.handle(e);
	});
	}

	public void showAndWait(){
		this.alert.showAndWait();
	}
}
