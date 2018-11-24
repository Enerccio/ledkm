package com.github.enerccio.ledkm.dialogs;

import com.github.enerccio.ledkm.LedKM;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.mappings.Profile;
import com.github.enerccio.ledkm.utils.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class ProfileDialog extends BaseDialog<ProfileDialogController> {

	private Profile profile;
	private boolean creating;
	
	public ProfileDialog(Profile profile, boolean creating) {
		super("/scenes/NewProfileDialog.fxml", "Profile editor", 450, 210, true);
		this.profile = profile;
		this.creating = creating;
	}

	@Override
	protected ProfileDialogController createController() {
		return new ProfileDialogController();
	}

	@Override
	protected void wire(Scene scene, ProfileDialogController controller) {
		self.initStyle(StageStyle.UTILITY);
		self.setResizable(false);
		
		controller.name.setText(profile.getName());
		controller.uuid.setText(profile.getUuid());
		controller.columns.setText("" + profile.getColumns());
		controller.rows.setText("" + profile.getRows());
		
		controller.uuid.setDisable(true);
		
		ObservableList<IKeyboard> devices = FXCollections.observableArrayList();
		devices.addAll(LedKM.manager.getKeyboards());
		
		controller.devices.setItems(devices);
		controller.devices.setConverter(new StringConverter<IKeyboard>() {
			
			@Override
			public String toString(IKeyboard object) {
				if (object == null)
					return "";
				return object.getManufacturer() + " - " + object.getName() + " / " + object.getIdentification();
			}
			
			@Override
			public IKeyboard fromString(String string) {
				return null;
			}
			
		});
		controller.devices.valueProperty().addListener((event, oldItem, newItem) -> {
			controller.columns.setText("" + newItem.getColumns());
			controller.rows.setText("" + newItem.getRows());
		});
		
		controller.ok.setOnAction(event -> {
			saveValues(controller);
		});
		
		controller.cancel.setOnAction(event -> {
			closeDialog();
		});
		self.setOnCloseRequest(event -> {
			closeDialog();
		});
	}

	private void saveValues(ProfileDialogController controller) {
		if (Utils.isVoid(controller.name.getText())) {
			Utils.showError("Name must not be empty", self);
			return;
		}
		
		if (Utils.isVoid(controller.columns.getText())) {
			Utils.showError("Columns must not be empty", self);
			return;
		}
		
		if (Utils.isVoid(controller.rows.getText())) {
			Utils.showError("Rows must not be empty", self);
			return;
		}
		
		Integer columns = null;
		try {
			columns = Integer.parseInt(controller.columns.getText());
		} catch (NumberFormatException e) {
			Utils.showError("Columns must be parseable number", self);
			return;
		}
		
		Integer rows = null;
		try {
			rows = Integer.parseInt(controller.rows.getText());
		} catch (NumberFormatException e) {
			Utils.showError("Rows must be parseable number", self);
			return;
		}
		
		if (columns <= 0) {
			Utils.showError("Columns must be positive number", self);
			return;
		}
		
		if (rows <= 0) {
			Utils.showError("Rows must be positive number", self);
			return;
		}
		
		profile.setName(controller.name.getText());
		profile.setColumns(columns);
		profile.setRows(rows);
		
		creating = false;
		self.close();
		LedKM.manager.requestRepaint();
	}

	private void closeDialog() {
		if (creating) {
			LedKM.manager.removeProfile(profile);
			LedKM.manager.requestRepaint();
		}
	}
	
}

class ProfileDialogController {
	
	@FXML
	TextField name;
	
	@FXML
	TextField uuid;
	
	@FXML
	ChoiceBox<IKeyboard> devices;
	
	@FXML
	TextField columns;
	
	@FXML
	TextField rows;
	
	@FXML
	Button ok;
	
	@FXML
	Button cancel;
	
}
