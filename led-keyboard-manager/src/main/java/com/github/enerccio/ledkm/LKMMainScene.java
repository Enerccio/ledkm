package com.github.enerccio.ledkm;

import java.io.IOException;
import java.util.Map;

import org.hid4java.HidServices;

import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardState;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LKMMainScene extends Scene {

	public LKMMainScene(Parent root) throws IOException {
		super(root, 1000, 600);
	}
	
	public void wire() {
		for (IKeyboardPlugin kbp : LedKM.manager.getKeyboardPlugins()) {
			kbp.registerKeyboardStateListener(e -> {
				if (e.getState() == KeyboardState.ERROR) {
					// TODO
				}
				LedKM.manager.requestRepaint();
			});
		}
		
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
				LedKM.manager.redrawDevice();
				redrawState();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			LedKM.manager.clearDirty();
		}
	}

	private void redrawState() throws Exception {
		
		showDevices();
	}

	private void showDevices() throws Exception {
		VBox vb = (VBox) lookup("#devices");
		
		ScrollPane sp = (ScrollPane) lookup("#devicesSP"); 
		double scrollPos = sp.getVvalue();
		
		vb.getChildren().clear();
		
		for (IKeyboardPlugin kbp : LedKM.manager.getKeyboardPlugins()) {
			for (IKeyboard kb : kbp.getKeyboards()) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DeviceController.fxml"));
				GridPane gp = (GridPane) loader.load();
				Map<String, Object> namespace = loader.getNamespace();
				
				((Label) namespace.get("deviceName")).setText(kb.getManufacturer() + " - " + kb.getName());
				((Label) namespace.get("deviceName")).setTooltip(new Tooltip(kb.getManufacturer() + " - " + kb.getName()));
				((Label) namespace.get("deviceId")).setText(kb.getIdentification());
				((Label) namespace.get("deviceId")).setTooltip(new Tooltip(kb.getIdentification()));
				((Slider) namespace.get("deviceBrightness")).valueProperty().set(kb.getBrightness() * 100);
				((Slider) namespace.get("deviceBrightness")).valueProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						float v = newValue.floatValue() / 100.0f;
						kb.setBrightness(v);
					}
					
				});
				((CheckBox) namespace.get("applies")).selectedProperty().set(profileApplies(kb));
				((CheckBox) namespace.get("applies")).selectedProperty().addListener(new ChangeListener<Boolean>() {
				    @Override
				    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				        profileApplyKeyboardChange(newValue, kb);
				    }
				});
				
				gp.setMaxWidth(Double.MAX_VALUE);
				vb.getChildren().add(gp);
			}
		}
		
		sp.setVvalue(scrollPos);
	}

	private void profileApplyKeyboardChange(Boolean applies, IKeyboard kb) {
		// TODO
		LedKM.manager.requestRepaint();
	}

	private boolean profileApplies(IKeyboard kb) {
		// TODO
		return true;
	}
	
}
