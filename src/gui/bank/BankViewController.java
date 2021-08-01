package gui.bank;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Bank;
import model.service.BankService;
import utils.Alerts;
import utils.Utils;

public class BankViewController implements Initializable{
	
	private BankService service;
	
	public void setBankService(BankService service) {
		this.service = service;
	}
	

//	@FXML
//	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.setTitle("Cadastro de banco");
		Bank bank = new Bank();
		loadView(bank, "/gui/bank/BankViewRegister.fxml", stage.getScene());
	}
	
	@FXML
	private TableView<Bank> tblView;
	@FXML
	private TableColumn<Bank, Integer> tblColumnId;
	@FXML
	private TableColumn<Bank, String> tblColumnCode;
	@FXML
	private TableColumn<Bank, String> tblColumnName;
	@FXML
	private TableColumn<Bank, Bank> tableColumnEDIT;
	@FXML
	private TableColumn<Bank, Bank> tableColumnDELETE;
	
	private ObservableList<Bank> obsList;
	
	private synchronized void loadView(Bank bank, String absoluteName, Scene scene) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox box = loader.load();
			
			BankViewRegisterController controller = loader.getController();
			controller.setBank(bank);
			controller.setBankService(new BankService());
			controller.updateFormData();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box.getChildren());
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialization();
	}
	
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalArgumentException("Serviço não instanciado");
		}
		
		List<Bank> list = service.findAll();
		obsList = FXCollections.observableList(list);
		tblView.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}


	private void initialization() {
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

	}
	
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Bank, Bank>() {
			private final Button button = new Button();
			@Override
			protected void updateItem(Bank entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				super.updateItem(entity, empty);
				if(entity == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction( e -> {
					Stage stage = Utils.getCurrentStage(e);
					stage.setTitle("Alteração de banco");
					loadView(entity, "/gui/bank/BankViewRegister.fxml", Utils.getCurrentScene(e));
				});
			}
		});
	}
	
	
	private void initRemoveButtons() {
		tableColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDELETE.setCellFactory(param -> new TableCell<Bank, Bank>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Bank entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				super.updateItem(entity, empty);
				if(entity == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}
	
	
	private void removeEntity(Bank bank) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja remover este item?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.remove(bank);
				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
}
