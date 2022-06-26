package gui.receivable;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import gui.receivement.ReceivementViewRegisterController;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import model.entities.Receivable;
import model.entities.Receivement;
import model.service.AccountPlanService;
import model.service.BankAccountService;
import model.service.CliforService;
import model.service.CompanyService;
import model.service.ReceivableService;
import model.service.ReceivementService;
import net.sf.jasperreports.engine.JRException;
import report.receivable.ReportReceivable;
import utils.Alerts;
import utils.Utils;

public class ReceivableViewController implements Initializable {

	private ReceivableService service;

	public void setReceivableService(ReceivableService service) {
		this.service = service;
	}

	private String status = "RECEBER";

	@FXML
	private Button btnNew;

	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		Receivable entity = new Receivable();
		loadModalView("/gui/receivable/ReceivableViewRegister.fxml", 600.0, 585.0, entity,
				"Cadastro de contas a receber", stage, (ReceivableViewRegisterController controller) -> {
					controller.setReceivableService(new ReceivableService());
					controller.setCompanyService(new CompanyService());
					controller.setCliforService(new CliforService());
					controller.setAccountPlanService(new AccountPlanService());
					controller.setReceivable(entity);
					controller.loadAssociateObjects();
					controller.updateFormData();
				});
	}

	@FXML
	private TableView<Receivable> tblView;
	@FXML
	private TableColumn<Receivable, Date> tblColumnDate;
	@FXML
	private TableColumn<Receivable, Date> tblColumnDueDate;
	@FXML
	private TableColumn<Receivable, String> tblColumnHistoric;
	@FXML
	private TableColumn<Receivable, String> tblColumnProvider;
	@FXML
	private TableColumn<Receivable, Double> tblColumnValue;
	@FXML
	private TableColumn<Receivable, Receivable> tblColumnEDIT;
	@FXML
	private TableColumn<Receivable, Receivable> tblColumnDELETE;
	@FXML
	private TableColumn<Receivable, Receivable> tblColumnHISTORIC;
	@FXML
	private RadioButton rdioRecebida;
	@FXML
	private RadioButton rdioReceber;
	@FXML
	private ToggleGroup rdioGroup;

	private ObservableList<Receivable> obsList = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	private void initializationNodes() {
		rdioReceber.setToggleGroup(rdioGroup);
		rdioRecebida.setToggleGroup(rdioGroup);
		btnNew.getStyleClass().add("btn-primary");
		tblColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(tblColumnDate, "dd/MM/yyyy");
		tblColumnDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		Utils.formatTableColumnDate(tblColumnDueDate, "dd/MM/yyyy");
		tblColumnHistoric.setCellValueFactory(new PropertyValueFactory<>("historic"));
		tblColumnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		Utils.formatTableColumnDouble(tblColumnValue, 2);
		tblColumnProvider.setCellValueFactory(v -> {
			String s = v.getValue().getClifor().getName();
			return new ReadOnlyStringWrapper(s);
		});

		rdioReceber.setSelected(true);

		// METODO DOIS CLICKES ROW TABELA
		tblView.setRowFactory(tv -> {
			TableRow<Receivable> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Receivable entity = row.getItem();

					if (entity.getStatus().equals("RECEBIDA")) {
						Alerts.showAlert("Erro ao abrir recebimento", null, "Está conta já foi recebida",
								AlertType.ERROR);
					} else {
						Stage stage = new Stage();
						loadModalView("/gui/receivement/ReceivementViewRegister.fxml", 600.0, 350.0, entity,
								"Recebimento de contas", stage, (ReceivementViewRegisterController controller) -> {
									controller.setReceivableService(new ReceivableService());
									controller.setAccountService(new BankAccountService());
									controller.setService(new ReceivementService());
									controller.setReceivement(new Receivement());
									controller.setReceivable(entity);
									controller.loadAssociateObjects();
								});
					}
				}
			});
			return row;
		});
	}

	public void rdioButtonFiltroClick() {
		RadioButton rb = (RadioButton) rdioGroup.getSelectedToggle();
		if (rb.getText().equalsIgnoreCase("Recebidas")) {
			this.status = "RECEBIDA";
		} else {
			this.status = "RECEBER";
		}
		updateTableFiltro(this.status);
	}

	private void updateTableFiltro(String status) {
		if (service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}

		List<Receivable> list = service.filtro(status);
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<Receivable> list = service.filtro(status);
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
		initializationNodes();
		initRemoveButtons();
		initEditButtons();
		initHistoricButtons();
	}

	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<Receivable, Receivable>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(Receivable entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if (entity == null || entity.getStatus().equals("RECEBIDA")) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = new Stage();
					loadModalView("/gui/receivable/ReceivableViewRegister.fxml", 600.0, 585.0, entity,
							"Cadastro de contas a receber", stage, (ReceivableViewRegisterController controller) -> {
								controller.setReceivableService(new ReceivableService());
								controller.setCompanyService(new CompanyService());
								controller.setCliforService(new CliforService());
								controller.setAccountPlanService(new AccountPlanService());
								controller.setReceivable(entity);
								controller.loadAssociateObjects();
								controller.updateFormData();
							});
				});
			}
		});
	}

	private void initHistoricButtons() {
		tblColumnHISTORIC.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnHISTORIC.setCellFactory(param -> new TableCell<Receivable, Receivable>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(Receivable entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/detail16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					openReport(entity.getId());
				});
			}
		});
	}

	private void openReport(Integer id) {
		try {
			new ReportReceivable().showExtratoSimple(id);
		} catch (ClassNotFoundException | JRException | SQLException e1) {
			e1.printStackTrace();
		}
	}

	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<Receivable, Receivable>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(Receivable entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if (entity == null || entity.getStatus().equals("RECEBIDA")) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}

	private void removeEntity(Receivable entity) {
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

	private synchronized <T> void loadModalView(String path, Double width, Double height, Receivable entity,
			String title, Stage stage, Consumer<T> initialization) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Window window = btnNew.getScene().getWindow();
			Pane pane = loader.load();

			Scene scene = new Scene(pane);
			scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
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

}
