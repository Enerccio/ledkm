package com.github.enerccio.ledkm.utils;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Utils {
	
	public static boolean isVoid(String value) {
		return value == null || value.matches("\\s*");
	}

	public static void showError(String string, Stage self) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.initModality(Modality.APPLICATION_MODAL);
	    alert.initOwner(self);
	    alert.setContentText(string);
	    alert.show();
	}

}
