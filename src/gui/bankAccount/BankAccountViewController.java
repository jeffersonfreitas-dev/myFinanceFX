package gui.bankAccount;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import model.entities.BankAccount;
import model.service.BankAccountService;
import model.service.BankAgenceService;
import model.service.CompanyService;
import utils.Alerts;
import utils.Utils;

public class BankAccountViewController implements Initializable{
	
	private BankAccountService service;
	public void setBankAccountService(BankAccountService service) {
		this.service = service;
	}

	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.setTitle("Cadastro de conta bancária");
		BankAccount account = new BankAccount();
		loadView(account, "/gui/bankAccount/BankAccountViewRegister.fxml", stage.getScene());
	}

	@FXML
	private TableView<BankAccount> tblBankAccount;
	@FXML
	private TableColumn<BankAccount, Integer> columnId;
	@FXML
	private TableColumn<BankAccount, String> columnCode;
	@FXML
	private TableColumn<BankAccount, String> columnAccount;
	@FXML
	private TableColumn<BankAccount, String> columnBank;
	@FXML
	private TableColumn<BankAccount, String> columnCompany;
	@FXML
	private TableColumn<BankAccount, BankAccount> columnEDIT;
	@FXML
	private TableColumn<BankAccount, BankAccount> columnDELETE;
	
	private ObservableList<BankAccount> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}
	
	
	private void initializationNodes() {
		columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		columnAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
		columnBank.setCellValueFactory( v -> {
			String col = v.getValue().getBankAgence().getAgence() + " - " + v.getValue().getBankAgence().getBank().getName();
			return new ReadOnlyStringWrapper(col);
		});
		columnCompany.setCellValueFactory(v -> {
			return new ReadOnlyStringWrapper(v.getValue().getCompany().getName());
		});
		btnNew.setGraphic(new ImageView("/assets/icons/new16.png"));
		initEditButtons();
		initRemoveButtons();
	}

	
	public void updateTableView() {
		List<BankAccount> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblBankAccount.setItems(obsList);
	}
	
	
	private void loadView(BankAccount account, String pathView, Scene scene) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(pathView));
			VBox box = loader.load();
			
			BankAccountViewRegisterController controller = loader.getController();
			controller.setBankAccountService(new BankAccountService(), new BankAgenceService(), new CompanyService());
			controller.loadAssociateObjects();
			controller.setBankAccount(account);
			controller.updateFormData();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box.getChildren());			
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	private void initEditButtons() {
		columnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnEDIT.setCellFactory(param -> new TableCell<BankAccount, BankAccount>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(BankAccount entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = Utils.getCurrentStage(e);
					stage.setTitle("Alteração da conta bancária");
					loadView(entity, "/gui/bankAccount/BankAccountViewRegister.fxml", Utils.getCurrentScene(e));
				});
			}
		});
	}

	private void initRemoveButtons() {
		columnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnDELETE.setCellFactory(param -> new TableCell<BankAccount, BankAccount>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(BankAccount entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}

	private void removeEntity(BankAccount entity) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
				"Você tem certeza que deseja remover este item?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.remove(entity);
				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}	

}
