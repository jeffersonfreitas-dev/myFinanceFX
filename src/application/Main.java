package application;
	
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/main/MainView.fxml"));
			BorderPane mainPanel = loader.load();
			mainPanel.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
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
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
}
