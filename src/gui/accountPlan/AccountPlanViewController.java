package gui.accountPlan;

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
import model.entities.AccountPlan;
import model.service.AccountPlanService;
import utils.Alerts;
import utils.Utils;

public class AccountPlanViewController implements Initializable{
	
	private AccountPlanService service;
	public void setAccountPlanService(AccountPlanService accountPlanService) {
		this.service = accountPlanService;
	}
	
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.setTitle("Cadastro de plano de conta");
		AccountPlan account = new AccountPlan();
		loadView(account, "/gui/accountPlan/AccountPlanViewRegister.fxml", stage.getScene());
	}

	@FXML
	private TableView<AccountPlan> tblAccountPlan;
	@FXML
	private TableColumn<AccountPlan, Integer> columnId;
	@FXML
	private TableColumn<AccountPlan, String> columnCredit;
	@FXML
	private TableColumn<AccountPlan, String> columnName;
	@FXML
	private TableColumn<AccountPlan, AccountPlan> columnEDIT;
	@FXML
	private TableColumn<AccountPlan, AccountPlan> columnDELETE;
	
	
	private ObservableList<AccountPlan> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
		
	}



	private void initializationNodes() {
		btnNew.setGraphic(new ImageView("/assets/icons/new16.png"));
		columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnCredit.setCellValueFactory( v -> {
			return new ReadOnlyStringWrapper(v.getValue().isCredit() ? "Crédito" : "Débito");
		});
		initEditButtons();
		initRemoveButtons();
	}
	
	
	private void loadView(AccountPlan account, String pathView, Scene scene) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(pathView));
			VBox box = loader.load();
			
			AccountPlanViewRegisterController controller = loader.getController();
			controller.setAccountPlanService(new AccountPlanService());
			controller.setAccountPlan(account);
			controller.updateFormData();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box.getChildren());
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
		
	}



	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		
		List<AccountPlan> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblAccountPlan.setItems(obsList);
	}

	
	
	private void initEditButtons() {
		columnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnEDIT.setCellFactory(param -> new TableCell<AccountPlan, AccountPlan>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(AccountPlan entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = Utils.getCurrentStage(e);
					stage.setTitle("Alteração do plano de conta");
					loadView(entity, "/gui/accountPlan/AccountPlanViewRegister.fxml", Utils.getCurrentScene(e));
				});
			}
		});
	}


	private void initRemoveButtons() {
		columnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnDELETE.setCellFactory(param -> new TableCell<AccountPlan, AccountPlan>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(AccountPlan entity, boolean empty) {
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

	private void removeEntity(AccountPlan entity) {
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
