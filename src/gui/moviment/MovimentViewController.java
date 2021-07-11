package gui.moviment;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Moviment;
import model.service.BankStatementService;
import utils.Alerts;
import utils.Utils;

public class MovimentViewController implements Initializable{
	
	private BankStatementService service;
	public void setBankStatementService(BankStatementService service) {
		this.service = service;
	}
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Alerts.showAlert("Aviso", "Aviso de implementa��o", "Esta fun��o n�o foi implementada!", AlertType.INFORMATION);
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
//		columnCode.setCellValueFactory(new PropertyValueFactory<>("id"));
//		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
//		Utils.formatTableColumnDate(columnDate, "dd/MM/yyyy");
//		columnHistoric.setCellValueFactory(new PropertyValueFactory<>("historic"));
//		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
//		Utils.formatTableColumnDouble(columnValue, 2);
//		columnCredit.setCellValueFactory(v -> {
//			String result = "";
//			result = v.getValue().isCredit() ? "C" : "D";
//			return new ReadOnlyStringWrapper(result);
//		});
//		columnAccount.setCellValueFactory(v -> {
//			return new ReadOnlyStringWrapper(v.getValue().getBankAccount().getCode());
//		});
	}


	public void updateTableView() {
//		if(service == null) {
//			throw new IllegalStateException("O servi�o n�o foi instanciado");
//		}
//		List<BankStatement> list = service.findAllOrderByDateAndBankAccount();
//		obsList = FXCollections.observableArrayList(list);
//		tblBankStatement.setItems(obsList);
//		initializationNodes();
	}
	
	
//	private synchronized <T> void loadViewModal(Billpay billpay, FXMLLoader loader, Window parent, String title, double height, double width,
//			Consumer<T> initialization) {
//		try {
//			Pane pane = loader.load();
//			Stage stage = new Stage();
//			stage.setTitle(title);
//			stage.setScene(new Scene(pane));
//			stage.setResizable(false);
//			stage.initOwner(parent);
//			stage.initModality(Modality.WINDOW_MODAL);
//			stage.setHeight(height);
//			stage.setWidth(width);
//			
//			T controller = loader.getController();
//			initialization.accept(controller);
//			
//			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//				@Override
//				public void handle(WindowEvent event) {
//					updateTableView();
//				}
//			});
//			stage.showAndWait();
//		} catch (IOException e) {
//			e.printStackTrace();
//			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
//		}
//	}	
	
	
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
