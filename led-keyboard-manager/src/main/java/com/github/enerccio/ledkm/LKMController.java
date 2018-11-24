package com.github.enerccio.ledkm;

import org.hid4java.HidServices;

import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardState;
import com.github.enerccio.ledkm.dialogs.ProfileDialog;
import com.github.enerccio.ledkm.mappings.Profile;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LKMController {

	@FXML
	private MenuItem profileNew;
	@FXML
	private MenuItem profileEdit;
	@FXML
	private MenuItem profileRemove;

	@FXML
	private ScrollPane profilesScrollPane;
	@FXML
	private VBox profiles;
	
	@FXML
	private ScrollPane devicesScrollPane;
	@FXML
	private VBox devices;
	
	@FXML
	private AnchorPane contentAnchor;
	
	@FXML
	private TitledPane sourcesContainer;
	@FXML
	private Accordion sources;
	
	@FXML
	private TitledPane actionsContainer;
	@FXML
	private Accordion actions;

	public void wire() throws Exception {
		for (IKeyboardPlugin kbp : LedKM.manager.getKeyboardPlugins()) {
			kbp.registerKeyboardStateListener(e -> {
				if (e.getState() == KeyboardState.ERROR) {
					// TODO
				}
				LedKM.manager.requestRepaint();
			});
		}
		
		profileNew.setOnAction(this::newProfile);

		Timeline tl = new Timeline(new KeyFrame(Duration.millis(100), e -> {
			iterateLoop();
		}));
		tl.setCycleCount(Animation.INDEFINITE);
		tl.playFromStart();
	}

	private void iterateLoop() {
		HidServices hidScan = LedKM.manager.getHidService();
		hidScan.scan();

		for (IKeyboardPlugin kbp : LedKM.manager.getKeyboardPlugins()) {
			kbp.keyboardEventTick();
		}

		if (LedKM.manager.isDirty()) {
			try {
				LedKM.manager.saveState();
				redrawDevice();
				redrawState();
			} catch (Exception e) {
				// TODO
				e.printStackTrace();
			}

			LedKM.manager.clearDirty();
		}
	}

	private void redrawState() throws Exception {
		showDevices();
	}
	
	public static class DeviceController {
		
		@FXML
		private Label deviceName;
		
		@FXML
		private Label deviceId;
		
		@FXML
		private Slider deviceBrightness;
		
		@FXML
		private CheckBox applies;
		
	}

	private void showDevices() throws Exception {
		double scrollPos = devicesScrollPane.getVvalue();

		devices.getChildren().clear();

		for (IKeyboard kb : LedKM.manager.getKeyboards()) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DeviceController.fxml"));
				DeviceController dc = new DeviceController();
				loader.setController(dc);
				
				GridPane gp = (GridPane) loader.load();

				dc.deviceName.setText(kb.getManufacturer() + " - " + kb.getName());
				dc.deviceName.setTooltip(new Tooltip(kb.getManufacturer() + " - " + kb.getName()));
				dc.deviceId.setText(kb.getIdentification());
				dc.deviceId.setTooltip(new Tooltip(kb.getIdentification()));
				dc.deviceBrightness.valueProperty().set(kb.getBrightness() * 100);
				dc.deviceBrightness.valueProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						float v = newValue.floatValue() / 100.0f;
						kb.setBrightness(v);
					}

				});
				dc.applies.selectedProperty().set(profileApplies(kb));
				dc.applies.selectedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						profileApplyKeyboardChange(newValue, kb);
					}
				});

				gp.setMaxWidth(Double.MAX_VALUE);
				devices.getChildren().add(gp);
			
		}

		devicesScrollPane.setVvalue(scrollPos);
	}
	
	private void redrawDevice() {
		// TODO Auto-generated method stub
		
	}

	private void profileApplyKeyboardChange(Boolean applies, IKeyboard kb) {
		Profile p = (Profile) LedKM.manager.getActiveProfile();
		if (p != null) {
			if (applies) {
				p.getAssignedDevices().add(kb.getIdentification());
			} else {
				p.getAssignedDevices().remove(kb.getIdentification());
			}
		}
		LedKM.manager.requestRepaint();
	}

	private boolean profileApplies(IKeyboard kb) {
		Profile p = (Profile) LedKM.manager.getActiveProfile();
		return p != null && p.getAssignedDevices().contains(kb.getIdentification());
	}

	private void newProfile(ActionEvent event) {
		Profile newProfile = (Profile) LedKM.manager.createNewProfile();
		
		ProfileDialog pd = new ProfileDialog(newProfile, true);
		try {
			pd.show(LedKM.application);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}
}
