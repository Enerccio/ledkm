package com.github.enerccio.ledkm;

import javafx.application.Application;
import javafx.scene.Group;
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
		
		Group root = new Group();
		primaryStage.setScene(new LKMMainScene(root));
		
		primaryStage.show();
	}
	
}
