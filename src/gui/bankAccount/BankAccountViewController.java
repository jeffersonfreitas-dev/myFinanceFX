package gui.bankAccount;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import model.entities.BankAccount;
import model.service.BankAccountService;
import model.service.BankAgenceService;
import model.service.CompanyService;
import utils.Alerts;

public class BankAccountViewController implements Initializable{
	
	private BankAccountService service;
	public void setBankAccountService(BankAccountService service) {
		this.service = service;
	}

	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		BankAccount entity = new BankAccount();
		loadModalView(entity, "Cadastro de contas bancárias", stage);
	}

	@FXML
	private TableView<BankAccount> tblView;
	@FXML
	private TableColumn<BankAccount, Integer> tblColumnId;
	@FXML
	private TableColumn<BankAccount, String> tblColumnCode;
	@FXML
	private TableColumn<BankAccount, String> tblColumnAccount;
	@FXML
	private TableColumn<BankAccount, String> tblColumnBank;
	@FXML
	private TableColumn<BankAccount, String> tblColumnTipo;
	@FXML
	private TableColumn<BankAccount, String> tblColumnCompany;
	@FXML
	private TableColumn<BankAccount, BankAccount> tblColumnEDIT;
	@FXML
	private TableColumn<BankAccount, BankAccount> tblColumnDELETE;
	
	private ObservableList<BankAccount> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}
	
	
	private void initializationNodes() {
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tblColumnTipo.setCellValueFactory(new PropertyValueFactory<>("type"));
		tblColumnAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
		tblColumnBank.setCellValueFactory( v -> {
			String col = v.getValue().getBankAgence().getAgence() + " - " + v.getValue().getBankAgence().getBank().getName();
			return new ReadOnlyStringWrapper(col);
		});
		tblColumnCompany.setCellValueFactory(v -> {
			return new ReadOnlyStringWrapper(v.getValue().getCompany().getName());
		});
		btnNew.getStyleClass().add("btn-primary");
	}

	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalArgumentException("Serviço não instanciado");
		}
		List<BankAccount> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	
	private synchronized <T> void loadModalView(BankAccount entity, String title, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bankAccount/BankAccountViewRegister.fxml"));
			Window window = btnNew.getScene().getWindow();
			Pane pane = loader.load();	

			Scene scene = new Scene(pane);
			scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet()); 
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(380.0);
			stage.setWidth(600.0);
			
			BankAccountViewRegisterController controller = loader.getController();
			controller.setBankAccount(entity);
			controller.setBankAccountService(new BankAccountService(), new BankAgenceService(), new CompanyService());
			controller.updateFormData();
			controller.loadAssociateObjects();
			
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
	
	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<BankAccount, BankAccount>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(BankAccount entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = new Stage();
					loadModalView(entity, "Alteração de conta bancária", stage);
				});
			}
		});
	}

	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<BankAccount, BankAccount>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(BankAccount entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
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
