package com.github.enerccio.ledkm.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseDialog<CONTROLLER> {
	
	private String fxml;
	private String title;
	private int width;
	private int height;
	private boolean modal;
	
	public BaseDialog(String fxml, String title, int width, int height, boolean modal) {
		this.fxml = fxml;
		this.title = title;
		this.width = width;
		this.height = height;
		this.modal = modal;
	}
	
	protected Stage self;

	public void show(Stage parent) throws Exception {
		self = new Stage();
		self.setTitle(title);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		CONTROLLER controller = createController();
		loader.setController(controller);
		Parent root = loader.load();
		
		Scene scene = new Scene(root, width, height);
		wire(scene, controller);
		
		self.setScene(scene);
		self.initOwner(parent);
		self.initModality(modal ? Modality.APPLICATION_MODAL : Modality.NONE); 
		self.showAndWait();
	}

	protected abstract CONTROLLER createController();
	
	protected abstract void wire(Scene scene, CONTROLLER controller);
}
