package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			ScrollPane mainPanel = setMainPainelConfiguration();
			mainScene = new Scene(mainPanel);
			primaryStage.setScene(mainScene);
			primaryStage.setMaximized(true);
			primaryStage.setTitle("MyFinance v1.0 - Programa para gerenciamento de despesas pessoais");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private ScrollPane setMainPainelConfiguration() throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/main/MainView.fxml"));
		ScrollPane pane = loader.load();
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
		return pane;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
}
