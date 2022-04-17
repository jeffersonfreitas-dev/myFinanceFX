package gui.accountPlan;

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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import model.entities.AccountPlan;
import model.service.AccountPlanService;
import utils.Alerts;

public class AccountPlanViewController implements Initializable {

	private AccountPlanService service;

	public void setAccountPlanService(AccountPlanService accountPlanService) {
		this.service = accountPlanService;
	}
	
	private Boolean tipo = false;

	@FXML
	private Button btnNew;

	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		AccountPlan entity = new AccountPlan();
		loadModalView(entity, "Cadastro de plano de conta", stage);
	}

	@FXML
	private TableView<AccountPlan> tblView;
	@FXML
	private TableColumn<AccountPlan, Integer> tblColumnId;
	@FXML
	private TableColumn<AccountPlan, String> tblColumnCredit;
	@FXML
	private TableColumn<AccountPlan, String> tblColumnName;
	@FXML
	private TableColumn<AccountPlan, AccountPlan> tblColumnEDIT;
	@FXML
	private TableColumn<AccountPlan, AccountPlan> tblColumnDELETE;
	@FXML
	private RadioButton rdioCredit;
	@FXML
	private RadioButton rdioDebito;
	@FXML
	private ToggleGroup rdioGroup;
	@FXML
	private Label lblFiltro;

	private ObservableList<AccountPlan> obsList;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();

	}

	private void initializationNodes() {
		rdioCredit.setToggleGroup(rdioGroup);
		rdioDebito.setToggleGroup(rdioGroup);
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tblColumnCredit.setCellValueFactory(v -> {
			return new ReadOnlyStringWrapper(v.getValue().isCredit() ? "CRÉDITO" : "DÉBITO");
		});
		btnNew.getStyleClass().add("btn-primary");
		rdioDebito.setSelected(true);
	}


	public void rdioButtonFiltroClick() {
		RadioButton rb = (RadioButton)rdioGroup.getSelectedToggle();
		if(rb.getText().equalsIgnoreCase("Crédito")) {
			this.tipo = true;
		}else {
			this.tipo = false;
		}
		updateTableFiltroTipo(this.tipo);
	}
	

	public void updateTableFiltroTipo(Boolean credit) {
		setPropertiesFilter(credit);
	}

	
	public void updateTableView() {
		setPropertiesFilter(this.tipo);
		initEditButtons();
		initRemoveButtons();
	}

	
	private void setPropertiesFilter(Boolean credit) {
		if (service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}

		List<AccountPlan> list = service.findAllByType(credit);
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
	}		

	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<AccountPlan, AccountPlan>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(AccountPlan entity, boolean empty) {
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
					loadModalView(entity, "Alteração de plano de conta", stage);
				});
			}
		});
	}

	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<AccountPlan, AccountPlan>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(AccountPlan entity, boolean empty) {
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

	private synchronized <T> void loadModalView(AccountPlan entity, String title, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/accountPlan/AccountPlanViewRegister.fxml"));
			Window window = btnNew.getScene().getWindow();
			Pane pane = loader.load();

			Scene scene = new Scene(pane);
			scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(270.0);
			stage.setWidth(600.0);

			AccountPlanViewRegisterController controller = loader.getController();
			controller.setAccountPlan(entity);
			controller.setAccountPlanService(new AccountPlanService());
			controller.updateFormData();

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
}
