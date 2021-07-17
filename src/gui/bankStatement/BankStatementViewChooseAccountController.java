package gui.bankStatement;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.entities.BankAccount;
import model.entities.Moviment;
import model.service.BankAccountService;
import model.service.BankStatementService;
import model.service.MovimentService;
import utils.Alerts;
import utils.Utils;

public class BankStatementViewChooseAccountController implements Initializable{
	
	private BankStatementService service;
	public void setBankStatementService(BankStatementService service) {
		this.service = service;
	}

	private BankAccountService accountService;
	public void setBankAccountService(BankAccountService accountService) {
		this.accountService = accountService;
	}

	private MovimentService movimentService;
	public void setMovimentService(MovimentService movimentService) {
		this.movimentService = movimentService;
	}
	

	
	@FXML
	private ComboBox<BankAccount> cmbAccount;
	private void initializeComboBoxAccount() {
		Callback<ListView<BankAccount>, ListCell<BankAccount>> factory = lv -> new ListCell<BankAccount>() {
			@Override
			protected void updateItem(BankAccount item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getAccount() +" - "+ item.getBankAgence().getBank().getName());
			}
		};
		cmbAccount.setCellFactory(factory);
		cmbAccount.setButtonCell(factory.call(null));
	}
	
	
	@FXML
	private ComboBox<Moviment> cmbMovimentation;
	private void initializeComboBoxMoviment() {
		Callback<ListView<Moviment>, ListCell<Moviment>> factory = lv -> new ListCell<Moviment>() {
			@Override
			protected void updateItem(Moviment item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cmbMovimentation.setCellFactory(factory);
		cmbMovimentation.setButtonCell(factory.call(null));
	}
	

	@FXML
	private Button btnOpen;
	@FXML
	public void onBtnOpenAction(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bankStatement/BankStatementView.fxml"));
		loadView(loader);
		Stage stage = Utils.getCurrentStage(event);
		stage.close();
	}
	

	@FXML
	private Button btnClose;
	@FXML
	public void onBtnCloseAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.close();
	}
	
	
	@FXML
	private Label lblErros;
	
	private ObservableList<BankAccount> obsAccount;
	private ObservableList<Moviment> obsMoviment;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	private void initializationNodes() {
		btnClose.setGraphic(new ImageView("/assets/icons/cancel16.png"));
		btnOpen.setGraphic(new ImageView("/assets/icons/into16.png"));
		initializeComboBoxAccount();
		initializeComboBoxMoviment();
	}
	
	
	public void loadAssociateObjects() {
		if(service == null || accountService == null || movimentService == null) {
			throw new IllegalStateException("Serviços não instanciados");
		}
		
		List<BankAccount> accounts = accountService.findAll();
		obsAccount = FXCollections.observableArrayList(accounts);
		cmbAccount.setItems(obsAccount);
		
		List<Moviment> moviments = movimentService.findAll();
		obsMoviment = FXCollections.observableArrayList(moviments);
		cmbMovimentation.setItems(obsMoviment);
	}
	
	
	private void loadView(FXMLLoader loader) {
		try {
			VBox box = loader.load();
			Scene scene = Main.getMainScene();
			
			VBox mainBox = (VBox) ((ScrollPane) scene.getRoot()).getContent();
			Node node = mainBox.getChildren().get(0);
			mainBox.getChildren().clear();
			mainBox.getChildren().add(node);
			mainBox.getChildren().addAll(box.getChildren());
			mainBox.prefWidthProperty().bind(Main.getMainScene().widthProperty().multiply(1));
			BankStatementViewController controller = loader.getController();
			controller.setBankStatementService(new BankStatementService());
			controller.updateTableView();
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}

}
