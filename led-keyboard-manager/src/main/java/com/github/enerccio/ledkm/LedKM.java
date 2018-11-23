package com.github.enerccio.ledkm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LedKM extends Application {
	
	public static LKM manager;
	
	public static void main(String[] args) throws Exception {
		manager = new LKM();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Led Keyboard Manager");
		
		AnchorPane root = FXMLLoader.load(getClass().getResource("/scenes/MainWindow.fxml"));
		LKMMainScene scene = new LKMMainScene(root);
		primaryStage.setScene(scene);
		
		primaryStage.show();
		
		scene.wire();
		manager.requestRepaint();
	}

	@Override
	public void stop() throws Exception {
		manager.saveState();
		manager.shutdown();
	}
	
	
}
