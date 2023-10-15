package gui.Alerts;

import com.jfoenix.controls.JFXButton;

public class Btn extends JFXButton{

	public Btn(BtnType type){
		this(type, "Ok");
	}

	public Btn(BtnType type, String text){
		super(text);
		this.getStyleClass().add("btn");
		this.getStyleClass().add(String.format("btn-%s", type.toString().toLowerCase()));
	}
	
}
