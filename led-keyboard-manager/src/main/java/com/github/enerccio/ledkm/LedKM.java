package com.github.enerccio.ledkm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LedKM extends Application {
	
	public static LKM manager;
	public static Stage application;
	
	public static void main(String[] args) throws Exception {
		manager = new LKM();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		application = primaryStage;
		
		primaryStage.setTitle("Led Keyboard Manager");
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/MainWindow.fxml"));
		LKMController c = new LKMController();
		fxmlLoader.setController(c);
		AnchorPane root = fxmlLoader.load();
		
		Scene scene = new Scene(root, 1000, 600);
		primaryStage.setScene(scene);
		
		primaryStage.show();
		
		c.wire();
		manager.requestRepaint();
	}

	@Override
	public void stop() throws Exception {
		manager.saveState();
		manager.shutdown();
	}
	
	
}
