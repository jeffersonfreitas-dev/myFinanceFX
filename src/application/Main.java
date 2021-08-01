package application;
	
import java.io.IOException;

import org.kordamp.bootstrapfx.BootstrapFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane mainPanel = setMainPainelConfiguration();
			mainPanel.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			mainPanel.getStyleClass().add("container");
			mainScene = new Scene(mainPanel);
			primaryStage.setScene(mainScene);
			primaryStage.setMaximized(true);
			primaryStage.setTitle("MyFinance v1.0 - Programa para gerenciamento de despesas pessoais");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private BorderPane setMainPainelConfiguration() throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/main/MainView.fxml"));
		BorderPane pane = loader.load();
		pane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
		return pane;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
}
