package gui.main;

import java.io.IOException;
import java.util.function.Consumer;

import gui.bank.BankViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.service.BankService;
import utils.Alerts;

public class MainViewController {
	
	@FXML
	private MenuBar mnuMain;
	
	@FXML
	private MenuItem mnuItenAbout;
	@FXML
	public void onMnuItemAboutAction(ActionEvent event) {
		FXMLLoader loader = getLoaderView("/gui/about/AboutView.fxml");
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Sobre o sistema", parentScene, 310.0, 460.0, x -> {});
	}
	
	
	@FXML
	private MenuItem mnuItemBank;
	@FXML
	public void onMnuItemBankAction() {
		FXMLLoader loader = getLoaderView("/gui/bank/BankView.fxml");
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de bancos", parentScene, 600.0, 800.0, (BankViewController controller) -> {
			controller.setBankService(new BankService());
			controller.updateTableView();
		});
	}
	
	
	private synchronized <T> void loadModalView(FXMLLoader loader, String title, Window parentScene, double heigth, double width, 
			Consumer<T> initialization) {
		try {
			Pane pane = loader.load();	
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(pane));
			stage.setResizable(false);
			stage.initOwner(parentScene);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(heigth);
			stage.setWidth(width);
			
			T controller = loader.getController();
			initialization.accept(controller);
			
			stage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}


	private FXMLLoader getLoaderView(String absolutePath) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));
		return loader;
	}
	
	
	@SuppressWarnings("unused")
	private synchronized <T> void loadView(String absolutePath, Consumer<T> consumer) {
		
		try {
			FXMLLoader loader = getLoaderView(absolutePath);
			VBox box = loader.load();
			
			VBox mainBox = (VBox) ((ScrollPane) loader.getRoot()).getContent();
			Node mainMenu = mainBox.getChildren().get(0);
			mainBox.getChildren().clear();
			mainBox.getChildren().add(mainMenu);
			mainBox.getChildren().addAll(box.getChildren());
			
			T controller = loader.getController();
			consumer.accept(controller);
		}catch(IOException e) {
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}

}
