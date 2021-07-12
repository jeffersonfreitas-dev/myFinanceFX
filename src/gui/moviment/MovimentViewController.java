package gui.moviment;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import model.entities.Moviment;
import model.service.MovimentService;
import utils.Alerts;
import utils.Utils;

public class MovimentViewController implements Initializable{
	
	private MovimentService service;
	public void setMovimentService(MovimentService service) {
		this.service = service;
	}
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/moviment/MovimentViewRegister.fxml")); 
		Window window = btnNew.getScene().getWindow();
		Moviment moviment = new Moviment();
		loadViewModal(moviment, loader, window, "Cadastro de movimentação", 170.0, 500.0, null);
	}


	@FXML
	private Button btnClose;
	@FXML
	public void onBtnCloseAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		loadView(stage);
	}


	@FXML
	private TableView<Moviment> tblMoviment;
	@FXML
	private TableColumn<Moviment, Integer> columnCode;
	@FXML
	private TableColumn<Moviment, Date> columnDateBeginner;
	@FXML
	private TableColumn<Moviment, Date> columnDateFinish;
	@FXML
	private TableColumn<Moviment, Double> columnValueFinish;
	@FXML
	private TableColumn<Moviment, Double> columnBalance;
	@FXML
	private TableColumn<Moviment, Double> columnValueBeginner;
	@FXML
	private TableColumn<Moviment, String> columnName;
	@FXML
	private TableColumn<Moviment, String> columnStatus;
	
	private ObservableList<Moviment> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
	
		btnNew.setGraphic(new ImageView("/assets/icons/new16.png"));
		btnClose.setGraphic(new ImageView("/assets/icons/cancel16.png"));
		columnCode.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnDateBeginner.setCellValueFactory(new PropertyValueFactory<>("dateBeginner"));
		Utils.formatTableColumnDate(columnDateBeginner, "dd/MM/yyyy");
		columnDateFinish.setCellValueFactory(new PropertyValueFactory<>("dateFinish"));
		Utils.formatTableColumnDate(columnDateFinish, "dd/MM/yyyy");
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnBalance.setCellValueFactory(new PropertyValueFactory<>("balanceMoviment"));
		Utils.formatTableColumnDouble(columnValueBeginner, 2);
		columnValueFinish.setCellValueFactory(new PropertyValueFactory<>("valueFinish"));
		Utils.formatTableColumnDouble(columnValueFinish, 2);
		columnValueBeginner.setCellValueFactory(new PropertyValueFactory<>("valueBeginner"));
		Utils.formatTableColumnDouble(columnValueBeginner, 2);
		columnStatus.setCellValueFactory(v -> {
			String result = "";
			result = v.getValue().isClosed() ? "F" : "A";
			return new ReadOnlyStringWrapper(result);
		});
	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<Moviment> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblMoviment.setItems(obsList);
		initializationNodes();
	}
	
	
	private synchronized <T> void loadViewModal(Moviment moviment, FXMLLoader loader, Window parent, String title, double height, double width,
			Consumer<T> initialization) {
		try {
			Pane pane = loader.load();
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(pane));
			stage.setResizable(false);
			stage.initOwner(parent);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(height);
			stage.setWidth(width);
			
			T controller = loader.getController();
			initialization.accept(controller);
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					updateTableView();
				}
			});
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}	
	
	
	private void loadView(Stage stage) {
		try {
			VBox mainBox =  (VBox) ((ScrollPane) stage.getScene().getRoot()).getContent();
			Node mnu = mainBox.getChildren().get(0);
			mainBox.getChildren().clear();
			mainBox.getChildren().add(mnu);
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}
}
